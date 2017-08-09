package ph.txtdis.mgdc.service.server;

import ph.txtdis.dto.Keyed;
import ph.txtdis.service.SavedReferencedKeyedService;
import ph.txtdis.service.SpunSavedKeyedService;

import java.io.Serializable;

public interface SpunSavedReferencedKeyedService< //
	E extends Keyed<PK>, //
	T extends Keyed<PK>, //
	PK extends Serializable> //
	extends SavedReferencedKeyedService<E, T, PK>,
	SpunSavedKeyedService<E, T, PK> {
}