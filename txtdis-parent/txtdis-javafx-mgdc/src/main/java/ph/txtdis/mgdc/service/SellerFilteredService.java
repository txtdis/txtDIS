package ph.txtdis.mgdc.service;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import ph.txtdis.dto.SellerSold;
import ph.txtdis.service.ListedAndResetableService;

public interface SellerFilteredService<T extends SellerSold> extends ListedAndResetableService<T> {

	default boolean filterSeller(String seller, T a) {
		return seller.equals("ALL") ? true : a.getSeller().equals(seller);
	}

	default List<T> list(String seller) {
		try {
			return list().stream().filter(a -> filterSeller(seller, a)).collect(toList());
		} catch (Exception e) {
			e.printStackTrace();
			return emptyList();
		}
	}

	default List<String> listSellers() {
		try {
			List<String> l = new ArrayList<>(asList("ALL"));
			l.addAll(list().stream().map(s -> s.getSeller()).distinct().sorted().collect(toList()));
			return l.size() > 1 ? l : emptyList();
		} catch (Exception e) {
			e.printStackTrace();
			return emptyList();
		}
	}
}
