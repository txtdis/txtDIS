package ph.txtdis.mgdc.ccbpi.domain;

import java.math.BigDecimal;

public interface ItemQuantifiedEntityDetail {

	BigDecimal getFinalQty();

	BigDecimal getInitialQty();

	ItemEntity getItem();

	BigDecimal getReturnedQty();
}