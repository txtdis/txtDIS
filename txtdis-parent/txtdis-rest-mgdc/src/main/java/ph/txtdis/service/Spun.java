package ph.txtdis.service;

import java.io.Serializable;

import ph.txtdis.dto.Keyed;

public interface Spun<T extends Keyed<PK>, PK extends Serializable> {

	T first();

	T firstToSpin();

	T last();

	T lastToSpin();

	T next(PK id);

	T previous(PK id);
}
