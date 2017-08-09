package ph.txtdis.mgdc.gsm.service;

import ph.txtdis.mgdc.gsm.dto.Customer;
import ph.txtdis.service.LatestApproved;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface CustomerValidationService //
	extends LatestApproved {

	Customer validate(Long id, LocalDate d) throws Exception;

	BigDecimal getRemainingCreditValue();

	long getTermsInDays();
}