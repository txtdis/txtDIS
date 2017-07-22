package ph.txtdis.service;

import java.util.List;

public interface ListedAndResetableService<T> //
		extends ModuleNamedService, ResettableService {

	ReadOnlyService<T> getListedReadOnlyService();

	default List<T> list() {
		try {
			return getListedReadOnlyService().module(getModuleName()).getList();
		} catch (Exception e) {
			return null;
		}
	}
}
