package ph.txtdis.mgdc.gsm.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import ph.txtdis.mgdc.gsm.dto.Customer;
import ph.txtdis.service.LatestApproved;

public interface CustomerValidationService //
		extends LatestApproved {

	Customer validate(Long id, LocalDate d) throws Exception;

	BigDecimal getRemainingCreditValue();

	long getTermsInDays();
}