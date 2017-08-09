package ph.txtdis.mgdc.gsm.service;

import ph.txtdis.dto.Billable;

import java.math.BigDecimal;

public interface GsmBillingService //
	extends AdjustableBillingService,
	BillingService,
	EditableBillingNoService,
	VerifiedLoadOrderService {

	boolean findUncorrectedInvalidBilling();

	Billable findUnvalidatedCorrectedBilling();

	BigDecimal getAdjustment();

	boolean setUncorrectedInvalidBilling(Billable b);
}
