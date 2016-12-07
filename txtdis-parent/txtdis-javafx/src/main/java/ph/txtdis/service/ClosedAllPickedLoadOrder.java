package ph.txtdis.service;

import java.time.LocalDate;

import ph.txtdis.dto.Billable;
import ph.txtdis.exception.OpenPickedLoadOrderException;

public interface ClosedAllPickedLoadOrder {

	ReadOnlyService<Billable> getBillableReadOnlyService();

	default void verifyAllPickedLoadOrdersHaveNoItemQuantityVariances(String seller, LocalDate date) throws Exception {
		Billable b = getBillableReadOnlyService().module("loadOrder")
				.getOne("/openLoadOrder?seller=" + seller + "&upTo=" + date);
		if (b != null)
			throw new OpenPickedLoadOrderException(b.getBookingId());
	}
}
