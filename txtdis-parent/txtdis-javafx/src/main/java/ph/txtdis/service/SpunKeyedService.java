package ph.txtdis.service;

import ph.txtdis.dto.Keyed;

public interface SpunKeyedService<T extends Keyed<PK>, PK> {

	SpunKeyedService<T, PK> module(String module);

	T next(PK key) throws Exception;

	T previous(PK key) throws Exception;
}