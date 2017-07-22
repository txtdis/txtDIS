package ph.txtdis.mgdc.service;

import java.time.LocalDate;

import ph.txtdis.dto.Billable;
import ph.txtdis.exception.UnbilledPickedSalesOrderException;
import ph.txtdis.exception.UnpickedBookingException;
import ph.txtdis.service.ReadOnlyService;

public interface VerifiedSalesOrderService {

	default void verifyAllPickedSalesOrderHaveBeenBilled(ReadOnlyService<Billable> readOnlyservice, LocalDate date) throws Exception {
		Billable b = findSalesOrder(readOnlyservice, "unbilled", date);
		if (b != null)
			throw new UnbilledPickedSalesOrderException(b.getBookingId());
	}

	default Billable findSalesOrder(ReadOnlyService<Billable> readOnlyservice, String path, LocalDate date) throws Exception {
		return readOnlyservice.module("salesOrder").getOne("/" + path + "?upTo=" + date);
	}

	default void verifyAllSalesOrderHaveBeenPicked(ReadOnlyService<Billable> readOnlyservice, LocalDate date) throws Exception {
		Billable b = findSalesOrder(readOnlyservice, "unpicked", date);
		if (b != null)
			throw new UnpickedBookingException("S/O", b.getBookingId().toString());
	}
}
