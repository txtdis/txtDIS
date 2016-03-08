package ph.txtdis.exception;

public class NoItemPriceException extends Exception {

	private static final long serialVersionUID = -7210202098067983158L;

	public NoItemPriceException(Long id) {
		super(id.toString());
	}
}
