package ph.txtdis.service;

import java.io.Serializable;
import java.util.List;

import ph.txtdis.dto.Keyed;
import ph.txtdis.exception.NotFoundException;

public interface SavedKeyedService<E extends Keyed<PK>, T extends Keyed<PK>, PK extends Serializable> //
		extends SavedService<T> {

	E findEntityByPrimaryKey(PK id);

	T findByPrimaryKey(PK id) throws NotFoundException;

	E post(E e);

	List<E> post(List<E> l);
}
