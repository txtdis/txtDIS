package ph.txtdis.mgdc.ccbpi.service;

import java.math.BigDecimal;

public interface RemittanceVarianceService //
		extends SellerTrackedVarianceService {

	String getCollector();

	BigDecimal getLoadOutValue();

	BigDecimal getReturnedValue();

	BigDecimal getRemittanceVarianceValue();

	BigDecimal getRemittedValue();

	void setCollector(String name);
}