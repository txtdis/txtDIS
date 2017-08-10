package ph.txtdis.mgdc.ccbpi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ph.txtdis.dto.Price;
import ph.txtdis.dto.PricingType;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.mgdc.ccbpi.dto.Item;
import ph.txtdis.service.RestClientService;
import ph.txtdis.type.PriceType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static ph.txtdis.type.BeverageType.EMPTIES;
import static ph.txtdis.type.BeverageType.FULL_GOODS;
import static ph.txtdis.type.UserType.MANAGER;
import static ph.txtdis.type.UserType.STORE_KEEPER;
import static ph.txtdis.util.UserUtils.isUser;

@Service("itemService")
public class ItemServiceImpl //
	extends AbstractBommedDiscountedPricedValidatedItemService {

	@Autowired
	private ItemFamilyService itemFamilyService;

	@Override
	public void confirmVendorIdIsUnique(String id) throws Exception {
		try {
			findByVendorNo(id);
			throw new DuplicateException("ID no. " + id + "\nhas non-numerals or");
		} catch (Exception e) {
			if (!(e instanceof NotFoundException))
				throw e;
		}
	}

	@Override
	public BigDecimal getCurrentPriceValue(Long itemId, LocalDate orderDate, PriceType type) {
		return getPrice(itemId, byTypeAndStartDate(orderDate, type));
	}

	public BigDecimal getPrice(Long itemId, Predicate<Price> filter) {
		try {
			return findById(itemId).getPriceList().stream().filter(filter) //
				.max(Comparator.comparing(Price::getStartDate)).get().getPriceValue();
		} catch (Exception e) {
			return BigDecimal.ZERO;
		}
	}

	private Predicate<Price> byTypeAndStartDate(LocalDate orderDate, PriceType type) {
		return p -> isSameType(p, type) && isApprovedAndStartDateIsNotInTheFuture(p, orderDate);
	}

	private boolean isSameType(Price p, PriceType type) {
		return p.getType().getName().equalsIgnoreCase(type.toString());
	}

	public RestClientService<Item> getRestClientServiceForLists() {
		return getRestClientService();
	}

	@Override
	public PricingType getDealerPricingType() {
		try {
			return pricingTypeService.findByName("DEALER");
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public BigDecimal getRegularPriceValue(Long itemId, PriceType type) {
		return getPrice(itemId, byTypeAndStartDate(LocalDate.MAX, type));
	}

	@Override
	public String getModuleNo() {
		return get().getVendorNo();
	}

	@Override
	public PricingType getPurchasePricingType() {
		try {
			return pricingTypeService.findByName("PURCHASE");
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public boolean isAppendable() {
		return isUser(MANAGER) || isUser(STORE_KEEPER);
	}

	@Override
	public boolean isEmpties() {
		try {
			return get().getFamily().getName().equalsIgnoreCase(EMPTIES.toString());
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public void setEmpties(boolean b) {
		try {
			String name = b ? EMPTIES.toString() : FULL_GOODS.toString();
			get().setFamily(itemFamilyService.findByName(name));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setEmpties(String empties) {
		get().setEmpties(empties);
	}

	@Override
	public boolean isNotDiscounted(String vendorNo) {
		try {
			Boolean b = findByVendorNo(vendorNo).isNotDiscounted();
			return b == null ? false : b;
		} catch (Exception e) {
			return true;
		}
	}

	@Override
	public List<String> listEmpties() {
		try {
			return getEmptiesList();
		} catch (Exception e) {
			return emptyList();
		}
	}

	private List<String> getEmptiesList() throws Exception {
		if (isNew())
			return getList("/empties").stream().map(i -> i.getName()).collect(Collectors.toList());
		return getEmptiesAsList();
	}

	private List<String> getEmptiesAsList() {
		String i = get().getEmpties();
		return i == null ? null : Arrays.asList(i);
	}

	@Override
	public void openByOpenDialogInputtedKey(String id) throws Exception {
		set(findByVendorNo(id));
	}
}
