package ph.txtdis.service;

import java.io.Serializable;

import org.springframework.data.repository.CrudRepository;

import ph.txtdis.dto.Keyed;
import ph.txtdis.exception.NotClosedException;
import ph.txtdis.exception.NotFoundException;

public abstract class AbstractSavedReferencedKeyedService< //
		R extends CrudRepository<E, PK>, //
		E extends Keyed<PK>, //
		T extends Keyed<PK>, //
		PK extends Serializable> //
		extends AbstractSavedKeyedService<R, E, T, PK> //
		implements SavedReferencedKeyedService<E, T, PK> {

	@Override
	public T findByPrimaryKey(PK id) {
		E e = findEntityByPrimaryKey(id);
		return toModel(e);
	}

	@Override
	public T findByReferenceId(PK id) throws NotFoundException {
		return findByPrimaryKey(id);
	}

	@Override
	public E findEntityByPrimaryKey(PK id) {
		return id == null ? null : repository.findOne(id);
	}

	protected T throwExceptionIfNotFound(String moduleNoPrompt, E e, Long id) throws NotFoundException {
		if (e == null)
			throw new NotFoundException(moduleNoPrompt + id);
		return toModel(e);
	}

	protected T throwNotClosedExceptionIfExists(String moduleNoPrompt, E e, Long id) throws NotClosedException {
		if (e != null)
			throw new NotClosedException(moduleNoPrompt + id);
		return null;
	}
}