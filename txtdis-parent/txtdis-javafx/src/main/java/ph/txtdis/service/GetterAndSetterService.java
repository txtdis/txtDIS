package ph.txtdis.service;

import ph.txtdis.dto.Keyed;

public interface GetterAndSetterService<PK> {

	<T extends Keyed<PK>> T get();

	<T extends Keyed<PK>> void set(T t);
}
