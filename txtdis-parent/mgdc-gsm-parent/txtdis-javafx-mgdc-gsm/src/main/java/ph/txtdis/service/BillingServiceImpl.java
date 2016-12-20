package ph.txtdis.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Billable;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.dto.Customer;
import ph.txtdis.dto.Item;
import ph.txtdis.dto.Route;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.info.Information;
import ph.txtdis.type.UomType;

@Service("billingService")
public class BillingServiceImpl extends AbstractBillingService implements PickedLoadOrderVerified {

	@Autowired
	private CustomerValidationService customerValidationService;

	@Autowired
	private PricedBillableService priceService;

	private boolean isReferenceAnExTruckLoadOrder;

	private Customer customer;

	private List<BillableDetail> exTruckDetails;

	@Override
	public BillableDetail createDetail() {
		BillableDetail d = super.createDetail();
		return priceService.addPriceToDetail(d, customer, getOrderDate());
	}

	@Override
	public ReadOnlyService<Billable> getBillableReadOnlyService() {
		return billableReadOnlyService;
	}

	@Override
	public String getReferencePrompt() {
		return "Load/Sales Order No.";
	}

	@Override
	public boolean isAppendable() {
		return isNew() && isReferenceAnExTruckLoadOrder;
	}

	@Override
	protected boolean isForDR(Billable b) {
		// TODO Auto-generated method stub
		if (isReferenceAnExTruckLoadOrder(b))
			return true;
		return super.isForDR(b);
	}

	@Override
	public boolean isNew() {
		return getBilledOn() == null;
	}

	@Override
	protected void nullifyAll() {
		super.nullifyAll();
		customer = null;
		exTruckDetails = null;
		isReferenceAnExTruckLoadOrder = false;
	}

	@Override
	public void save() throws Information, Exception {
		// TODO Auto-generated method stub
		System.err.println("DueDate = " + getDueDate());
		super.save();
	}

	@Override
	public void setQtyUponValidation(UomType uom, BigDecimal qty) throws Exception {
		if (isBilledQtyMoreThanRemainingLoaded(qty))
			throw new InvalidException("Quantity is more than remaining");
		super.setQtyUponValidation(uom, qty);
	}

	private boolean isBilledQtyMoreThanRemainingLoaded(BigDecimal qty) {
		return exTruckDetails.stream()
				.anyMatch(d -> d.getId().equals(item.getId()) && d.getFinalQty().compareTo(qty) < 0);
	}

	@Override
	public void updateSummaries(List<BillableDetail> items) {
		setDetails(items);
		super.updateSummaries(items);
	}

	@Override
	public void updateUponCustomerIdValidation(Long id) throws Exception {
		Customer c = customerValidationService.validate(id, getOrderDate());
		verifyCustomerAndBillableRoutesAreTheSame(c);
		verifyDeliveryIsScheduledToday(c);
		priceService.setItemDiscountMap(c, getOrderDate());
		setCustomer(c);
	}

	private void setCustomer(Customer c) {
		customer = c;
		get().setDueDate(getOrderDate().plusDays(c.getCreditTerm()));
		get().setCustomerId(c.getId());
		get().setCustomerName(c.getName());
		get().setCustomerAddress(c.getAddress());
	}

	private void verifyCustomerAndBillableRoutesAreTheSame(Customer c) throws Exception {
		if (!areRoutesTheSame(c))
			throw new InvalidException(c + "\nis not in " + getRouteName(c) + "'s route");
	}

	private String getRouteName(Customer c) {
		if (get().getRoute() == null)
			get().setRoute(getCustomerRouteName(c));
		return get().getRoute();
	}

	private boolean areRoutesTheSame(Customer c) {
		return getRouteName(c).equalsIgnoreCase(getCustomerRouteName(c));
	}

	private String getCustomerRouteName(Customer c) {
		try {
			Route route = c.getRoute(getOrderDate());
			return route.getName();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	private void verifyDeliveryIsScheduledToday(Customer c) throws InvalidException {
		if (!c.areDeliveriesPickedUp(getOrderDate()) && !customerService.isDeliveryScheduledOnThisDate(c, getOrderDate()))
			throw new InvalidException(c + "\nis not scheduled for delivery today");
	}

	@Override
	protected Billable validateBooking(String id) throws Exception {
		Billable b = super.validateBooking(id);
		return !isReferenceAnExTruckLoadOrder ? b : updateUponLoadOrderValidation(b);
	}

	private boolean isReferenceAnExTruckLoadOrder(Billable b) {
		return isReferenceAnExTruckLoadOrder = b == null || b.getCustomerName() == null ? false
				: b.getCustomerName().startsWith("EX-TRUCK");
	}

	private Billable updateUponLoadOrderValidation(Billable b) throws Exception {
		verifyLoadOrderIsStillOpenForBilling(b);
		exTruckDetails = b.getDetails();
		return setOrderDateAndBookingIdAndCreatedByAndOn(b);
	}

	private void verifyLoadOrderIsStillOpenForBilling(Billable b) throws Exception {
		if (isAnInvoice())
			verifyPickedLoadOrderCanBeInvoiced(b);
		else
			verifyPickedLoadOrderCanBeDRdBecauseOfItemShortages(b.getBookingId());
	}

	private Billable setOrderDateAndBookingIdAndCreatedByAndOn(Billable b) {
		setOrderDate(b.getOrderDate());
		get().setBookingId(b.getBookingId());
		get().setCreatedBy(b.getCreatedBy());
		get().setCreatedOn(b.getCreatedOn());
		get().setPickListId(b.getPickListId());
		return get();
	}

	@Override
	public Item verifyItem(Long itemId) throws Exception {
		Item item = super.verifyItem(itemId);
		verifyItemIsInLoadOrder(item, itemId);
		return item;
	}

	private void verifyItemIsInLoadOrder(Item item, Long id) throws InvalidException {
		boolean isPresent = exTruckDetails.stream().anyMatch(d -> d.getId().equals(id));
		if (!isPresent)
			throw new InvalidException(item + "\nis not in Load Order");
	}
}
