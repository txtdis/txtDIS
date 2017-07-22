package ph.txtdis.service;

import ph.txtdis.info.Information;

public interface SavedByName<T> {

	T save(String name) throws Information, Exception;
}
