package ph.txtdis.mgdc.service;

import ph.txtdis.dto.Billable;
import ph.txtdis.dto.Remittance;
import ph.txtdis.dto.RemittanceDetail;
import ph.txtdis.service.PaymentDetailedRemittanceService;
import ph.txtdis.service.RemittanceService;
import ph.txtdis.type.BillingType;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

public interface AdjustableInputtedPaymentDetailedRemittanceService //
	extends PaymentDetailedRemittanceService,
	RemittanceService {

	RemittanceDetail createDetail(Billable b, BigDecimal payment, BigDecimal remaining);

	Remittance findByBillable(Billable b);

	String findWithPendingActions();

	String getPaymentReceivedBy();

	ZonedDateTime getPaymentReceivedOn();

	boolean isBillableOnThisPaymentList(Billable b);

	List<String> listAdjustingAccounts();

	void setPaymentReceiptData();

	Billable updateUponIdValidation(BillingType b, String prefix, Long id, String suffix) throws Exception;
}