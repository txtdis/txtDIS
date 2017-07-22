package ph.txtdis.dto;

import java.math.BigDecimal;

public interface ReceivingDetail //
		extends Keyed<Long> {

	String getItemName();

	BigDecimal getInitialQty();

	BigDecimal getReturnedQty();

	void setReturnedQty(BigDecimal qty);
}
