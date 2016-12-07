package ph.txtdis.service;

import static java.time.LocalDate.now;

import java.time.LocalDate;
import java.util.List;

import ph.txtdis.dto.StartDated;
import ph.txtdis.exception.DateInThePastException;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.util.DateTimeUtils;

public interface DateNotInThePast {

	default void validateDateIsNotInThePast(LocalDate startDate) throws Exception {
		if (startDate.isBefore(now()))
			throw new DateInThePastException();
	}

	default void validateDateIsUnique(List<? extends StartDated> list, LocalDate startDate) throws Exception {
		if (list.stream().anyMatch(s -> s.getStartDate().isEqual(startDate)))
			throw new DuplicateException("Start Date of " + DateTimeUtils.toDateDisplay(startDate));
	}

	default void validateStartDate(List<? extends StartDated> list, LocalDate startDate) throws Exception {
		validateDateIsNotInThePast(startDate);
		validateDateIsUnique(list, startDate);
	}

}
