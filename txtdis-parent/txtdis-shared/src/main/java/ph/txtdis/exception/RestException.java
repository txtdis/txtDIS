package ph.txtdis.exception;

import static org.apache.commons.lang3.StringUtils.substringBetween;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpStatusCodeException;

@ResponseStatus(INTERNAL_SERVER_ERROR)
public class RestException extends Exception {

	private static final long serialVersionUID = 174954871159198865L;

	public RestException(HttpStatusCodeException e) {
		super(substringBetween(//
				e.getResponseBodyAsString(), //
				"\"message\":\"", //
				"\",\"path\"") //
						.replace("\n", " "));
	}
}
