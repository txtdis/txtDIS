package ph.txtdis.service;

import ph.txtdis.dto.Keyed;

public interface SpunService<T extends Keyed<PK>, PK> {

	SpunService<T, PK> module(String module);

	T next(PK pk) throws Exception;

	T previous(PK id) throws Exception;
}