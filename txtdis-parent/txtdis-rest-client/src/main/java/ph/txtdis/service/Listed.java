package ph.txtdis.service;

import java.util.List;

public interface Listed<T> {

	ReadOnlyService<T> getListedReadOnlyService();

	default List<T> list() throws Exception {
		return getListedReadOnlyService().module(getModule()).getList();
	}

	String getModule();
}
