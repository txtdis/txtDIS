package ph.txtdis.service;

import java.io.Serializable;

import ph.txtdis.dto.Keyed;

public interface CreateService<T extends Keyed<PK>, PK extends Serializable> {

	T save(T t);
}
