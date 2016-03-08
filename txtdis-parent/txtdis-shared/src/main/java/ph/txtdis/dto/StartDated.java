package ph.txtdis.dto;

import java.time.LocalDate;

public interface StartDated {

	LocalDate getStartDate();

	default int compareStartDates(StartDated d1, StartDated d2) {
		if (isStartDateNull(d2))
			return isStartDateNull(d1) ? 0 : 1;
		return isStartDateNull(d1) ? -1 : d1.getStartDate().compareTo(d2.getStartDate());
	}

	default boolean isStartDateNull(StartDated cd) {
		return cd == null || cd.getStartDate() == null;
	}

}
