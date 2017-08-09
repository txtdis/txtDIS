package ph.txtdis.mgdc.gsm.service;

import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ph.txtdis.dto.CreditDetail;
import ph.txtdis.dto.Route;
import ph.txtdis.dto.WeeklyVisit;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.UnauthorizedUserException;
import ph.txtdis.info.Information;
import ph.txtdis.mgdc.gsm.dto.Customer;
import ph.txtdis.mgdc.gsm.dto.CustomerDiscount;
import ph.txtdis.mgdc.gsm.dto.Item;
import ph.txtdis.mgdc.gsm.dto.ItemStartDate;
import ph.txtdis.type.PartnerType;
import ph.txtdis.type.UomType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;
import static ph.txtdis.type.PartnerType.EX_TRUCK;
import static ph.txtdis.type.UserType.MANAGER;
import static ph.txtdis.util.DateTimeUtils.*;
import static ph.txtdis.util.NumberUtils.isPositive;
import static ph.txtdis.util.UserUtils.isUser;
import static ph.txtdis.util.Util.areEqual;

@Service("customerService")
public class CustomerServiceImpl //
	extends AbstractCreditAndDiscountGivenCustomerService //
	implements CustomerService {

	@Autowired
	private BommedDiscountedPricedValidatedItemService itemService;

	@Value("${droar.go.live}")
	private String droarGoLive;

	private Item item;

	@Override
	public boolean cannotGiveCreditAndOrDiscount() {
		try {
			return get().getRoute().getName().startsWith(PartnerType.EX_TRUCK.toString());
		} catch (Exception e) {
			return true;
		}
	}

	@Override
	public boolean cannotReactivate() {
		return isNew() || (getDeactivatedOn() == null && !isUser(MANAGER));
	}

	@Override
	public CustomerDiscount createDiscountUponValidation(Item item, BigDecimal discount, LocalDate start)
		throws Exception {
		validateItemAndStartDate(getCustomerDiscounts(), item, start);
		return createCustomerDiscount(item, discount, start);
	}

	private void validateItemAndStartDate(List<? extends ItemStartDate> list, Item item, LocalDate startDate)
		throws Exception {
		confirmDateIsNotInThePast(startDate);
		confirmItemAndStartDateAreUnique(list, item, startDate);
	}

	@Override
	public List<CustomerDiscount> getCustomerDiscounts() {
		return get().getDiscounts();
	}

	private CustomerDiscount createCustomerDiscount(Item item, BigDecimal discount, LocalDate startDate) {
		CustomerDiscount d = new CustomerDiscount();
		d.setDiscount(discount);
		d.setItem(item);
		d.setStartDate(startDate);
		return d;
	}

	private void confirmItemAndStartDateAreUnique(List<? extends ItemStartDate> list, Item item, LocalDate startDate)
		throws Exception {
		if (list.stream().anyMatch(exist(item, startDate)))
			throw new DuplicateException("Discount for " + item + " of start date " + toDateDisplay(startDate));
	}

	private Predicate<ItemStartDate> exist(Item item, LocalDate startDate) {
		return d -> areEqual(d.getItem(), item) //
			&& areEqual(d.getStartDate(), startDate);
	}

	@Override
	public void setCustomerDiscounts(List<CustomerDiscount> discounts) {
		get().setDiscounts(discounts);
	}

	@Override
	public Customer findEmployee(Long id) throws Exception {
		return findByEndPt("/employee?id=" + id);
	}

	@Override
	public BigDecimal getQtyPerUom(UomType uom) {
		return itemService.getQtyPerUom(item, uom);
	}

	@Override
	public boolean hasCreditOrDiscountBeenGiven() {
		return hasCreditBeenGiven(getCredit(get(), today())) || hasDiscountBeenGiven(getCustomerDiscounts());
	}

	private boolean hasCreditBeenGiven(CreditDetail credit) {
		return credit.getTermInDays() > 0 || credit.getGracePeriodInDays() > 0;
	}

	private boolean hasDiscountBeenGiven(List<CustomerDiscount> discounts) {
		return discounts.isEmpty() ? false
			: discounts.stream() //
			.filter(d -> d.getIsValid() != null || d.getIsValid() == true).collect(groupingBy( //
				CustomerDiscount::getItem, //
				maxBy(comparing(CustomerDiscount::getStartDate)))) //
			.entrySet().stream() //
			.map(d -> d.getValue().orElse(new CustomerDiscount()).getDiscount()) //
			.anyMatch(d -> isPositive(d));
	}

	@Override
	public List<String> listNames() {
		try {
			return getList("/onAction").stream().map(e -> e.getName()).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
			return emptyList();
		}
	}

	@Override
	public void reactivate() throws Information, Exception {
		get().setDeactivatedBy(null);
		get().setDeactivatedOn(null);
		save();
	}

	@Override
	public void reset() {
		super.reset();
		item = null;
	}

	@Override
	public void setItem(Item item) {
		this.item = item;
	}

	@Override
	public void setNameUponValidation(String customer) throws Exception {
		super.setNameUponValidation(customer);
		if (customer.startsWith(EX_TRUCK.toString()))
			setExTruckRouteAndPartnerType(customer);
	}

	private void setExTruckRouteAndPartnerType(String exTruck) throws Exception {
		setType(EX_TRUCK);
		setExTruckRoute(exTruck);
	}

	private void setExTruckRoute(String exTruck) throws Exception {
		Route exTruckRoute = routeService.findByName(exTruck);
		throwExceptionIfExTruckRouteIsNotFound(exTruckRoute, exTruck);
		createRouteAssignment(exTruckRoute, goLive());
	}

	private void throwExceptionIfExTruckRouteIsNotFound(Route route, String exTruck) throws Exception {
		if (route != null)
			return;
		get().setName(null);
		throw new InvalidException("Add a " + exTruck + " route\nbefore proceeding");
	}

	@Override
	public void verifyUserIsAllowedToChangeSchedule(ObservableList<WeeklyVisit> old, ObservableList<WeeklyVisit>
		changed)
		throws Exception {
		if (isDroarLive() && isFromTheSameOutlet(old, changed) && !old.equals(changed) && !isUser(MANAGER))
			throw new UnauthorizedUserException("Managers Only");
	}

	private boolean isDroarLive() {
		return !toDate(droarGoLive).isBefore(getServerDate());
	}

	private boolean isFromTheSameOutlet(ObservableList<WeeklyVisit> old, ObservableList<WeeklyVisit> changed) {
		List<Long> customerIds = changed.stream().map(v -> v.getCustomerId()).collect(toCollection(ArrayList::new));
		return old.stream().anyMatch(v -> customerIds.contains(v.getCustomerId()));
	}
}
