package ph.txtdis.dyvek.service;

import static java.util.stream.Collectors.toList;
import static ph.txtdis.util.NumberUtils.isPositive;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import ph.txtdis.dyvek.model.BillableDetail;
import ph.txtdis.info.Information;

@Service("clientBillAssignmentService")
public class ClientBillAssignmentServiceImpl //
		extends AbstractBillingService //
		implements ClientBillAssignmentService {

	@Override
	public String getBillActedByBy() {
		return get().getBillActedBy();
	}

	@Override
	public ZonedDateTime getBillActedOn() {
		return get().getBillActedOn();
	}

	@Override
	public String getCustomer() {
		return get().getVendor();
	}

	@Override
	public List<BillableDetail> getDetails() {
		return get().getBookings();
	}

	@Override
	public String getHeaderName() {
		return "Delivery to Sales Assignment";
	}

	@Override
	public String getModuleName() {
		return "clientBillAssignment";
	}

	@Override
	public void save() throws Information, Exception {
		get().setCreatedBy("");
		super.save();
	}

	@Override
	public void setAdjustments(List<BigDecimal> adjustments) throws Information, Exception {
		if (adjustments == null)
			return;
		get().setAdjustmentQty(adjustments.get(0));
		get().setAdjustmentPriceValue(adjustments.get(1));
		get().setBillActedBy("");
		save();
	}

	@Override
	public void setDetails(List<BillableDetail> l) {
		if (l != null)
			l = l.stream() //
					.filter(d -> d != null && isPositive(d.getAssignedQty())) //
					.collect(toList());
		get().setBookings(l);
	}
}
