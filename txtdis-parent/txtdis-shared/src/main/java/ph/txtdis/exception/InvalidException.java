package ph.txtdis.exception;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(INTERNAL_SERVER_ERROR)
public class InvalidException extends Exception {

	private static final long serialVersionUID = -2704062424620965401L;

	public InvalidException(String message) {
		super(message);
	}
}
