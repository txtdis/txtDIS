package ph.txtdis.dyvek.service.server;

import ph.txtdis.dto.Keyed;
import ph.txtdis.service.SpunSavedKeyedService;

import java.util.List;

public interface SearchedSpunSavedOrderService< //
	E extends Keyed<Long>, //
	T extends Keyed<Long>> //>
	extends SpunSavedKeyedService<E, T, Long> {

	List<T> search(String orderNo);
}