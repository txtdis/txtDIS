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
import ph.txtdis.type.UomType;

@Service("billingService")
public class BillingServiceImpl extends AbstractBillingService {

	@Autowired
	private CustomerValidationService customerValidationService;

	@Autowired
	private PricedBillableService priceService;

	private Billable exTruckLoadOrder;

	private Customer customer;

	private List<BillableDetail> exTruckDetails;

	@Override
	public BillableDetail createDetail() {
		BillableDetail d = super.createDetail();
		return priceService.addPriceToDetail(d, customer, getOrderDate());
	}

	@Override
	public String getReferencePrompt() {
		return "Load/Sales Order No.";
	}

	@Override
	public boolean isAppendable() {
		return isNew() && isReferenceAnExTruckLoadOrder(get());
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
		exTruckLoadOrder = null;
	}

	@Override
	public void setQtyUponValidation(UomType uom, BigDecimal qty) throws Exception {
		if (exTruckDetails != null) {
			for (BillableDetail detail : exTruckLoadOrder.getDetails()) {
				if (detail.getId().equals(item.getId())) {
					detail.setSoldQty(detail.getSoldQtyInDecimals().add(qty));
					break;
				}
			}
			exTruckLoadOrder = save(exTruckLoadOrder);
		}
		super.setQtyUponValidation(uom, qty);
	}

	@Override
	public void updateUponCustomerIdValidation(Long id) throws Exception {
		customer = customerValidationService.validate(id, getOrderDate());
		verifyCustomerAndBillableRoutesAreTheSame();
		verifyDeliveryIsScheduledToday();
		priceService.setItemDiscountMap(customer, getOrderDate());
	}

	private void verifyCustomerAndBillableRoutesAreTheSame() throws Exception {
		if (!areRoutesTheSame())
			throw new InvalidException(customer + "\nis not in " + getRouteName() + "'s route");
	}

	private String getRouteName() {
		if (get().getRoute() == null)
			get().setRoute(getCustomerRouteName());
		return get().getRoute();
	}

	private boolean areRoutesTheSame() {
		return getRouteName().equalsIgnoreCase(getCustomerRouteName());
	}

	private String getCustomerRouteName() {
		try {
			Route route = customer.getRoute(getOrderDate());
			return route.getName();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	private void verifyDeliveryIsScheduledToday() throws InvalidException {
		if (!customer.areDeliveriesPickedUp(getOrderDate())
				&& !customerService.isDeliveryScheduledOnThisDate(customer, getOrderDate()))
			throw new InvalidException(customer + "\nis not scheduled for delivery today");
	}

	@Override
	protected Billable validateBooking(String id) throws Exception {
		Billable b = super.validateBooking(id);
		return !isReferenceAnExTruckLoadOrder(b) ? b : updateUponLoadOrderValidation(b);
	}

	private boolean isReferenceAnExTruckLoadOrder(Billable b) {
		return b.getCustomerName().startsWith("EX-TRUCK");
	}

	private Billable updateUponLoadOrderValidation(Billable b) throws Exception {
		verifyLoadOrderIsStillOpen(b);
		exTruckDetails = b.getDetails();
		exTruckLoadOrder = b;
		return nullifyCustomerIdAndNameAndAddressAndBillableDetails(b);
	}

	private void verifyLoadOrderIsStillOpen(Billable b) throws Exception {
		if (isAnInvoice() && b.getReceivedOn() != null)
			throw new InvalidException("L/O No. " + b.getBookingId() + " has an R/R\nthus, is closed.");
	}

	private Billable nullifyCustomerIdAndNameAndAddressAndBillableDetails(Billable b) {
		b.setCustomerId(null);
		b.setCustomerName(null);
		b.setCustomerAddress(null);
		b.setDetails(null);
		return b;
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
