package ph.txtdis.mgdc.service.server;

import java.io.Serializable;

import ph.txtdis.dto.Keyed;
import ph.txtdis.repository.SpunRepository;
import ph.txtdis.service.AbstractSavedReferencedKeyedService;

public abstract class AbstractServerSpunSavedSearchedEntityService< //
		R extends SpunRepository<E, PK>, //
		E extends Keyed<PK>, //
		T extends Keyed<PK>, //
		PK extends Serializable> //
		extends AbstractSavedReferencedKeyedService<R, E, T, PK> //
		implements SpunSavedReferencedKeyedService<E, T, PK> {

	@Override
	public T next(PK id) {
		return toModel(isNewOrLast(id) ? firstEntity() : nextEntity(id));
	}

	private boolean isNewOrLast(PK id) {
		return id == null || findEntityByPrimaryKey(id).equals(lastEntity());
	}

	protected E firstEntity() {
		return repository.findFirstByOrderByIdAsc();
	}

	protected E nextEntity(PK id) {
		return repository.findFirstByIdGreaterThanOrderByIdAsc(id);
	}

	@Override
	public T previous(PK id) {
		return toModel(isNewOrFirst(id) ? lastEntity() : previousEntity(id));
	}

	private boolean isNewOrFirst(PK id) {
		return id == null || findEntityByPrimaryKey(id).equals(firstEntity());
	}

	protected E lastEntity() {
		return repository.findFirstByOrderByIdDesc();
	}

	protected E previousEntity(PK id) {
		return repository.findFirstByIdLessThanOrderByIdDesc(id);
	}
}