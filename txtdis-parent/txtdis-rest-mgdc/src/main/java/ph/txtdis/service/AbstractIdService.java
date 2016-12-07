package ph.txtdis.service;

import java.io.Serializable;

import org.springframework.data.repository.CrudRepository;

import ph.txtdis.dto.Keyed;
import ph.txtdis.exception.NotFoundException;

public abstract class AbstractIdService<//
		R extends CrudRepository<E, PK>, //
		E extends Keyed<PK>, //
		T extends Keyed<PK>, //
		PK extends Serializable> //
		extends AbstractCreateService<R, E, T, PK> implements EntityService<E, PK>, IdService<T, PK> {

	@Override
	public T findById(PK id) throws NotFoundException {
		E e = findEntity(id);
		if (e == null)
			throw new NotFoundException("ID No. " + id);
		return toDTO(e);
	}

	@Override
	public E findEntity(PK id) {
		return id == null ? null : repository.findOne(id);
	}
}