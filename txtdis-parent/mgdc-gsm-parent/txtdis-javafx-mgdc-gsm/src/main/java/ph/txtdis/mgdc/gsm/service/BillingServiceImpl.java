package ph.txtdis.mgdc.gsm.service;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static ph.txtdis.type.PartnerType.EX_TRUCK;
import static ph.txtdis.type.UserType.MANAGER;
import static ph.txtdis.util.DateTimeUtils.toDate;
import static ph.txtdis.util.TextUtils.blankIfNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Billable;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.dto.Bom;
import ph.txtdis.dto.Route;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.mgdc.gsm.dto.Customer;
import ph.txtdis.mgdc.gsm.dto.Item;
import ph.txtdis.type.UomType;

@Service("billingService")
public class BillingServiceImpl //
		extends AbstractBillingService //
		implements GsmBillingService {

	@Autowired
	private CustomerValidationService customerValidationService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private PricedBillableService priceService;

	@Value("${droar.go.live}")
	private String droarGoLive;

	private boolean isReferenceAnExTruckLoadOrder;

	private Customer customer;

	private List<BillableDetail> exTruckDetails;

	private String oldOrderNo, remarks;

	@Override
	public boolean canEditInvoiceNo() {
		return getIsValid() == null //
				&& getBilledOn() != null //
				&& isUser(MANAGER);
	}

	@Override
	public BillableDetail createDetail() {
		BillableDetail d = super.createDetail();
		return priceService.addPriceToDetail(false, d, customer, getOrderDate());
	}

	@Override
	public boolean findUncorrectedInvalidBilling() {
		try {
			Billable b = findBillable("/uncorrected?type=" + type);
			return b == null ? false : setUncorrectedInvalidBilling(b);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean setUncorrectedInvalidBilling(Billable b) {
		isReferenceAnExTruckLoadOrder = b.isLoaded();
		set(isReferenceAnExTruckLoadOrder ? loadOrderData(b) : b);
		return true;
	}

	private Billable loadOrderData(Billable b) {
		exTruckDetails = b.getDetails();
		setCustomer(b);
		b.setDetails(null);
		b.setBilledBy(getUsername());
		return b;
	}

	private void setCustomer(Billable b) {
		try {
			customer = customerService.findById(b.getCustomerId());
		} catch (Exception e) {
			customer = null;
		}
	}

	@Override
	public Billable findUnvalidatedCorrectedBilling() {
		try {
			return findBillable("/unvalidatedCorrected?type=" + type);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public BigDecimal getAdjustment() {
		return getGrossValue().subtract(getTotalValue());
	}

	@Override
	public String getReferencePrompt() {
		return "L/S/O No.";
	}

	@Override
	public String getRemarks() {
		String remarks = get().getRemarks();
		return remarks == null ? "" : remarks;
	}

	@Override
	public boolean isAppendable() {
		return isNew() && isReferenceAnExTruckLoadOrder;
	}

	@Override
	protected boolean isForDR(Billable b) {
		if (isReferenceALoadOrder(b))
			return true;
		return super.isForDR(b);
	}

	@Override
	public boolean isNew() {
		return getBilledOn() == null;
	}

	@Override
	public void reset() {
		super.reset();
		customer = null;
		exTruckDetails = null;
		isReferenceAnExTruckLoadOrder = false;
		oldOrderNo = null;
		remarks = null;
	}

	@Override
	public Billable setAdjustmentData(Long id, BigDecimal value) {
		get().setEmployeeId(id);
		get().setAdjustmentValue(value);
		updateTotals();
		return get();
	}

	@Override
	public void setQtyUponValidation(UomType uom, BigDecimal qty) throws Exception {
		if (isBilledQtyMoreThanRemainingLoaded(qty))
			throw new InvalidException("Quantity is more than remaining");
		super.setQtyUponValidation(uom, qty);
	}

	private boolean isBilledQtyMoreThanRemainingLoaded(BigDecimal qty) {
		return exTruckDetails.stream() //
				.anyMatch(d -> d.getId().equals(item.getId()) && d.getFinalQty().compareTo(qty) < 0);
	}

	@Override
	public void setOrderNoAndRemarksBeforeInvoiceNoEdit() {
		oldOrderNo = getOrderNo();
		remarks = getRemarks();
	}

	@Override
	public void updateRemarksAfterInvoiceNoEdit() {
		if (remarks != null && !remarks.isEmpty())
			remarks = "\n" + remarks;
		remarks = "CHANGED S/I No. FROM " + oldOrderNo + " TO " + getOrderNo() + blankIfNull(remarks);
		setRemarks(remarks);
	}

	@Override
	public void updateUponCustomerIdValidation(Long id) throws Exception {
		Customer c = customerValidationService.validate(id, getOrderDate());
		verifyCustomerAndBillableRoutesAreTheSame(c);
		verifyDeliveryIsScheduledToday(c);
		priceService.setItemDiscountMap(c, getOrderDate());
		set(setCustomer(get(), c));
		customer = c;
	}

	private Billable setCustomer(Billable b, Customer c) {
		b.setDueDate(getOrderDate().plusDays(customerService.getCreditTerm(c)));
		b.setCustomerId(c.getId());
		b.setCustomerName(c.getName());
		b.setCustomerAddress(c.getAddress());
		return b;
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
			Route route = customerService.getRoute(c, getOrderDate());
			return route.getName();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	private void verifyDeliveryIsScheduledToday(Customer c) throws InvalidException {
		if (isNew() && isDroarLive() //
				&& !customerService.areDeliveriesPickedUp(c, getOrderDate()) //
				&& !customerService.isDeliveryScheduledOnThisDate(c, getOrderDate()))
			throw new InvalidException(c + "\nis not scheduled for delivery today");
	}

	private boolean isDroarLive() {
		return !toDate(droarGoLive).isBefore(syncService.getServerDate());
	}

	@Override
	protected Billable validateBooking(String id) throws Exception {
		Billable b = super.validateBooking(id);
		return !isReferenceALoadOrder(b) ? b : updateUponLoadOrderValidation(b);
	}

	private boolean isReferenceALoadOrder(Billable b) {
		return isReferenceAnExTruckLoadOrder = //
				b == null || b.getCustomerName() == null ? false : b.getCustomerName().startsWith(EX_TRUCK.toString());
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
			verifyPickedLoadOrderCanBeDRdBecauseOfItemShortages(getReadOnlyService(), b.getBookingId());
	}

	private Billable setOrderDateAndBookingIdAndCreatedByAndOn(Billable old) {
		Billable b = new Billable();
		b.setOrderDate(old.getOrderDate());
		b.setBookingId(old.getBookingId());
		b.setCreatedBy(old.getCreatedBy());
		b.setCreatedOn(old.getCreatedOn());
		b.setPickListId(old.getPickListId());
		return setOrderNo(b);
	}

	@Override
	public Customer verifyIsAnEmployee(Long id) throws Exception {
		Customer e = customerService.findEmployee(id);
		if (e == null)
			throw new NotFoundException("Employee No. " + id);
		return e;
	}

	@Override
	public Item verifyItem(Long itemId) throws Exception {
		Item item = super.verifyItem(itemId);
		verifyItemIsInLoadOrder(item, itemId);
		return item;
	}

	private void verifyItemIsInLoadOrder(Item item, Long idToBill) throws InvalidException {
		List<Long> loadedIds = exTruckDetails.stream().map(d -> d.getId()).collect(toList());
		List<Long> forBillingIds = extractAllItemIds(idToBill);
		if (!loadedIds.containsAll(forBillingIds))
			throw new InvalidException(item + "\nis not in Load Order");
	}

	private List<Long> extractAllItemIds(Long itemId) {
		try {
			List<Long> ids = new ArrayList<>();
			Item i = itemService.findById(itemId);
			if (i.getBoms().isEmpty())
				ids.add(itemId);
			else
				for (Bom bom : i.getBoms())
					ids.addAll(extractAllItemIds(bom.getId()));
			return ids;
		} catch (Exception e) {
			return asList(itemId);
		}
	}
}
