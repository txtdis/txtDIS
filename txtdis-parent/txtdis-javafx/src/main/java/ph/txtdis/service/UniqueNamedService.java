package ph.txtdis.service;

import ph.txtdis.dto.Keyed;
import ph.txtdis.exception.DuplicateException;

public interface UniqueNamedService<T extends Keyed<Long>> {

	default void confirmUniqueness(String name) throws Exception {
		if (findByName(name) != null)
			throw new DuplicateException(name);
	}

	default T findByName(String name) throws Exception {
		return getListedReadOnlyService().module(getModuleName()).getOne("/" + name);
	}

	String getModuleName();

	ReadOnlyService<T> getListedReadOnlyService();
}
