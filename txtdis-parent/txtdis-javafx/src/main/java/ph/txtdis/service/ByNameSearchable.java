package ph.txtdis.service;

import java.util.List;

public interface ByNameSearchable<T> {

	List<T> search(String name) throws Exception;
}
