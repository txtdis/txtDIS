package ph.txtdis.dyvek.service.server;

import org.springframework.stereotype.Service;
import ph.txtdis.dyvek.domain.BillableEntity;
import ph.txtdis.dyvek.domain.DeliveryDetailEntity;
import ph.txtdis.dyvek.domain.RemittanceEntity;
import ph.txtdis.dyvek.model.Billable;
import ph.txtdis.dyvek.model.BillableDetail;
import ph.txtdis.dyvek.repository.VendorBillRepository;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

import static java.math.BigDecimal.ZERO;
import static java.util.stream.Collectors.toList;
import static ph.txtdis.util.NumberUtils.nullIfZero;
import static ph.txtdis.util.NumberUtils.zeroIfNull;
import static ph.txtdis.util.UserUtils.username;

@Service("vendorBillService")
public class VendorBillingServiceImpl
	extends AbstractBillingService<VendorBillRepository>
	implements VendorBillingService {

	private final DeliveryService deliveryService;

	private final DyvekRemittanceService remitService;

	public VendorBillingServiceImpl(DeliveryService deliveryService, DyvekRemittanceService remitService) {
		this.deliveryService = deliveryService;
		this.remitService = remitService;
	}

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
		List<BillableEntity> l = repository
			.findByDeliveryNotNullAndDeliveryAssignedToPurchaseOnNullOrderByOrderDateAsc();
		return l == null ? null : l.stream().map(this::toBillable).collect(toList());
	}

	@Override
	protected Billable toBillable(BillableEntity e) {
		Billable b = super.toBillable(e);
		b.setClient(client(e));
		b.setTotalQty(e.getTotalQty());
		return b;
	}

	private String client(BillableEntity b) {
		try {
			return b.getDelivery().getRecipient().getName();
		} catch (Exception e) {
			return null;
		}
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
	public Billable toModel(BillableEntity e) {
		Billable b = super.toModel(e);
		if (b == null)
			return null;
		b.setPurchases(purchases(b, e));
		DeliveryDetailEntity d = e.getDelivery();
		b = setDeliveryData(b, d);
		b = setQualityData(b, d);
		if (d.getSoaReceivedOn() != null)
			b = setBillingData(b, d);
		return setPaymentData(b, e);
	}

	private List<BillableDetail> purchases(Billable b, BillableEntity e) {
		return b.getCreatedOn() == null ? unassignedPurchases(e) : assignedPurchases(e);
	}

	private List<BillableDetail> unassignedPurchases(BillableEntity e) {
		List<BillableEntity> l =
			repository
				.findByDeliveryNullAndOrderClosedOnNullAndCustomerAndItemOrderByOrderDateAsc(e.getCustomer(), e.getItem());
		return l.stream()
			.map(this::toDetail)
			.filter(Objects::nonNull)
			.collect(toList());
	}

	private List<BillableDetail> assignedPurchases(BillableEntity e) {
		return e.getReferences().stream()
			.map(this::toDetail)
			.filter(Objects::nonNull)
			.collect(toList());
	}

	private Billable setDeliveryData(Billable b, DeliveryDetailEntity d) {
		b.setTruckPlateNo(d.getPlateNo());
		b.setTruckScaleNo(d.getScaleNo());
		b.setGrossWeight(d.getGrossWeight());
		return b;
	}

	private Billable setQualityData(Billable b, DeliveryDetailEntity d) {
		b.setFfaPercent(d.getFfa());
		b.setIodineQty(d.getIodine());
		b.setColor(d.getColor());
		return b;
	}

	private Billable setBillingData(Billable b, DeliveryDetailEntity d) {
		b.setBillNo(d.getSoaNo());
		b.setBillDate(d.getSoaDate());
		b.setBillActedBy(d.getSoaReceivedBy());
		b.setBillActedOn(d.getSoaReceivedOn());
		return b;
	}

	private Billable setPaymentData(Billable b, BillableEntity e) {
		List<RemittanceEntity> l = remitService.findEntitiesByBillingId(e.getId());
		return l.isEmpty() ? b : setPaymentData(b, l);
	}

	private Billable setPaymentData(Billable b, List<RemittanceEntity> l) {
		b = setCheckPayment(b, l);
		return l.size() == 1 ? b : setCashAdvanceLiquidation(b, l);
	}

	private Billable setCheckPayment(Billable b, List<RemittanceEntity> l) {
		RemittanceEntity r = l.get(0);
		b.setBank(r.getDrawnFrom().getName());
		b.setCheckDate(r.getPaymentDate());
		b.setCheckId(r.getCheckId());
		b.setPaymentValue(r.getValue());
		b.setPaymentActedBy(r.getCreatedBy());
		b.setPaymentActedOn(r.getCreatedOn());
		return b;
	}

	private Billable setCashAdvanceLiquidation(Billable b, List<RemittanceEntity> l) {
		RemittanceEntity r = l.get(1);
		b.setCashAdvanceDate(r.getPaymentDate());
		b.setCashAdvanceId(r.getCheckId());
		b.setCashAdvanceValue(r.getValue());
		return b;
	}

	@Override
	protected BillableDetail toDetail(BillableEntity e) {
		BillableDetail d = super.toDetail(e);
		d.setQty(balanceQty(e));
		return d;
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