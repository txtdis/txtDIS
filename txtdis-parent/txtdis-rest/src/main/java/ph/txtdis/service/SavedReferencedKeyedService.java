package ph.txtdis.service;

import java.io.Serializable;

import ph.txtdis.dto.Keyed;
import ph.txtdis.exception.NotFoundException;

public interface SavedReferencedKeyedService< //
		E extends Keyed<PK>, //
		T extends Keyed<PK>, //
		PK extends Serializable> //
		extends SavedKeyedService<E, T, PK> {

	T findByReferenceId(PK id) throws NotFoundException;
}