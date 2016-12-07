package ph.txtdis.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import ph.txtdis.dto.CustomerDiscount;
import ph.txtdis.dto.ItemFamily;
import ph.txtdis.exception.DateInThePastException;
import ph.txtdis.exception.DuplicateException;

public interface PercentBasedCustomerDiscountService {

	CustomerDiscount createDiscountUponValidation(int level, BigDecimal discount, ItemFamily familyLimit,
			LocalDate start) throws DateInThePastException, DuplicateException;
}
