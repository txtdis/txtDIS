package ph.txtdis.mgdc.gsm.mail;


public interface ApprovedByMail {

	void sendMail() throws MailNotSentException;

	Mail checkMail() throws CannotCheckMailException;

	void handleCheckingResult(Mail mail);
}
