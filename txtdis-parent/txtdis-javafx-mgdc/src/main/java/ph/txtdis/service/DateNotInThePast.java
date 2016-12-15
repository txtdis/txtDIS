package ph.txtdis.service;

import static java.time.LocalDate.now;

import java.time.LocalDate;

import ph.txtdis.exception.DateInThePastException;

public interface DateNotInThePast {

	default void validateDateIsNotInThePast(LocalDate startDate) throws Exception {
		if (startDate.isBefore(now()))
			throw new DateInThePastException();
	}
}
