package ph.txtdis.mgdc.service.server;

import java.io.Serializable;

import ph.txtdis.dto.Keyed;
import ph.txtdis.service.SavedReferencedKeyedService;
import ph.txtdis.service.SpunSavedKeyedService;

public interface SpunSavedReferencedKeyedService< //
		E extends Keyed<PK>, //
		T extends Keyed<PK>, //
		PK extends Serializable> //
		extends SavedReferencedKeyedService<E, T, PK>, SpunSavedKeyedService<E, T, PK> {
}