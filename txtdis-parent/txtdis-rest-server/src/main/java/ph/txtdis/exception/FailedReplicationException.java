package ph.txtdis.exception;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(INTERNAL_SERVER_ERROR)
public class FailedReplicationException extends Exception {

	private static final long serialVersionUID = -3873340228411218001L;

	public FailedReplicationException(String activity) {
		super(activity + " failed");
	}
}
