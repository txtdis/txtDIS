package ph.txtdis.dyvek.service.server;

import org.springframework.stereotype.Service;
import ph.txtdis.dyvek.domain.*;
import ph.txtdis.dyvek.model.Billable;
import ph.txtdis.dyvek.model.BillableDetail;
import ph.txtdis.dyvek.repository.ClientBillAssignmentRepository;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

import static java.math.BigDecimal.ZERO;
import static java.util.stream.Collectors.toList;
import static ph.txtdis.type.PartnerType.OUTLET;
import static ph.txtdis.util.NumberUtils.*;
import static ph.txtdis.util.UserUtils.username;

@Service("clientBillAssignmentService")
public class ClientBillingAssignmentServiceImpl
	extends AbstractBillingService<ClientBillAssignmentRepository>
	implements ClientBillAssignmentService {

	@Override
	protected BigDecimal adjustmentPrice(DeliveryDetailEntity d) {
		return d == null ? ZERO : zeroIfNull(d.getBillAdjustmentPriceValue());
	}

	@Override
	protected BigDecimal adjustmentQty(DeliveryDetailEntity d) {
		return d == null ? ZERO : zeroIfNull(d.getBillAdjustmentQty());
	}

	@Override
	public List<Billable> findAllOpen() {
		List<BillableEntity> l = repository.findByDeliveryNotNullAndDeliveryAssignedToSalesOnNullOrderByOrderDateAsc();
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
		return repository.findFirstByDeliveryAssignedToSalesOnNotNullOrderByIdAsc();
	}

	@Override
	protected BillableEntity nextEntity(Long id) {
		return repository.findFirstByDeliveryAssignedToSalesOnNotNullAndIdGreaterThanOrderByIdAsc(id);
	}

	@Override
	protected BillableEntity lastEntity() {
		return repository.findFirstByDeliveryAssignedToSalesOnNotNullOrderByIdDesc();
	}

	@Override
	protected BillableEntity previousEntity(Long id) {
		return repository.findFirstByDeliveryAssignedToSalesOnNotNullAndIdLessThanOrderByIdDesc(id);
	}

	@Override
	public List<Billable> search(String dr) {
		List<BillableEntity> l = repository.findByDeliveryAssignedToSalesOnNotNullAndOrderNoContainingIgnoreCase(dr);
		return toModels(l);
	}

	@Override
	protected Billable setCreationData(Billable b, BillableEntity e) {
		DeliveryDetailEntity d = e.getDelivery();
		if (d == null)
			return null;
		b.setCreatedBy(d.getAssignedToSalesBy());
		b.setCreatedOn(d.getAssignedToSalesOn());
		return b;
	}

	@Override
	public Billable toModel(BillableEntity e) {
		Billable b = super.toModel(e);
		if (b != null)
			b.setBookings(bookings(e));
		return b;
	}

	private List<BillableDetail> bookings(BillableEntity e) {
		List<BillableReferenceEntity> l = e.getReferences();
		return salesOrderReferenceExists(l) ? referencedSalesOrders(l) : openSalesOrders(e);
	}

	private boolean salesOrderReferenceExists(List<BillableReferenceEntity> l) {
		try {
			return l.stream().anyMatch(b -> b.getReference().getCustomer().getType() == OUTLET);
		} catch (Exception e) {
			return false;
		}
	}

	private List<BillableDetail> referencedSalesOrders(List<BillableReferenceEntity> l) {
		return l.stream()
			.map(this::toDetail)
			.filter(Objects::nonNull)
			.collect(toList());
	}

	private List<BillableDetail> openSalesOrders(BillableEntity e) {
		List<BillableEntity> l = repository
			.findByDeliveryNullAndItemAndOrderNotNullAndOrderClosedOnNullAndCustomerOrderByOrderDateAsc(
				e.getItem(),
				e.getDelivery().getRecipient());
		return l == null ? null : l.stream()
			.map(this::toDetail)
			.filter(Objects::nonNull)
			.collect(toList());
	}

	@Override
	protected BillableDetail toDetail(BillableEntity e) {
		BillableDetail d = super.toDetail(e);
		d.setQty(balanceQty(e));
		return d;
	}

	@Override
	protected BigDecimal balanceQty(BillableEntity b) {
		return super.balanceQty(b).add(toleranceQty(b));
	}

	private BigDecimal toleranceQty(BillableEntity b) {
		OrderDetailEntity order = b.getOrder();
		BigDecimal percentTolerance = order.getTolerancePercent();
		BigDecimal orderedQty = b.getTotalQty();
		return orderedQty.multiply(toPercentRate(percentTolerance));
	}

	@Override
	public BillableEntity update(Billable b) {
		BillableEntity e = super.update(b);
		DeliveryDetailEntity d = e.getDelivery();
		if (b.getCreatedBy() != null && d.getAssignedToSalesBy() == null)
			return updateDeliveryAssignmentAndReferences(e, b, d);
		if (b.getBillActedBy() != null && d.getBillAdjustedBy() == null)
			return updateAdjustmentData(e, b, d);
		return e;
	}

	private BillableEntity updateDeliveryAssignmentAndReferences(BillableEntity e, Billable b, DeliveryDetailEntity d) {
		d.setBillAdjustmentPriceValue(nullIfZero(b.getAdjustmentPriceValue()));
		d.setBillAdjustmentQty(nullIfZero(b.getAdjustmentQty()));
		d.setAssignedToSalesBy(username());
		d.setAssignedToSalesOn(ZonedDateTime.now());
		e.setDelivery(d);
		return updateReferences(e, b.getBookings());
	}

	private BillableEntity updateAdjustmentData(BillableEntity e, Billable b, DeliveryDetailEntity d) {
		d.setBillAdjustmentQty(b.getAdjustmentQty());
		d.setBillAdjustmentPriceValue(b.getAdjustmentPriceValue());
		d.setBillAdjustedBy(username());
		d.setBillAdjustedOn(ZonedDateTime.now());
		e.setDelivery(d);
		return e;
	}

	@Override
	protected List<BillableEntity> deliveries(BillableEntity e) {
		String salesNo = e.getOrderNo();
		CustomerEntity client = e.getCustomer();
		return salesNo == null || client == null ? null :
			repository.findByReferencesReferenceCustomerAndReferencesReferenceOrderNo(client, salesNo);
	}
}