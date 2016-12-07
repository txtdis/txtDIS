package ph.txtdis.printer;

public class NotPrintedException extends Exception {

	private static final long serialVersionUID = 7010664033441991775L;

	public NotPrintedException() {
		super("Not all were printed;\ncheck every connections, then restart");
	}
}
