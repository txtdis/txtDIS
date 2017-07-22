package ph.txtdis.service;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;

import ph.txtdis.dto.Keyed;
import ph.txtdis.exception.NotFoundException;

public abstract class AbstractSavedKeyedService<//
		R extends CrudRepository<E, PK>, //
		E extends Keyed<PK>, //
		T extends Keyed<PK>, //
		PK extends Serializable> //
		implements SavedKeyedService<E, T, PK> {

	@Autowired
	protected R repository;

	@Override
	public E findEntityByPrimaryKey(PK id) {
		return repository.findOne(id);
	}

	@Override
	public T findByPrimaryKey(PK id) throws NotFoundException {
		E e = findEntityByPrimaryKey(id);
		return toModel(e);
	}

	@Override
	public T save(T t) {
		E e = toEntity(t);
		return e == null ? null : toModel(post(e));
	}

	protected abstract E toEntity(T t);

	protected abstract T toModel(E e);

	public E post(E e) {
		return repository.save(e);
	}

	protected List<T> save(List<T> t) {
		List<E> e = toEntities(t);
		return e == null ? null : toModels(post(e));
	}

	protected List<E> toEntities(List<T> l) {
		return l == null ? null : l.stream().map(t -> toEntity(t)).collect(toList());
	}

	protected List<T> toModels(List<E> l) {
		return l == null ? null : l.stream().map(e -> toModel(e)).collect(toList());
	}

	public List<E> post(List<E> e) {
		Iterable<E> i = repository.save(e);
		return toEntities(i);
	}

	protected List<E> toEntities(Iterable<E> i) {
		return i == null ? null : stream(i.spliterator(), false).collect(toList());
	}
}
