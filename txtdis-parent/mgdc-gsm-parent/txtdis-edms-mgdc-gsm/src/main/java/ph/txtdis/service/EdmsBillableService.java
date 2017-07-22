package ph.txtdis.service;

import java.util.List;

import ph.txtdis.domain.EdmsInvoice;
import ph.txtdis.domain.EdmsLoadOrder;
import ph.txtdis.dto.Billable;

public interface EdmsBillableService //
		extends SavedService<Billable> {

	List<EdmsInvoice> getBillables(EdmsLoadOrder e);

	Long getBookingId(EdmsInvoice i);

	EdmsInvoice getByOrderNo(String orderNo);

	String getOrderNoFromBillingNo(String billingNo);

	List<Billable> list();
}
