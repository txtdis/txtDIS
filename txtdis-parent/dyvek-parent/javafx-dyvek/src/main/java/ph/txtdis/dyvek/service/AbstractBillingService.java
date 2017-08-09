package ph.txtdis.dyvek.service;

import ph.txtdis.dyvek.model.BillableDetail;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static ph.txtdis.util.NumberUtils.isZero;
import static ph.txtdis.util.NumberUtils.zeroIfNull;

public abstract class AbstractBillingService
	extends AbstractOrderService<VendorService>
	implements BillingService {

	@Override
	public boolean areAssignedAndDeliverQtyDifferent() {
		return !isZero(getQty().subtract(totalAssignedQty()));
	}

	private BigDecimal totalAssignedQty() {
		return getDetails().stream()
			.map(BillableDetail::getAssignedQty)
			.reduce(ZERO, BigDecimal::add);
	}

	@Override
	public String getAlternateName() {
		return "D/R";
	}

	@Override
	public String getBank() {
		return get().getBank();
	}

	@Override
	public String getBillActedByBy() {
		return get().getBillActedBy();
	}

	@Override
	public ZonedDateTime getBillActedOn() {
		return get().getBillActedOn();
	}

	@Override
	public LocalDate getCheckDate() {
		return get().getCheckDate();
	}

	@Override
	public Long getCheckId() {
		return get().getCheckId();
	}

	@Override
	public LocalDate getBillDate() {
		return get().getBillDate();
	}

	@Override
	public String getBillNo() {
		return get().getBillNo();
	}

	@Override
	public String getCustomer() {
		return get().getClient();
	}

	@Override
	public void setCustomer(String name) {
		get().setVendor(name);
	}

	@Override
	public BigDecimal getNetValue() {
		return getTotalValue().subtract(getAdjustmentValue());
	}

	@Override
	public BigDecimal getTotalValue() {
		return zeroIfNull(get().getTotalValue());
	}

	@Override
	public BigDecimal getAdjustmentValue() {
		return getAdjustmentPrice().multiply(getAdjustmentQty());
	}

	@Override
	public BigDecimal getAdjustmentPrice() {
		return zeroIfNull(get().getAdjustmentPriceValue());
	}

	@Override
	public BigDecimal getAdjustmentQty() {
		return zeroIfNull(get().getAdjustmentQty());
	}

	@Override
	public String getOrderNo() {
		return get().getDeliveryNo();
	}

	@Override
	public List<String> listCustomers() {
		return null;
	}

	@Override
	public void setOrderNoUponValidation(String no) throws Exception {
	}

	@Override
	public void updateTotals(List<BillableDetail> items) {
		get().setTotalValue(items
			.stream()
			.map(d -> zeroIfNull(d.getAssignedQty()).multiply(zeroIfNull(d.getPriceValue())))
			.reduce(ZERO, BigDecimal::add));
	}
}
