package ph.txtdis.service;

import java.time.LocalDate;

import ph.txtdis.dto.Billable;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.OpenPickedLoadOrderException;

public interface PickedLoadOrderVerified {

	ReadOnlyService<Billable> getBillableReadOnlyService();

	default void verifyAllPickedLoadOrdersHaveNoItemQuantityVariances(String seller, LocalDate date) throws Exception {
		Billable b = getBillableReadOnlyService().module("loadOrder")
				.getOne("/openLoadOrder?seller=" + seller + "&upTo=" + date);
		if (b != null)
			throw new OpenPickedLoadOrderException(b.getBookingId());
	}

	default void verifyPickedLoadOrderCanBeDRdBecauseOfItemShortages(Long loadOrderId) throws Exception {
		Billable b = getBillableReadOnlyService().module("loadOrder").getOne("/shortLoadOrder?id=" + loadOrderId);
		if (b == null)
			throw new InvalidException("A D/R can only be used for an L/O\nafter an R/R showed items fell short");
	}

	default void verifyPickedLoadOrderCanBeInvoiced(Billable b) throws Exception {
		if (b.getReceivedOn() != null)
			throw new InvalidException("L/O No. " + b.getBookingId() + " has an R/R\nthus, is closed.");
	}
}
