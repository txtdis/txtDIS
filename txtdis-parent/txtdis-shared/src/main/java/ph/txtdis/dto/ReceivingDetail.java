package ph.txtdis.dto;

import java.math.BigDecimal;

public interface ReceivingDetail {

	Long getId();

	String getItemName();

	BigDecimal getInitialQty();

	BigDecimal getReturnedQty();

	void setReturnedQty(BigDecimal qty);
}
