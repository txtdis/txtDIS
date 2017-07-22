package ph.txtdis.type;

public enum RouteType {
	BULK, //
	EX_TRUCK {
		@Override
		public String toString() {
			return "EX-TRUCK";
		}
	}, //
	PRE_SELL {
		@Override
		public String toString() {
			return "PRE-SELL";
		}
	}, //
	PICK_UP {
		@Override
		public String toString() {
			return "PICK-UP";
		}
	};
}
