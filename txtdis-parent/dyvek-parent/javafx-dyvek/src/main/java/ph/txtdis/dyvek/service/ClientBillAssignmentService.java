package ph.txtdis.dyvek.service;

import ph.txtdis.info.Information;

import java.math.BigDecimal;
import java.util.List;

public interface ClientBillAssignmentService //
	extends BillingService {

	void setAdjustments(List<BigDecimal> adjustments) throws Information, Exception;
}
