package ph.txtdis.mgdc.type;

public enum SalesVolumeReportType {
	CATEGORY, //
	PRODUCT_LINE {
		@Override
		public String toString() {
			return "PRODUCT LINE";
		}
	}, //
	ITEM; //
}
