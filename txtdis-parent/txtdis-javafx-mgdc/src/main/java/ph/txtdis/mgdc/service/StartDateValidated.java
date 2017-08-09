package ph.txtdis.mgdc.service;

import ph.txtdis.dto.StartDated;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.util.DateTimeUtils;

import java.time.LocalDate;
import java.util.List;

public interface StartDateValidated
	extends DateNotInThePast {

	default void validateStartDate(List<? extends StartDated> list, LocalDate startDate) throws Exception {
		validateDateIsNotInThePast(startDate);
		validateDateIsUnique(list, startDate);
	}

	default void validateDateIsUnique(List<? extends StartDated> list, LocalDate startDate) throws Exception {
		if (list.stream().anyMatch(s -> s.getStartDate().isEqual(startDate)))
			throw new DuplicateException("Start Date of " + DateTimeUtils.toDateDisplay(startDate));
	}

}
