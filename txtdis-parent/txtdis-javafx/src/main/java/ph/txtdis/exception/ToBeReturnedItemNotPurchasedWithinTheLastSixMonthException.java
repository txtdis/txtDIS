package ph.txtdis.exception;

public class ToBeReturnedItemNotPurchasedWithinTheLastSixMonthException
	extends Exception {

	private static final long serialVersionUID = 1145267869617171227L;

	public ToBeReturnedItemNotPurchasedWithinTheLastSixMonthException(String item, String customer) {
		super(customer + " did not buy\n" + item + " in the last six months");
	}
}
