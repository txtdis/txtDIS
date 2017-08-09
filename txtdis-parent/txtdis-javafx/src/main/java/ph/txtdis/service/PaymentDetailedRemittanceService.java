package ph.txtdis.service;

import java.math.BigDecimal;
import java.util.List;

import ph.txtdis.dto.RemittanceDetail;

public interface PaymentDetailedRemittanceService //
	extends AppendableDetailService,
	RemittanceService {

	List<RemittanceDetail> getDetails();

	BigDecimal getRemaining();

	//void nullifyPaymentData(Billable b) throws Information, Exception;
}