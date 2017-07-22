package ph.txtdis.service;

import java.util.List;

public interface SearchedByNameService<T> {

	List<T> search(String name) throws Exception;
}
