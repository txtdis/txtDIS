package ph.txtdis.service;

import ph.txtdis.util.TypeMap;

public interface Iconed {

	default String getFontIcon() {
		return getTypeMap().icon(getModule());
	}

	TypeMap getTypeMap();

	String getModule();
}
