package ph.txtdis.mgdc.ccbpi.service;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface CokeRemittanceService //
		extends PaymentOnlyRemittanceService {

	BigDecimal getTotalValue(String collector, LocalDate start, LocalDate end);
}
