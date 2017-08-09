package ph.txtdis.mgdc.service;

import ph.txtdis.exception.DateInThePastException;

import java.time.LocalDate;

import static java.time.LocalDate.now;

public interface DateNotInThePast {

	default void validateDateIsNotInThePast(LocalDate startDate) throws Exception {
		if (startDate.isBefore(now()))
			throw new DateInThePastException();
	}
}
