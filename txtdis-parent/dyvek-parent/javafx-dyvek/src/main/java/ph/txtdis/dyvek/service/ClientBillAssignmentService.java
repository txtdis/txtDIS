package ph.txtdis.dyvek.service;

import java.math.BigDecimal;
import java.util.List;

import ph.txtdis.info.Information;

public interface ClientBillAssignmentService //
		extends BillingService {

	void setAdjustments(List<BigDecimal> adjustments) throws Information, Exception;
}
