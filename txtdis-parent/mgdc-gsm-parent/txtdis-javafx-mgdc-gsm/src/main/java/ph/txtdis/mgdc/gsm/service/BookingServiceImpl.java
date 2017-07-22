package ph.txtdis.mgdc.gsm.service;

import static java.lang.Integer.MAX_VALUE;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static ph.txtdis.type.ItemType.BUNDLED;
import static ph.txtdis.type.ModuleType.SALES_ORDER;
import static ph.txtdis.type.PartnerType.EX_TRUCK;
import static ph.txtdis.type.UserType.SALES_ENCODER;
import static ph.txtdis.type.UserType.SELLER;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Billable;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.dto.Booking;
import ph.txtdis.dto.Route;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.UnauthorizedUserException;
import ph.txtdis.fx.table.AppTable;
import ph.txtdis.mgdc.gsm.dto.Customer;
import ph.txtdis.mgdc.gsm.dto.Item;
import ph.txtdis.service.ReadOnlyService;
import ph.txtdis.type.BillingType;
import ph.txtdis.type.ModuleType;

@Service("bookingService")
public class BookingServiceImpl //
		extends AbstractBookingService //
		implements GsmBookingService {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private ExpandedBommedDiscountedPricedValidatedItemService itemService;

	@Autowired
	private ItineraryService itineraryService;

	@Autowired
	private PricedBillableService priceService;

	@Value("${min.qty.to.avail.discount}")
	private BigDecimal minQtyToAvailDiscount;

	@Value("${invoice.line.item.count}")
	private Integer invoiceLinesPerPage;

	private boolean canAvailDiscount;

	private BigDecimal runningTotalInCases;

	private ModuleType type;

	@Override
	public BillableDetail createDetail() {
		BillableDetail d = super.createDetail();
		updateAllPricesIfMinQtytoAvailDiscountIsReached(d);
		if (!isLoadOrder())
			d = addPriceToDetail(d);
		return d;
	}

	private BillableDetail addPriceToDetail(BillableDetail d) {
		return priceService.addPriceToDetail(canAvailDiscount, d, customer(), getOrderDate());
	}

	private void updateAllPricesIfMinQtytoAvailDiscountIsReached(BillableDetail d) {
		runningTotalInCases = runningTotalInCases.add(itemService.getBomExpandedQtyInCases(d));
		if (!canAvailDiscount && runningTotalInCases.compareTo(minQtyToAvailDiscount) >= 0)
			updateAllPrices();
	}

	private void updateAllPrices() {
		canAvailDiscount = true;
		if (!getDetails().isEmpty())
			setDetails(getDetails().stream() //
					.map(bd -> addPriceToDetail(bd)) //
					.collect(toList()));
	}

	@Override
	public boolean canChangeDetails() {
		return get().isCanChangeDetails();
	}

	@Override
	public boolean detailsChanged() {
		return get().isDetailsChanged();
	}

	@Override
	public Billable findUncorrectedInvalidBilling() {
		try {
			return findBillable("/uncorrected?type=" + SALES_ORDER);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String getAlternateName() {
		if (isLoadOrder())
			return "L/O";
		return super.getAlternateName();
	}

	@Override
	public String getIdPrompt() {
		return isLoadOrder() ? "Route" : super.getIdPrompt();
	}

	@Override
	public ReadOnlyService<Customer> getListedReadOnlyService() {
		return null;
	}

	@Override
	public String getModuleName() {
		return isLoadOrder() ? "loadOrder" : "salesOrder";
	}

	@Override
	public String getModuleNo() {
		return getBookingId() == null ? "" : getBookingId().toString();
	}

	@Override
	public String getOrderNo() {
		return get().getOrderNo();
	}

	@Override
	public String getReferencePrompt() {
		return getHeaderName() + " No.";
	}

	@Override
	public String getSavingInfo() {
		if (getOrderNo().equals("0"))
			return getAlternateName() + " invalidation";
		return getAbbreviatedModuleNoPrompt() + getBookingId();
	}

	@Override
	protected String getSeller() {
		return customerService.getSeller(customer, getOrderDate());
	}

	@Override
	public ModuleType getType() {
		return type;
	}

	@Override
	public boolean isAppendable() {
		return isNew() //
				? getDetails() == null || getDetails().size() < linesPerPage()//
				: canChangeDetails();
	}

	private int linesPerPage() {
		return isForDR() || isLoadOrder() ? MAX_VALUE : invoiceLinesPerPage;
	}

	private boolean isForDR() {
		try {
			return customerService.getBillingType(get()) == BillingType.DELIVERY;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean isLoadOrder() {
		return type == ModuleType.EX_TRUCK;
	}

	@Override
	public List<Customer> listCustomersByScheduledRouteAndGoodCreditStanding(Route r) {
		return itineraryService.listCustomersByScheduledRouteAndGoodCreditStanding(r);
	}

	private ReadOnlyService<Billable> findBooking() {
		return getReadOnlyService().module(getModuleName());
	}

	@Override
	public List<String> listExTrucksWithoutLoadOrders() {
		if (!isNew())
			return asList(get().getCustomerName());
		return removeBooked(new ArrayList<>(customerService.listNames()));
	}

	private List<String> removeBooked(List<String> all) {
		List<String> booked = listBooked();
		if (!booked.isEmpty())
			all.removeAll(booked);
		return all;
	}

	private List<String> listBooked() {
		try {
			return findBooking() //
					.getList("/booked?on=" + getOrderDate()) //
					.stream().map(b -> b.getCustomerName()) //
					.collect(toList());
		} catch (Exception e) {
			return emptyList();
		}
	}

	@Override
	public List<Booking> listUnpicked(LocalDate d) {
		try {
			return findBooking().getList("/forPicking?on=" + d).stream().map(a -> toBooking(a)).collect(toList());
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void reset() {
		super.reset();
		canAvailDiscount = false;
		runningTotalInCases = BigDecimal.ZERO;
	}

	@Override
	public void resetDetailsAndUpdateDiscountsAndPrices() {
		setDetails(null);
		priceService.setItemDiscountMap(customer(), getOrderDate());
		get().setDetailsChanged(true);
	}

	private Customer customer() {
		if (customer == null)
			try {
				customer = customerService.findById(getCustomerId());
			} catch (Exception e) {
				e.printStackTrace();
			}
		return customer;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void saveAsExcel(AppTable<Customer>... tables) throws IOException {
		itineraryService.saveAsExcel(tables);
	}

	@Override
	public void setCustomerRelatedData() {
		get().setCustomerId(customer.getId());
		get().setCustomerName(customerName());
		get().setCustomerAddress(customer.getAddress());
	}

	private String customerName() {
		return customer.getName();
	}

	@Override
	public void setExTruckAsCustomerIfExTruckPreviousTransactionsAreComplete(String exTruck) throws Exception {
		setExTruckAsCustomer(exTruck);
		verifyAllCODsHaveBeenFullyPaid();
		verifyAllLoadOrdersHaveBeenPicked(getReadOnlyService(), getOrderDate());
		verifyAllPickedLoadOrdersHaveNoItemQuantityVariances(getReadOnlyService(), getOrderDate());
	}

	private void setExTruckAsCustomer(String t) throws Exception {
		customer = customerService.findByName(t);
		setCustomerRelatedData();
	}

	@Override
	public boolean setType(ModuleType type) {
		this.type = type;
		return true;
	}

	@Override
	public void updateUponCustomerIdValidation(Long id) throws Exception {
		super.updateUponCustomerIdValidation(id);
		verifyCustomerIsOnScheduledDeliveryRoute();
		verifyExTruckLoadOrderExistsBeforeUnscheduledDeliveriesAreBooked();
		priceService.setItemDiscountMap(customer, getOrderDate());
		setCustomerRelatedData();
		setDates();
	}

	private void verifyCustomerIsOnScheduledDeliveryRoute() throws InvalidException {
		if (!isPickedUp() && !isAnExTruckRoute() && !isOnScheduledRoute())
			throw new InvalidException(customer + "\nis not on the scheduled route.");
	}

	private boolean isPickedUp() {
		return customerService.areDeliveriesPickedUp(customer, today());
	}

	private boolean isAnExTruckRoute() {
		return getExTruckName().startsWith(EX_TRUCK.toString());
	}

	private String getExTruckName() {
		return customerService.getRoute(customer, today()).getName();
	}

	private boolean isOnScheduledRoute() {
		return customerService.isDeliveryScheduledOnThisDate(customer, getOrderDate());
	}

	private void verifyExTruckLoadOrderExistsBeforeUnscheduledDeliveriesAreBooked() throws Exception {
		if (isAnExTruckRoute() && !loadOrderExists())
			throw new InvalidException(getExTruckName() + " must have a Load Order\nbefore additional orders can be placed.");
	}

	private boolean loadOrderExists() {
		try {
			Billable b = findBooking().getOne("/bookedLoadOrder?date=" + today() + "&exTruck=" + getExTruckName());
			return b != null;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private void setDates() {
		if (isLoadOrder())
			return;
		if (getOrderDate() == null)
			setOrderDate();
		setDueDate();
	}

	private void setOrderDate() {
		if (isPickedUp() || isAnExTruckRoute())
			setOrderDate(today());
		else
			setOrderDate(nextWorkDay());
	}

	private void setDueDate() {
		long terms = customerService.getCreditTerm(customer);
		get().setDueDate(getOrderDate().plusDays(terms));
	}

	@Override
	public Item verifyItem(Long id) throws Exception {
		Item i = super.verifyItem(id);
		if (isLoadOrder() && i.getType() == BUNDLED)
			throw new InvalidException("Load components of bundled items");
		return i;
	}

	@Override
	protected void verifyUserAuthorization() throws Exception {
		if (!isUser(SELLER) && !isUser(SALES_ENCODER))
			throw new UnauthorizedUserException("Sales Encoders only");
	}
}
