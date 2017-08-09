package ph.txtdis.service;

import ph.txtdis.dto.CreationLogged;
import ph.txtdis.dto.Keyed;

public interface SpunByIdService<PK>
	extends CreationLogged,
	GetterAndSetterService<PK>,
	Keyed<PK>,
	ModuleNamedService,
	SpunByKeyService,
	SpunService {

	default boolean isNew() {
		return getCreatedOn() == null;
	}

	@Override
	default void next() throws Exception {
		set(next(getId()));
	}

	@Override
	default void previous() throws Exception {
		set(previous(getId()));
	}
}
