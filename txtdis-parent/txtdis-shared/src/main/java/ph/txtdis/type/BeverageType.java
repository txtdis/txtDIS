package ph.txtdis.type;

public enum BeverageType {
	FULL_GOODS {
		@Override
		public String toString() {
			return "FULL GOODS";
		}
	},
	EMPTIES;
}
