package ph.txtdis.dyvek.service;

import static java.util.stream.Collectors.toList;
import static ph.txtdis.util.DateTimeUtils.toDate;
import static ph.txtdis.util.NumberUtils.isPositive;
import static ph.txtdis.util.NumberUtils.toBigDecimal;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import ph.txtdis.dto.Remittance;
import ph.txtdis.dyvek.model.BillableDetail;
import ph.txtdis.info.Information;

@Service("vendorBillingService")
public class VendorBillingServiceImpl //
		extends AbstractBillingService //
		implements VendorBillingService {

	@Override
	public String getCustomer() {
		return get().getVendor();
	}

	@Override
	public List<BillableDetail> getDetails() {
		return get().getPurchases();
	}

	@Override
	public String getHeaderName() {
		return "Supplier Billing";
	}

	@Override
	public String getModuleName() {
		return "vendorBill";
	}

	@Override
	public String getPaymentActedBy() {
		return get().getPaymentActedBy();
	}

	@Override
	public ZonedDateTime getPaymentActedOn() {
		return get().getPaymentActedOn();
	}

	@Override
	public void save() throws Information, Exception {
		get().setCreatedBy("");
		super.save();
	}

	@Override
	public void setBillData(List<String> billData) throws Information, Exception {
		if (billData == null)
			return;
		get().setBillNo(billData.get(0));
		get().setBillDate(toDate(billData.get(1)));
		get().setAdjustmentQty(toBigDecimal(billData.get(2)));
		get().setAdjustmentPriceValue(toBigDecimal(billData.get(3)));
		save();
	}

	@Override
	public void setDetails(List<BillableDetail> l) {
		if (l != null)
			l = l.stream() //
					.filter(d -> d != null && isPositive(d.getAssignedQty())) //
					.collect(toList());
		get().setPurchases(l);
	}

	@Override
	public void setPaymentData(List<Remittance> paymentData) throws Information, Exception {
		if (paymentData == null)
			return;
		Remittance r = paymentData.get(0);
		get().setBank(r.getDraweeBank());
		get().setCheckDate(r.getPaymentDate());
		get().setCheckId(r.getCheckId());
		get().setPaymentActedBy(r.getCreatedBy());
		get().setPaymentActedOn(r.getCreatedOn());
		save();
	}
}
