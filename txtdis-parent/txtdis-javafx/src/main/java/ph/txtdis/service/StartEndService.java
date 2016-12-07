package ph.txtdis.service;

import java.time.LocalDate;

public interface StartEndService {

	LocalDate getEndDate();

	LocalDate getStartDate();

	void setEndDate(LocalDate d);

	void setStartDate(LocalDate d);
}
