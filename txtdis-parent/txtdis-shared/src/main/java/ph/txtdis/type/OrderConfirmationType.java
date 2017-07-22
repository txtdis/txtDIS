package ph.txtdis.type;

public enum OrderConfirmationType {
	BLANKET, //
	BUFFER, //
	MANUAL, //
	PARTIAL, //
	REGULAR, //
	STOCK_OUT {
		@Override
		public String toString() {
			return "STOCK-OUT";
		}
	}, //
	UNDELIVERED, //
	WAREHOUSE;
}
