package ph.txtdis.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.dto.Keyed;
import ph.txtdis.repository.NameListRepository;

public abstract class AbstractCreateNameListService<//
		R extends NameListRepository<E>, //
		E extends Keyed<Long>, //
		T extends Keyed<Long>> //
		extends AbstractSavedKeyedService<R, E, T, Long> //
		implements SavedNameListService<T> {

	@Autowired
	protected R repository;

	@Override
	public T findByName(String name) {
		E e = findEntityByName(name);
		return toModel(e);
	}

	protected E findEntityByName(String name) {
		return repository.findByNameIgnoreCase(name);
	}

	@Override
	public List<T> findByOrderByNameAsc() {
		List<E> e = repository.findByOrderByNameAsc();
		return toModels(e);
	}
}
