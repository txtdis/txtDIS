package ph.txtdis.mgdc.gsm.service;

import ph.txtdis.dto.Billable;
import ph.txtdis.mgdc.gsm.dto.Customer;

import java.math.BigDecimal;

public interface AdjustableBillingService {

	Billable setAdjustmentData(Long id, BigDecimal value);

	Customer verifyIsAnEmployee(Long id) throws Exception;
}
