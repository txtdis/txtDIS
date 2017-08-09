package ph.txtdis.service;

import ph.txtdis.dto.StartDated;
import ph.txtdis.exception.DateInThePastException;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.util.DateTimeUtils;

import java.time.LocalDate;
import java.util.List;

public interface DateValidated {

	default void confirmDateIsNotInThePast(LocalDate startDate) throws Exception {
		if (startDate.isBefore(today()))
			throw new DateInThePastException();
	}

	LocalDate today();

	default void validateStartDate(List<? extends StartDated> list, LocalDate startDate) throws Exception {
		// confirmDateIsNotInThePast(startDate);
		validateDateIsUnique(list, startDate);
	}

	default void validateDateIsUnique(List<? extends StartDated> list, LocalDate startDate) throws Exception {
		if (list == null || list.isEmpty())
			return;
		if (list.stream().anyMatch(r -> r.getStartDate().isEqual(startDate)))
			throw new DuplicateException("Start Date of " + DateTimeUtils.toDateDisplay(startDate));
	}
}
