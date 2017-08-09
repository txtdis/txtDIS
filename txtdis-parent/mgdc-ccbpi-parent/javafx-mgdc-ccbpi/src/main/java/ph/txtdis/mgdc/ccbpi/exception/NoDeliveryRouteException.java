package ph.txtdis.mgdc.ccbpi.exception;

public class NoDeliveryRouteException
	extends Exception {

	private static final long serialVersionUID = -959354899344338479L;

	public NoDeliveryRouteException(String customer) {
		super(customer + "\nhas no delivery route; please update.");
	}
}
