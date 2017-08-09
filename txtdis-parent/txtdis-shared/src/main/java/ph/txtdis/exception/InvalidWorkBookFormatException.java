package ph.txtdis.exception;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(INTERNAL_SERVER_ERROR)
public class InvalidWorkBookFormatException
	extends Exception {

	private static final long serialVersionUID = -9027625422810760302L;

	public InvalidWorkBookFormatException(String cell, String required) {
		super("Cell/s " + cell + " must contain " + required + ".\n" //
			+ "See template for correct format.");
	}
}
