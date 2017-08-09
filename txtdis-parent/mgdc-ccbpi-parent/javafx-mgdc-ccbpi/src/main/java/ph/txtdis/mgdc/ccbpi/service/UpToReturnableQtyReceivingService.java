package ph.txtdis.mgdc.ccbpi.service;

import java.math.BigDecimal;

import ph.txtdis.exception.InvalidException;

public interface UpToReturnableQtyReceivingService //
	extends ReceivingService {

	default void acceptOnlyUpToBookedQty(BigDecimal qty) throws Exception {
		if (qty != null)
			try {
				if (getPickedDetails().stream().filter(d -> d.getId().equals(getItem().getId())).findFirst().get()
					.getInitialQty().compareTo(qty) < 0)
					throw new Exception();
			} catch (Exception e) {
				throw new InvalidException("Quantity exceeds booked");
			}
	}
}
