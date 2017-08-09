package ph.txtdis.mgdc.gsm.mail;

import java.time.ZonedDateTime;

public class Mail {

	private final String approver;

	private final ZonedDateTime timestamp;

	private final Boolean isApproved;

	public Mail(String approver, ZonedDateTime timestamp, Boolean isApproved) {
		this.approver = approver;
		this.timestamp = timestamp;
		this.isApproved = isApproved;
	}

	public String getAddress() {
		return approver;
	}

	public ZonedDateTime getTimestamp() {
		return timestamp;
	}

	public Boolean isApproved() {
		return isApproved;
	}
}
