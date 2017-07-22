package ph.txtdis.mgdc.gsm.service;

import java.math.BigDecimal;

import ph.txtdis.dto.Billable;

public interface GsmBillingService //
		extends AdjustableBillingService, BillingService, EditableBillingNoService, VerifiedLoadOrderService {

	boolean findUncorrectedInvalidBilling();

	Billable findUnvalidatedCorrectedBilling();

	BigDecimal getAdjustment();

	boolean setUncorrectedInvalidBilling(Billable b);
}
