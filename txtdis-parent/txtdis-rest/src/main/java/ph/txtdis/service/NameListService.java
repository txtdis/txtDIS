package ph.txtdis.service;

import java.util.List;

import ph.txtdis.dto.Keyed;

public interface NameListService<T extends Keyed<Long>> {

	T findByName(String name);

	List<T> findByOrderByNameAsc();
}
