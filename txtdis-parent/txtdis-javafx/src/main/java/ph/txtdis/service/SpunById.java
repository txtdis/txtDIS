package ph.txtdis.service;

import ph.txtdis.dto.CreationLogged;
import ph.txtdis.dto.Keyed;

public interface SpunById<PK> extends CreationLogged, GetterAndSetterService<PK>, Keyed<PK>, ModuleNamedService, SpunService {

	SpunKeyedService<? extends Keyed<PK>, PK> getSpunService();

	default boolean isNew() {
		return getCreatedOn() == null;
	}

	@Override
	default void next() throws Exception {
		set(getSpunService().module(getModuleName()).next(getId()));
	}

	@Override
	default void previous() throws Exception {
		set(getSpunService().module(getModuleName()).previous(getId()));
	}
}
