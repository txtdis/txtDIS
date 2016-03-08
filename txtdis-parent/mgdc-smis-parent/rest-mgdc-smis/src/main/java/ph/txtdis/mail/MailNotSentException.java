package ph.txtdis.mail;

public class MailNotSentException extends Exception {

    private static final long serialVersionUID = -2619244872921175899L;

    public MailNotSentException() {
        super("Mail was not sent.\nCheck every network connections and close all other applications."
                + "\nClick the excel button to resend.");
    }
}
