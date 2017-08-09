package ph.txtdis.mgdc.gsm.service;

import ph.txtdis.dto.BillableDetail;
import ph.txtdis.mgdc.gsm.dto.Customer;
import ph.txtdis.service.LatestApproved;

import java.time.LocalDate;

public interface PricedBillableService //
	extends LatestApproved {

	BillableDetail addPriceToDetail(boolean canAvailDiscount, BillableDetail d, Customer c, LocalDate l);

	void setItemDiscountMap(Customer c, LocalDate d);
}
