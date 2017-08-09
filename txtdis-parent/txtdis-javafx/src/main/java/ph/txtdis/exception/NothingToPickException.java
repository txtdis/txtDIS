package ph.txtdis.exception;

import static ph.txtdis.util.DateTimeUtils.toDateDisplay;

import java.time.LocalDate;

public class NothingToPickException
	extends Exception {

	private static final long serialVersionUID = 7000764332504494319L;

	public NothingToPickException(LocalDate date) {
		super("There's nothing to pick on\n" + toDateDisplay(date));
	}
}
