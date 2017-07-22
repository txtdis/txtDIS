package ph.txtdis.type;

public enum OrderReturnType {
	DIVERTED_PICK_UP {
		@Override
		public String toString() {
			return "DIVERTED PICK-UP";
		}
	}, //
	OWN_FAULT {
		@Override
		public String toString() {
			return "OWN FAULT";
		}
	}, //
	INVALID, //
	UNDELIVERED;
}
