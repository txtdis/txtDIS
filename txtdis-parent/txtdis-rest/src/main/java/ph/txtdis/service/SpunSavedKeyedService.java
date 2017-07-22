package ph.txtdis.service;

import java.io.Serializable;

import ph.txtdis.dto.Keyed;

public interface SpunSavedKeyedService< //
		E extends Keyed<PK>, //
		T extends Keyed<PK>, //
		PK extends Serializable> //
		extends SpunService<T, PK>, SavedKeyedService<E, T, PK> {
}