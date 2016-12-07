package ph.txtdis.service;

import static ph.txtdis.util.DateTimeUtils.toDateDisplay;
import static ph.txtdis.util.Util.areEqual;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.CustomerDiscount;
import ph.txtdis.dto.CustomerVolumeDiscount;
import ph.txtdis.dto.CustomerVolumePromo;
import ph.txtdis.dto.Item;
import ph.txtdis.dto.ItemStartDate;
import ph.txtdis.exception.DateInThePastException;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.type.UomType;

@Service("customerService")
public class CustomerServiceImpl extends AbstractCustomerService
		implements ValueBasedCustomerDiscountWithVolumePromoAndDiscountCustomerService {

	@Autowired
	private ItemService itemService;

	private Item item;

	@Override
	public CustomerDiscount createDiscountUponValidation(Item item, BigDecimal discount, LocalDate start)
			throws DateInThePastException, DuplicateException {
		validateItemAndStartDate(customerDiscounts(), item, start);
		return createCustomerDiscount(item, discount, start);
	}

	private void validateItemAndStartDate(List<? extends ItemStartDate> list, Item item, LocalDate startDate)
			throws DateInThePastException, DuplicateException {
		confirmDateIsNotInThePast(startDate);
		confirmItemAndStartDateAreUnique(list, item, startDate);
	}

	private void confirmItemAndStartDateAreUnique(List<? extends ItemStartDate> list, Item item, LocalDate startDate)
			throws DuplicateException {
		if (list.stream().anyMatch(exist(item, startDate)))
			throw new DuplicateException("Discount for " + item + " of start date " + toDateDisplay(startDate));
	}

	private Predicate<ItemStartDate> exist(Item item, LocalDate startDate) {
		return d -> areEqual(d.getItem(), item) //
				&& areEqual(d.getStartDate(), startDate);
	}

	private CustomerDiscount createCustomerDiscount(Item item, BigDecimal discount, LocalDate startDate) {
		CustomerDiscount d = new CustomerDiscount();
		d.setDiscount(discount);
		d.setItem(item);
		d.setStartDate(startDate);
		updateCustomerDiscounts(d);
		return d;
	}

	@Override
	public CustomerVolumeDiscount createVolumeDiscountUponValidation(BigDecimal targetQty, BigDecimal discount,
			LocalDate start) throws DateInThePastException, DuplicateException {
		validateItemAndStartDate(volumeDiscounts(), item, start);
		return createVolumeDiscount(item, targetQty, discount, start);
	}

	private List<CustomerVolumeDiscount> volumeDiscounts() {
		if (get().getVolumeDiscounts() == null)
			get().setVolumeDiscounts(new ArrayList<>());
		return get().getVolumeDiscounts();
	}

	private CustomerVolumeDiscount createVolumeDiscount(Item item, BigDecimal targetQty, BigDecimal discount,
			LocalDate start) {
		CustomerVolumeDiscount p = new CustomerVolumeDiscount();
		p.setItem(item);
		p.setTargetQty(targetQty);
		p.setDiscountValue(discount);
		p.setStartDate(start);
		updateVolumeDiscounts(p);
		return p;
	}

	private void updateVolumeDiscounts(CustomerVolumeDiscount customerDiscount) {
		List<CustomerVolumeDiscount> list = new ArrayList<>(get().getVolumeDiscounts());
		list.add(customerDiscount);
		get().setVolumeDiscounts(list);
	}

	@Override
	public CustomerVolumePromo createVolumePromoUponValidation(BigDecimal targetQty, BigDecimal freeQty, LocalDate start)
			throws DateInThePastException, DuplicateException {
		validateItemAndStartDate(volumePromos(), item, start);
		return createVolumePromo(item, targetQty, freeQty, start);
	}

	private List<CustomerVolumePromo> volumePromos() {
		if (get().getVolumePromos() == null)
			get().setVolumePromos(new ArrayList<>());
		return get().getVolumePromos();
	}

	private CustomerVolumePromo createVolumePromo(Item item, BigDecimal targetQty, BigDecimal freeQty, LocalDate start) {
		CustomerVolumePromo p = new CustomerVolumePromo();
		p.setItem(item);
		p.setTargetQty(targetQty);
		p.setFreeQty(freeQty);
		p.setStartDate(start);
		updateVolumePromos(p);
		return p;
	}

	private void updateVolumePromos(CustomerVolumePromo customerDiscount) {
		List<CustomerVolumePromo> list = new ArrayList<>(get().getVolumePromos());
		list.add(customerDiscount);
		get().setVolumePromos(list);
	}

	@Override
	public List<CustomerVolumeDiscount> getVolumeDiscounts() {
		return get().getVolumeDiscounts();
	}

	@Override
	public List<CustomerVolumePromo> getVolumePromos() {
		return get().getVolumePromos();
	}

	@Override
	public BigDecimal getQtyPerUom(UomType uom) {
		return itemService.getQtyPerUom(item, uom);
	}

	@Override
	public void setItem(Item item) {
		this.item = item;
	}

	@Override
	public void setVolumeDiscounts(List<CustomerVolumeDiscount> discounts) {
		get().setVolumeDiscounts(discounts);
	}

	@Override
	public void setVolumePromos(List<CustomerVolumePromo> promos) {
		get().setVolumePromos(promos);
	}
}
