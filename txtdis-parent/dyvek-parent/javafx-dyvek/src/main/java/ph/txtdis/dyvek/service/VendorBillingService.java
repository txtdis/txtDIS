package ph.txtdis.dyvek.service;

import java.time.ZonedDateTime;
import java.util.List;

import ph.txtdis.dto.Remittance;
import ph.txtdis.info.Information;

public interface VendorBillingService //
		extends BillingService {

	String getPaymentActedBy();

	ZonedDateTime getPaymentActedOn();

	void setBillData(List<String> billData) throws Information, Exception;

	void setPaymentData(List<Remittance> paymentData) throws Information, Exception;
}
