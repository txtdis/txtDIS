package ph.txtdis.service;

import java.time.LocalDate;

public interface StartEndService {

	LocalDate getEndDate();

	void setEndDate(LocalDate d);

	LocalDate getStartDate();

	void setStartDate(LocalDate d);
}
