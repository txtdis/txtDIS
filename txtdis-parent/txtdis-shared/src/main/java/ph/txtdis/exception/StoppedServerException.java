package ph.txtdis.exception;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(INTERNAL_SERVER_ERROR)
public class StoppedServerException
	extends Exception {

	private static final long serialVersionUID = 7515581013872748074L;

	public StoppedServerException() {
		super("Database stopped;\nreboot server");
	}

}
