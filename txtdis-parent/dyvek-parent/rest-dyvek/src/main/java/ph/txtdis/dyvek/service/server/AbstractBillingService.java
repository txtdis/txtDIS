package ph.txtdis.dyvek.service.server;

import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.dyvek.domain.BillableEntity;
import ph.txtdis.dyvek.domain.BillableReferenceEntity;
import ph.txtdis.dyvek.domain.DeliveryDetailEntity;
import ph.txtdis.dyvek.model.Billable;
import ph.txtdis.dyvek.model.BillableDetail;
import ph.txtdis.repository.SpunRepository;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static java.util.stream.Collectors.toList;
import static ph.txtdis.type.PartnerType.OUTLET;
import static ph.txtdis.util.NumberUtils.zeroIfNull;

public abstract class AbstractBillingService<R extends SpunRepository<BillableEntity, Long>> //
	extends AbstractSpunSavedBillableService<R> {

	@Override
	protected Billable toBillable(BillableEntity e) {
		Billable b = super.toBillable(e);
		b.setDeliveryNo(e.getOrderNo());
		return b;
	}

	@Override
	public Billable toModel(BillableEntity e) {
		Billable b = super.toModel(e);
		return b == null ? null : setAdjustmentData(b, e);
	}

	private Billable setAdjustmentData(Billable b, BillableEntity e) {
		DeliveryDetailEntity d = e.getDelivery();
		b.setAdjustmentPriceValue(adjustmentPrice(d));
		b.setAdjustmentQty(adjustmentQty(d));
		b.setBillActedBy(billAdjustedBy(d));
		b.setBillActedOn(billAdjustedOn(d));
		return b;
	}

	protected abstract BigDecimal adjustmentPrice(DeliveryDetailEntity d);

	protected abstract BigDecimal adjustmentQty(DeliveryDetailEntity d);

	private String billAdjustedBy(DeliveryDetailEntity d) {
		return d == null ? null : d.getBillAdjustedBy();
	}

	private ZonedDateTime billAdjustedOn(DeliveryDetailEntity d) {
		return d == null ? null : d.getBillAdjustedOn();
	}

	@Override
	protected BigDecimal totalValue(BillableEntity b) {
		try {
			return b.getReferences().stream() //
				.map(r -> r.getQty().multiply(price(r))) //
				.reduce(ZERO, BigDecimal::add);
		} catch (Exception e) {
			return ZERO;
		}
	}

	private BigDecimal price(BillableReferenceEntity r) {
		try {
			return zeroIfNull(r.getReference().getOrder().getPriceValue());
		} catch (Exception e) {
			return ZERO;
		}
	}

	BillableEntity updateReferences(BillableEntity e, List<BillableDetail> l) {
		e.setReferences(l == null ? null //
			: l.stream() //
			.map(d -> toReference(d, e)) //
			.filter(d -> d != null && d.getReference() != null) //
			.collect(toList()));
		return e;
	}

	private BillableReferenceEntity toReference(BillableDetail d, BillableEntity b) {
		BillableReferenceEntity r = new BillableReferenceEntity();
		r.setReference(findEntityByPrimaryKey(d.getId()));
		r.setDelivery(b);
		r.setQty(d.getAssignedQty());
		return r;
	}

	protected BillableDetail toDetail(BillableReferenceEntity r) {
		BillableEntity e = r.getReference();
		if (e == null || e.getCustomer() == null || e.getCustomer().getType() == OUTLET)
			return null;
		BillableDetail d = toDetail(e);
		d.setAssignedQty(r.getQty());
		return d;
	}
}