package ph.txtdis.mgdc.ccbpi.service;

import ph.txtdis.dto.Keyed;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.exception.NotSellableItemException;
import ph.txtdis.mgdc.ccbpi.dto.Item;
import ph.txtdis.type.UomType;

import java.util.List;

import static ph.txtdis.type.ItemType.FREE;
import static ph.txtdis.util.Util.areEqual;

public interface ItemBasedService<T extends Keyed<Long>> {

	default List<UomType> getBuyingUoms() {
		try {
			return getItemService().listBuyingUoms(getItem());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	BommedDiscountedPricedValidatedItemService getItemService();

	Item getItem();

	default String getItemName() {
		return getItem() == null ? null : getItem().getDescription();
	}

	default List<UomType> getSellingUoms() {
		try {
			return getItemService().listSellingUoms(getItem());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	default Item verifyItem(Long id) throws Exception {
		Item i = confirmItemExistsAndIsNotDeactivated(id);
		confirmItemIsNotOnList(i);
		if (i.getType() == FREE)
			throw new NotSellableItemException(i.getName());
		return i;
	}

	default Item confirmItemExistsAndIsNotDeactivated(Long id) throws Exception {
		return getItemService().findById(id);
	}

	default void confirmItemIsNotOnList(Item i) throws DuplicateException {
		if (getDetails() != null)
			if (getDetails().stream().filter(d -> d != null).anyMatch(d -> areEqual(d.getId(), i.getId())))
				throw new DuplicateException(i.getName());
	}

	List<T> getDetails();
}
