package ph.txtdis.type;

public enum PartnerType {
	INTERNAL, // 0
	OUTLET, // 1
	VENDOR, // 2
	FINANCIAL, // 3
	EX_TRUCK {
		@Override
		public String toString() {
			return "EX-TRUCK";
		}
	}; // 4
}
