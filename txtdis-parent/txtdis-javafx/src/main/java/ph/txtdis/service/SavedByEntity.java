package ph.txtdis.service;

import ph.txtdis.info.Information;

public interface SavedByEntity<T> extends ModuleNamedService {

	SavingService<T> getSavingService();

	default T save(T entity) throws Information, Exception {
		return getSavingService().module(getModuleName()).save(entity);
	}
}
