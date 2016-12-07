package ph.txtdis.service;

import java.io.Serializable;

import ph.txtdis.dto.Keyed;
import ph.txtdis.repository.SpunRepository;

public abstract class AbstractSpunService<//
		R extends SpunRepository<E, PK>, //
		E extends Keyed<PK>, //
		T extends Keyed<PK>, //
		PK extends Serializable> //
		extends AbstractIdService<R, E, T, PK> implements SpunService<T, PK> {

	@Override
	public T first() {
		return firstSpun();
	}

	private T firstSpun() {
		E e = repository.findFirstByOrderByIdAsc();
		return toDTO(e);
	}

	@Override
	public T firstToSpin() {
		return firstSpun();
	}

	@Override
	public T last() {
		return lastSpun();
	}

	private T lastSpun() {
		E e = repository.findFirstByOrderByIdDesc();
		return toDTO(e);
	}

	@Override
	public T lastToSpin() {
		return lastSpun();
	}

	@Override
	public T next(PK id) {
		E e = repository.findFirstByIdGreaterThanOrderByIdAsc(id);
		return toDTO(e);
	}

	@Override
	public T previous(PK id) {
		E e = repository.findFirstByIdLessThanOrderByIdDesc(id);
		return toDTO(e);
	}
}