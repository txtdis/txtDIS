package ph.txtdis.exception;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(INTERNAL_SERVER_ERROR)
public class NoServerConnectionException extends Exception {

	private static final long serialVersionUID = -919135936918539710L;

	public NoServerConnectionException(String server) {
		super("No server connection;\ncheck link to " + server);
	}
}
