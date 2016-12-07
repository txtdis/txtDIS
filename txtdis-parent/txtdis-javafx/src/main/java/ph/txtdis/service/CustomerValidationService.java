package ph.txtdis.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import ph.txtdis.dto.Customer;

public interface CustomerValidationService extends LatestApproved {

	Customer validate(Long id, LocalDate d) throws Exception;

	BigDecimal getRemainingCreditValue();

	long getTermsInDays();
}