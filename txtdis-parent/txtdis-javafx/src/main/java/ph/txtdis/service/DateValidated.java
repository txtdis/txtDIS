package ph.txtdis.service;

import static java.time.LocalDate.now;
import static ph.txtdis.util.DateTimeUtils.toDateDisplay;

import java.time.LocalDate;
import java.util.List;

import ph.txtdis.dto.StartDated;
import ph.txtdis.exception.DateInThePastException;
import ph.txtdis.exception.DuplicateException;

public interface DateValidated {

	default void confirmDateIsNotInThePast(LocalDate startDate) throws DateInThePastException {
		if (startDate.isBefore(now()))
			throw new DateInThePastException();
	}

	default void validateDateIsUnique(List<? extends StartDated> list, LocalDate startDate) throws DuplicateException {
		if (list.stream().anyMatch(r -> r.getStartDate().equals(startDate)))
			throw new DuplicateException("Start Date of " + toDateDisplay(startDate));
	}

	default void validateStartDate(List<? extends StartDated> list, LocalDate startDate)
			throws DateInThePastException, DuplicateException {
		confirmDateIsNotInThePast(startDate);
		validateDateIsUnique(list, startDate);
	}
}
