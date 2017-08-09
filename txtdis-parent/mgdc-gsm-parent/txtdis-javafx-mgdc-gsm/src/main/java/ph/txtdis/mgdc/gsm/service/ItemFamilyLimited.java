package ph.txtdis.mgdc.gsm.service;

import ph.txtdis.dto.ItemFamily;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static ph.txtdis.type.ItemTier.PRINCIPAL;

public interface ItemFamilyLimited {

	default List<ItemFamily> listAllFamilies() {
		try {
			List<ItemFamily> l = new ArrayList<>(asList(getItemFamilyForAll()));
			l.addAll(getItemFamilyService().list());
			return l;
		} catch (Exception e) {
			return null;
		}
	}

	default ItemFamily getItemFamilyForAll() {
		ItemFamily a = new ItemFamily();
		a.setName("ALL");
		a.setTier(PRINCIPAL);
		return a;
	}

	ItemFamilyService getItemFamilyService();

	default ItemFamily nullIfAll(ItemFamily f) {
		return f.equals(getItemFamilyForAll()) ? null : f;
	}
}
