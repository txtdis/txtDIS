package ph.txtdis.service;

import java.io.Serializable;

import ph.txtdis.dto.Keyed;

public interface EntityService<E extends Keyed<PK>, PK extends Serializable> {

	E findEntity(PK id);
}
