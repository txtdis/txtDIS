package ph.txtdis.exception;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(INTERNAL_SERVER_ERROR)
public class FailedPrintingException extends Exception {

	private static final long serialVersionUID = -5399480827294368809L;

	public FailedPrintingException(String message) {
		super("Printing failed: " + message);
	}
}
