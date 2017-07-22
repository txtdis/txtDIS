package ph.txtdis.dyvek.service.server;

import java.util.List;

import ph.txtdis.dto.Keyed;

public interface OpenListedSearchedSpunSavedOrderService< //
		E extends Keyed<Long>, //
		T extends Keyed<Long>> //
		extends SearchedSpunSavedOrderService<E, T> {

	List<T> findAllOpen();
}
