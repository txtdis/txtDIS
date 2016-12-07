package ph.txtdis.service;

import java.math.BigDecimal;

import ph.txtdis.exception.InvalidException;

public interface CokeReceivingService extends ReceivingService {

	default void acceptOnlyUpToBookedQty(BigDecimal qty) throws Exception {
		if (qty != null)
			try {
				if (getOriginalDetails().stream().filter(d -> d.getId().equals(getItem().getId())).findFirst().get()
						.getInitialQty().compareTo(qty) < 0)
					throw new Exception();
			} catch (Exception e) {
				e.printStackTrace();
				throw new InvalidException("Quantity exceeds booked");
			}
	}
}
