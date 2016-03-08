package ph.txtdis.service;

import static ph.txtdis.util.Util.areEqual;

import java.util.List;

import ph.txtdis.dto.Item;
import ph.txtdis.dto.Keyed;
import ph.txtdis.exception.AlreadyBilledBookingException;
import ph.txtdis.exception.DifferentDiscountException;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.NotAnItemToBeSoldToCustomerException;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;
import ph.txtdis.exception.ToBeReturnedItemNotPurchasedWithinTheLastSixMonthException;
import ph.txtdis.type.UomType;

public interface ItemBased<T extends Keyed<Long>> {

	default Item confirmItemExistsAndIsNotDeactivated(Long id) throws NoServerConnectionException,
			StoppedServerException, FailedAuthenticationException, RestException, InvalidException, NotFoundException {
		return getItemService().find(id);
	}

	default void confirmItemIsNotOnList(Item i) throws DuplicateException {
		if (getDetails().stream().anyMatch(d -> areEqual(d.getId(), i.getId())))
			throw new DuplicateException(i.getName());
	}

	default List<UomType> getBuyingUoms() {
		try {
			return getItemService().listBuyingUoms(getItem());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	List<T> getDetails();

	Item getItem();

	default String getItemName() {
		return getItem() == null ? null : getItem().getDescription();
	}

	ItemService getItemService();

	default List<UomType> getSellingUoms() {
		try {
			return getItemService().listSellingUoms(getItem());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	default Item verifyItem(Long id) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException, NotFoundException, DuplicateException,
			AlreadyBilledBookingException, NotAnItemToBeSoldToCustomerException, DifferentDiscountException,
			ToBeReturnedItemNotPurchasedWithinTheLastSixMonthException {
		Item item = confirmItemExistsAndIsNotDeactivated(id);
		confirmItemIsNotOnList(item);
		return item;
	}
}
