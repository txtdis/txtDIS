package ph.txtdis.exception;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import java.util.Date;

import org.springframework.web.bind.annotation.ResponseStatus;

import static ph.txtdis.util.DateTimeUtils.toTimestampText;

@ResponseStatus(INTERNAL_SERVER_ERROR)
public class NoNewerFileException extends Exception {

	private static final long serialVersionUID = 6996562319537406273L;

	public NoNewerFileException(String syncType, Date d) {
		super("No " + syncType + " newer than " + toTimestampText(d));
	}
}
