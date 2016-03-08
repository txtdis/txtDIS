package ph.txtdis.exception;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(INTERNAL_SERVER_ERROR)
public class EndDateBeforeStartException extends Exception {

	private static final long serialVersionUID = 4681082199212793048L;

	public EndDateBeforeStartException() {
		super("End date cannot be before start");
	}
}
