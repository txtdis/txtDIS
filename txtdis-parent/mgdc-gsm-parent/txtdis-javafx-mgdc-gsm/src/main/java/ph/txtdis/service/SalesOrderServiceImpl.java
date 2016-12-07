package ph.txtdis.service;

import static java.util.Collections.emptyList;
import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.type.UserType.SALES_ENCODER;
import static ph.txtdis.type.UserType.SELLER;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Billable;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.dto.Customer;
import ph.txtdis.dto.Route;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.UnauthorizedUserException;
import ph.txtdis.fx.table.AppTable;
import ph.txtdis.type.BillableType;
import ph.txtdis.type.BillingType;

@Service("salesOrderService")
public class SalesOrderServiceImpl extends AbstractBookingService implements SalesOrderService {

	private static Logger logger = getLogger(SalesOrderServiceImpl.class);

	@Autowired
	private CustomerValidationService customerValidationService;

	@Autowired
	private InvoiceBookletService bookletService;

	@Autowired
	private ItineraryService itineraryService;

	@Autowired
	private PricedBillableService priceService;

	private BillableType type;

	@Override
	public BillableDetail createDetail() {
		BillableDetail d = super.createDetail();
		if (!isExTruck())
			d = priceService.addPriceToDetail(d, customer, getOrderDate());
		return d;
	}

	@Override
	public String getAlternateName() {
		if (isExTruck())
			return "L/O";
		return super.getAlternateName();
	}

	@Override
	public ReadOnlyService<Billable> getBillableReadOnlyService() {
		return billableReadOnlyService;
	}

	@Override
	public String getHeaderText() {
		if (isExTruck())
			return "Load Order";
		return "Sales Order";
	}

	@Override
	public String getIdPrompt() {
		if (isExTruck())
			return "Route";
		return super.getIdPrompt();
	}

	@Override
	public ReadOnlyService<Customer> getListedReadOnlyService() {
		return null;
	}

	@Override
	public String getModuleIdNo() {
		return "" + (getBookingId() == null ? "" : getBookingId());
	}

	@Override
	public String getReferencePrompt() {
		if (isExTruck())
			return "Load Order No.";
		return "Sales Order No.";
	}

	@Override
	public String getSavingInfo() {
		if (getOrderNo().equals("0"))
			return getAlternateName() + " invalidation";
		return getModuleId() + getBookingId();
	}

	@Override
	protected String getSeller() {
		return customer.getSeller(getOrderDate());
	}

	@Override
	public Long getSpunId() {
		return isNew() ? null : getBookingId();
	}

	@Override
	public String getSpunModule() {
		if (isExTruck())
			return "loadOrder";
		return "salesOrder";
	}

	@Override
	public boolean isAppendable() {
		return isNew() && (getDetails() == null || getDetails().size() < linesPerPage());
	}

	private int linesPerPage() {
		return isForDR() || isExTruck() ? Integer.MAX_VALUE : bookletService.getLinesPerPage();
	}

	private boolean isForDR() {
		try {
			return customerService.getBillingType(get()) == BillingType.DELIVERY;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean isExTruck() {
		return type == BillableType.EX_TRUCK;
	}

	@Override
	public List<Customer> listCustomersByScheduledRouteAndGoodCreditStanding(Route r) {
		return itineraryService.listCustomersByScheduledRouteAndGoodCreditStanding(r);
	}

	@Override
	public List<String> listUnbookedExTrucks() {
		if (!isNew())
			return Arrays.asList(get().getCustomerName());
		List<String> all = new ArrayList<>(customerService.listExTrucks());
		List<String> booked = listBookedExTrucks();
		if (!booked.isEmpty())
			all.removeAll(booked);
		return all;
	}

	private List<String> listBookedExTrucks() {
		try {
			return billableReadOnlyService.module("loadOrder").getList("/bookedExTrucks?on=" + getOrderDate()).stream()
					.map(b -> b.getCustomerName()).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
			return emptyList();
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void saveAsExcel(AppTable<Customer>... tables) throws IOException {
		itineraryService.saveAsExcel(tables);
	}

	@Override
	public void setCustomerRelatedData() {
		get().setCustomerId(customer.getId());
		get().setCustomerName(customer.getName());
		get().setCustomerAddress(customer.getAddress());
	}

	@Override
	public void setExTruckAsCustomerIfExTruckPreviousTransactionsAreComplete(String truck) throws Exception {
		logger.info("\n    ExTruck = " + truck);
		if (truck == null || truck.isEmpty() || !isNew())
			return;
		setExTruck(truck);
		verifyAllCashBillablesHaveBeenFullyPaid();
		verifyAllPickedBillablesHaveBeenClosed();
	}

	private void verifyAllPickedBillablesHaveBeenClosed() throws Exception {
		if (isExTruck())
			verifyAllPickedLoadOrdersHaveNoItemQuantityVariances(getSeller(), getOrderDate());
		else
			verifyAllPickedSalesOrderHaveBeenBilled(getSeller(), getOrderDate());
	}

	private void setExTruck(String t) {
		try {
			customer = customerService.findByName(t);
			setCustomerRelatedData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setType(BillableType type) {
		this.type = type;
	}

	@Override
	public void updateUponCustomerIdValidation(Long id) throws Exception {
		super.updateUponCustomerIdValidation(id);
		customer = customerValidationService.validate(id, getOrderDate());
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
		return customer.areDeliveriesPickedUp(today());
	}

	private boolean isAnExTruckRoute() {
		return getExTruckName().startsWith("EX-TRUCK");
	}

	private String getExTruckName() {
		return customer.getRoute(today()).getName();
	}

	private boolean isOnScheduledRoute() {
		return customerService.isDeliveryScheduledOnThisDate(customer, nextWorkDay());
	}

	private void verifyExTruckLoadOrderExistsBeforeUnscheduledDeliveriesAreBooked() throws Exception {
		if (isAnExTruckRoute() && !loadOrderExists())
			throw new InvalidException(
					getExTruckName() + " must have a Load Order\nbefore additional orders can be placed.");
	}

	private boolean loadOrderExists() {
		try {
			Billable b = billableReadOnlyService.module("loadOrder")
					.getOne("/bookedLoadOrder?date=" + today() + "&exTruck=" + getExTruckName());
			return b != null;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private void setDates() {
		if (isExTruck())
			return;
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
		long terms = customer.getCreditTerm();
		get().setDueDate(getOrderDate().plusDays(terms));
	}

	@Override
	protected void verifyUserAuthorization() throws UnauthorizedUserException {
		super.verifyUserAuthorization();
		if (!credentialService.isUser(SELLER) && !credentialService.isUser(SALES_ENCODER))
			throw new UnauthorizedUserException("Sales Encoders only");
	}
}
