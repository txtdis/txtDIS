package ph.txtdis.exception;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(INTERNAL_SERVER_ERROR)
public class FailedAuthenticationException
	extends Exception {

	private static final long serialVersionUID = -2665228278605327804L;

	public FailedAuthenticationException() {
		super("Incorrect username and/or password");
	}
}
