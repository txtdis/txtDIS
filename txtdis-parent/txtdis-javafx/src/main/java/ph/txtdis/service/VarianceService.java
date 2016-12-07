package ph.txtdis.service;

public interface VarianceService<T> extends ReportService<T> {

	String getActualHeader();

	default String getBeginningHeader() {
		return "Beginning";
	}

	String getExpectedHeader();

	default String getVarianceHeader() {
		return "Gain / (Loss)";
	}
}
