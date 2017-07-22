package ph.txtdis.dyvek.service.server;

import static java.math.BigDecimal.ZERO;
import static java.util.stream.Collectors.toList;
import static ph.txtdis.type.PartnerType.OUTLET;
import static ph.txtdis.util.NumberUtils.nullIfZero;
import static ph.txtdis.util.NumberUtils.zeroIfNull;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dyvek.domain.BillableEntity;
import ph.txtdis.dyvek.domain.BillableReferenceEntity;
import ph.txtdis.dyvek.domain.DeliveryDetailEntity;
import ph.txtdis.dyvek.domain.RemittanceEntity;
import ph.txtdis.dyvek.model.Billable;
import ph.txtdis.dyvek.model.BillableDetail;
import ph.txtdis.dyvek.repository.VendorBillRepository;

@Service("vendorBillService")
public class VendorBillServiceImpl //
		extends AbstractBillService<VendorBillRepository> //
		implements VendorBillService {

	@Autowired
	private DeliveryService deliveryService;

	@Override
	protected BigDecimal adjustmentPrice(DeliveryDetailEntity d) {
		return d == null ? ZERO : zeroIfNull(d.getSoaAdjustmentPriceValue());
	}

	@Override
	protected BigDecimal adjustmentQty(DeliveryDetailEntity d) {
		return d == null ? ZERO : zeroIfNull(d.getSoaAdjustmentQty());
	}

	@Override
	protected List<BillableEntity> deliveries(BillableEntity e) {
		return deliveryService.findAllByVendorPurchaseNo(e.getCustomer().getId(), e.getOrderNo());
	}

	@Override
	public List<Billable> findAllOpen() {
		List<BillableEntity> l = repository.findByDeliveryNotNullAndDeliveryAssignedToPurchaseOnNullOrderByOrderDateAsc();
		return l == null ? null : l.stream().map(e -> toBillable(e)).collect(toList());
	}

	@Override
	protected BillableEntity firstEntity() {
		return repository.findFirstByDeliveryAssignedToPurchaseOnNotNullOrderByIdAsc();
	}

	@Override
	protected BillableEntity nextEntity(Long id) {
		return repository.findFirstByDeliveryAssignedToPurchaseOnNotNullAndIdGreaterThanOrderByIdAsc(id);
	}

	@Override
	protected BillableEntity lastEntity() {
		return repository.findFirstByDeliveryAssignedToPurchaseOnNotNullOrderByIdDesc();
	}

	@Override
	protected BillableEntity previousEntity(Long id) {
		return repository.findFirstByDeliveryAssignedToPurchaseOnNotNullAndIdLessThanOrderByIdDesc(id);
	}

	@Override
	public List<Billable> search(String dr) {
		List<BillableEntity> l = repository.findByDeliveryAssignedToPurchaseOnNotNullAndOrderNoContainingIgnoreCase(dr);
		return toModels(l);
	}

	@Override
	protected Billable setCreationData(Billable b, BillableEntity e) {
		DeliveryDetailEntity d = e.getDelivery();
		if (d != null) {
			b.setCreatedBy(d.getAssignedToPurchaseBy());
			b.setCreatedOn(d.getAssignedToPurchaseOn());
		}
		return b;
	}

	@Override
	protected Billable toBillable(BillableEntity e) {
		Billable b = super.toBillable(e);
		b.setClient(client(e));
		b.setTotalQty(e.getTotalQty());
		return b;
	}

	@Override
	protected BillableDetail toDetail(BillableEntity e) {
		BillableDetail d = super.toDetail(e);
		d.setQty(balanceQty(e));
		return d;
	}

	@Override
	protected BillableDetail toDetail(BillableReferenceEntity r) {
		BillableEntity e = r.getReference();
		if (e == null || e.getCustomer() == null || e.getCustomer().getType() == OUTLET)
			return null;
		BillableDetail d = toDetail(e);
		d.setAssignedQty(r.getQty());
		return d;
	}

	@Override
	public Billable toModel(BillableEntity e) {
		Billable b = super.toModel(e);
		if (b == null)
			return null;
		b.setPurchases(purchases(b, e));
		b = setBillingData(b, e);
		return setPaymentData(b, e);
	}

	private String client(BillableEntity b) {
		try {
			return b.getDelivery().getRecipient().getName();
		} catch (Exception e) {
			return null;
		}
	}

	private List<BillableDetail> purchases(Billable b, BillableEntity e) {
		return b.getCreatedOn() == null ? unassignedPurchases(e) : assignedPurchases(e);
	}

	private List<BillableDetail> unassignedPurchases(BillableEntity e) {
		List<BillableEntity> l = repository.findByDeliveryNullAndOrderClosedOnNullAndCustomerOrderByOrderDateAsc(e.getCustomer());
		return l.stream() //
				.map(b -> toDetail(b)) //
				.filter(d -> d != null) //
				.collect(toList());
	}

	private List<BillableDetail> assignedPurchases(BillableEntity e) {
		return e.getReferences().stream() //
				.map(r -> toDetail(r)) //
				.filter(d -> d != null) //
				.collect(toList());
	}

	private Billable setBillingData(Billable b, BillableEntity e) {
		DeliveryDetailEntity d = e.getDelivery();
		if (d != null && d.getSoaReceivedOn() != null) {
			b.setBillNo(d.getSoaNo());
			b.setBillDate(d.getSoaDate());
			b.setBillActedBy(d.getSoaReceivedBy());
			b.setBillActedOn(d.getSoaReceivedOn());
		}
		return b;
	}

	private Billable setPaymentData(Billable b, BillableEntity e) {
		RemittanceEntity r = remitService.findEntityByBillingId(e.getId());
		if (r == null)
			return b;
		b.setBank(r.getDrawnFrom().getName());
		b.setCheckDate(r.getPaymentDate());
		b.setCheckId(r.getCheckId());
		b.setPaymentActedBy(r.getCreatedBy());
		b.setPaymentActedOn(r.getCreatedOn());
		return b;
	}

	@Override
	public BillableEntity update(Billable b) {
		BillableEntity e = super.update(b);
		DeliveryDetailEntity d = e.getDelivery();
		if (b.getCreatedBy() != null && d.getAssignedToPurchaseBy() == null)
			e = updateDeliveryAssignmentAndReferences(e, b, d);
		if (b.getBillNo() != null && d.getSoaNo() == null)
			e = updateDeliveryBilling(e, b, d);
		return e;
	}

	private BillableEntity updateDeliveryAssignmentAndReferences(BillableEntity e, Billable b, DeliveryDetailEntity d) {
		d.setAssignedToPurchaseBy(username());
		d.setAssignedToPurchaseOn(ZonedDateTime.now());
		e.setDelivery(d);
		return updateReferences(e, b.getPurchases());
	}

	private BillableEntity updateDeliveryBilling(BillableEntity e, Billable b, DeliveryDetailEntity d) {
		d.setSoaNo(b.getBillNo());
		d.setSoaDate(b.getBillDate());
		d.setSoaAdjustmentPriceValue(nullIfZero(b.getAdjustmentPriceValue()));
		d.setSoaAdjustmentQty(nullIfZero(b.getAdjustmentQty()));
		d.setSoaReceivedBy(username());
		d.setSoaReceivedOn(ZonedDateTime.now());
		e.setDelivery(d);
		return e;
	}
}