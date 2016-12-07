package ph.txtdis.mail;

import javax.mail.MessagingException;

public class CannotCheckMailException extends MessagingException {

    private static final long serialVersionUID = 8052249618227815045L;

    public CannotCheckMailException(String module, String id) {
        super("Cannot check mail for approval of " + id + module
                + ".\nCheck every network connections and close all other applications;"
                + "\nClick mail button to recheck");
    }
}
