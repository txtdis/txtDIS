package ph.txtdis.exception;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(INTERNAL_SERVER_ERROR)
public class DateBeforeGoLiveException extends Exception {

	private static final long serialVersionUID = 4259114034005470290L;

	public DateBeforeGoLiveException() {
		super("Date is before go-live");
	}
}
