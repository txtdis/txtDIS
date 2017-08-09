package ph.txtdis.service;

import ph.txtdis.util.ClientTypeMap;

public interface IconAndModuleNamedAndTypeMappedService //
	extends ModuleNamedService {

	default String getIconName() {
		return getTypeMap().icon(getModuleName());
	}

	ClientTypeMap getTypeMap();
}
