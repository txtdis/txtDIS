package ph.txtdis.service;

import java.util.List;

public interface ListedAndResettableService<T> //
	extends ModuleNamedService,
	ResettableService {

	default List<T> list() {
		try {
			return getRestClientServiceForLists().module(getModuleName()).getList();
		} catch (Exception e) {
			return null;
		}
	}

	RestClientService<T> getRestClientServiceForLists();
}
