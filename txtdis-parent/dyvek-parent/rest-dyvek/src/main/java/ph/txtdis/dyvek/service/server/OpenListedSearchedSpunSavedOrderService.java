package ph.txtdis.dyvek.service.server;

import ph.txtdis.dto.Keyed;

import java.util.List;

public interface OpenListedSearchedSpunSavedOrderService< //
	E extends Keyed<Long>, //
	T extends Keyed<Long>> //
	extends SearchedSpunSavedOrderService<E, T> {

	List<T> findAllOpen();
}
