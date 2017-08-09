package ph.txtdis.dyvek.service;

import java.math.BigDecimal;

public interface SalesService //
	extends OrderService {

	BigDecimal getTolerancePercent();

	void setTolerancePercent(BigDecimal percent);
}
