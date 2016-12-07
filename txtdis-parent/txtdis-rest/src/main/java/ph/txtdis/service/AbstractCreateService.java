package ph.txtdis.service;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;

import ph.txtdis.dto.Keyed;

public abstract class AbstractCreateService<//
		R extends CrudRepository<E, PK>, //
		E extends Keyed<PK>, //
		T extends Keyed<PK>, //
		PK extends Serializable> //
		implements CreateService<T, PK> {

	@Autowired
	protected R repository;

	@Override
	public T save(T t) {
		if (t == null)
			return null;
		E e = toEntity(t);
		e = repository.save(e);
		return toDTO(e);
	}

	protected List<T> toList(List<E> l) {
		return l == null ? null : l.stream().map(e -> toDTO(e)).collect(Collectors.toList());
	}

	protected List<E> toEntities(List<T> l) {
		return l == null ? null : l.stream().map(t -> toEntity(t)).collect(Collectors.toList());
	}

	protected abstract T toDTO(E e);

	protected abstract E toEntity(T t);
}
