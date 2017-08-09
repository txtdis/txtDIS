package ph.txtdis.exception;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(INTERNAL_SERVER_ERROR)
public class NotClosedException
	extends Exception {

	private static final long serialVersionUID = 5188901581015748476L;

	public NotClosedException(String message) {
		super(message + " is still open");
	}
}
