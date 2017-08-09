package ph.txtdis.mgdc.gsm.service;

import ph.txtdis.dto.Billable;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.OpenPickedLoadOrderException;
import ph.txtdis.exception.UnpickedBookingException;
import ph.txtdis.service.RestClientService;

import java.time.LocalDate;

public interface VerifiedLoadOrderService {

	default void verifyAllLoadOrdersHaveBeenPicked(RestClientService<Billable> readOnlyservice, LocalDate date)
		throws Exception {
		Billable b = findLoadOrder(readOnlyservice, "/unpicked?upTo=" + date);
		if (b != null)
			throw new UnpickedBookingException("L/O", b.getBookingId().toString());
	}

	default Billable findLoadOrder(RestClientService<Billable> readOnlyservice, String endPt) throws Exception {
		return readOnlyservice.module("loadOrder").getOne(endPt);
	}

	default void verifyAllPickedLoadOrdersHaveNoItemQuantityVariances(RestClientService<Billable> readOnlyservice,
	                                                                  LocalDate date) throws Exception {
		Billable b = findLoadOrder(readOnlyservice, "/loadVariance?upTo=" + date);
		if (b != null)
			throw new OpenPickedLoadOrderException(b.getBookingId());
	}

	default void verifyPickedLoadOrderCanBeDRdBecauseOfItemShortages(RestClientService<Billable> readOnlyservice,
	                                                                 Long loadOrderId) throws Exception {
		Billable b = findLoadOrder(readOnlyservice, "/shortage?id=" + loadOrderId);
		if (b == null)
			throw new InvalidException(
				"A D/R can only be used for an L/O\nafter an R/R showed item quantities fell short");
	}

	default void verifyPickedLoadOrderCanBeInvoiced(Billable b) throws Exception {
		if (b.getReceivedOn() != null)
			throw new InvalidException("L/O No. " + b.getBookingId() + " has an R/R\nthus, is closed.");
	}
}
