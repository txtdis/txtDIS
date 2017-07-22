package ph.txtdis.service;

import java.io.Serializable;

import ph.txtdis.dto.Keyed;

public interface SpunSavedSearchedService<E extends Keyed<PK>, T extends Keyed<PK>, PK extends Serializable> //
		extends SavedReferencedKeyedService<E, T, PK>, SpunService<T, PK> {
}