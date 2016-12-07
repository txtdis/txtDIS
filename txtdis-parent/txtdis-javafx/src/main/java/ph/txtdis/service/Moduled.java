package ph.txtdis.service;

import static org.apache.commons.lang3.StringUtils.capitalize;

public interface Moduled extends Iconed {

	default String newModule() {
		return "New " + getHeaderText();
	}

	default String getHeaderText() {
		return capitalize(getModule());
	}

	default String getOpenDialogHeader() {
		return "Open a(n) " + getHeaderText();
	}
}
