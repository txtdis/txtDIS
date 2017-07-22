package ph.txtdis.service;

import java.io.Serializable;

import ph.txtdis.dto.Keyed;

public interface SpunService<T extends Keyed<PK>, PK extends Serializable> {

	T next(PK id);

	T previous(PK id);
}
