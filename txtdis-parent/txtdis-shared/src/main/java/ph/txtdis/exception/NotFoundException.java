package ph.txtdis.exception;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(INTERNAL_SERVER_ERROR)
public class NotFoundException extends Exception {

	private static final long serialVersionUID = -6046598298985805710L;

	public NotFoundException(String message) {
		super(message + " does not exist");
	}
}
