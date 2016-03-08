package ph.txtdis.service;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.List;

public interface Listed<T> extends Moduled {

	ReadOnlyService<T> getReadOnlyService();

	default List<T> list() {
		try {
			return getReadOnlyService().module(getModule()).getList();
		} catch (Exception e) {
			e.printStackTrace();
			return emptyList();
		}
	}

	default List<String> listNames() {
		try {
			return list().stream().map(t -> t.toString()).collect(toList());
		} catch (Exception e) {
			e.printStackTrace();
			return emptyList();
		}
	}
}
