package ph.txtdis.service;

import java.io.Serializable;

import ph.txtdis.dto.Keyed;

public interface SpunService<T extends Keyed<PK>, PK extends Serializable> extends IdService<T, PK>, Spun<T, PK> {
}