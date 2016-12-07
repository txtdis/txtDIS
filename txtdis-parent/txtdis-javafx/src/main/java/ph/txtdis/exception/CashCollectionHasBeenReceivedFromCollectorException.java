package ph.txtdis.exception;

import java.time.LocalDate;

import ph.txtdis.util.DateTimeUtils;

public class CashCollectionHasBeenReceivedFromCollectorException extends Exception {

	private static final long serialVersionUID = -680021070658923416L;

	public CashCollectionHasBeenReceivedFromCollectorException(String collector, LocalDate d) {
		super("Cash collection has been received\nfrom " + collector + " on " + DateTimeUtils.toDateDisplay(d));
	}
}
