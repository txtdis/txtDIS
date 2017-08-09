package ph.txtdis.service;

import ph.txtdis.info.Information;

public interface SavedByEntity<T>
	extends ModuleNamedService {

	default T save(T entity) throws Information, Exception {
		return getRestClientService().module(getModuleName()).save(entity);
	}

	RestClientService<T> getRestClientService();
}
