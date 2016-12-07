package ph.txtdis.service;

import java.time.LocalDate;

import ph.txtdis.dto.BillableDetail;
import ph.txtdis.dto.Customer;

public interface PricedBillableService extends LatestApproved {

	BillableDetail addPriceToDetail(BillableDetail d, Customer c, LocalDate l);

	void setItemDiscountMap(Customer c, LocalDate d);
}
