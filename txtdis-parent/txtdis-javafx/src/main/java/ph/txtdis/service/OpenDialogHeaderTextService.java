package ph.txtdis.service;

public interface OpenDialogHeaderTextService //
	extends HeaderNameService {

	default String getOpenDialogHeader() {
		return "Open a(n) " + getHeaderName();
	}

	default String getOpenDialogKeyPrompt() {
		return "";
	}

	default String getOpenDialogPrompt() {
		return "Enter ID of " + getHeaderName() + " to open";
	}
}
