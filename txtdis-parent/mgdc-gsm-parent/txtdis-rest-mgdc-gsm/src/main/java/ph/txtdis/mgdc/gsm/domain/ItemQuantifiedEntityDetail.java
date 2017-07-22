package ph.txtdis.mgdc.gsm.domain;

import java.math.BigDecimal;

public interface ItemQuantifiedEntityDetail {

	BigDecimal getFinalQty();

	BigDecimal getInitialQty();

	ItemEntity getItem();

	BigDecimal getReturnedQty();
}