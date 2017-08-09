package ph.txtdis.mgdc.service;

import ph.txtdis.dto.Billable;
import ph.txtdis.exception.UnbilledPickedSalesOrderException;
import ph.txtdis.exception.UnpickedBookingException;
import ph.txtdis.service.RestClientService;

import java.time.LocalDate;

public interface VerifiedSalesOrderService {

	default void verifyAllPickedSalesOrderHaveBeenBilled(RestClientService<Billable> readOnlyservice, LocalDate date)
		throws Exception {
		Billable b = findSalesOrder(readOnlyservice, "unbilled", date);
		if (b != null)
			throw new UnbilledPickedSalesOrderException(b.getBookingId());
	}

	default Billable findSalesOrder(RestClientService<Billable> readOnlyservice, String path, LocalDate date)
		throws Exception {
		return readOnlyservice.module("salesOrder").getOne("/" + path + "?upTo=" + date);
	}

	default void verifyAllSalesOrderHaveBeenPicked(RestClientService<Billable> readOnlyservice, LocalDate date)
		throws Exception {
		Billable b = findSalesOrder(readOnlyservice, "unpicked", date);
		if (b != null)
			throw new UnpickedBookingException("S/O", b.getBookingId().toString());
	}
}
