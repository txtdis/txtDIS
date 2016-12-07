package ph.txtdis.service;

import ph.txtdis.util.ClientTypeMap;

public interface Iconed {

	default String getFontIcon() {
		return getTypeMap().icon(getModule());
	}

	ClientTypeMap getTypeMap();

	String getModule();
}
