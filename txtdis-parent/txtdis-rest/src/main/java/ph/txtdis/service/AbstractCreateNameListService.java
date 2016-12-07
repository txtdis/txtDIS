package ph.txtdis.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.dto.Keyed;
import ph.txtdis.repository.NameListRepository;

public abstract class AbstractCreateNameListService<//
		R extends NameListRepository<E>, //
		E extends Keyed<Long>, //
		T extends Keyed<Long>> //
		extends AbstractCreateService<R, E, T, Long> //
		implements NameListCreateService<T> {

	@Autowired
	protected R repository;

	@Override
	public T findByName(String name) {
		E e = repository.findByNameIgnoreCase(name);
		return toDTO(e);
	}

	@Override
	public List<T> findByOrderByNameAsc() {
		List<E> e = repository.findByOrderByNameAsc();
		return toList(e);
	}
}
