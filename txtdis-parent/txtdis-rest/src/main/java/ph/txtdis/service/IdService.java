package ph.txtdis.service;

import java.io.Serializable;

import ph.txtdis.dto.Keyed;
import ph.txtdis.exception.NotFoundException;

public interface IdService<T extends Keyed<PK>, PK extends Serializable> extends CreateService<T, PK> {

	T findById(PK id) throws NotFoundException;
}