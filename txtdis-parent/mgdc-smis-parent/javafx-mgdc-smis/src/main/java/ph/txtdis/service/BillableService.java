package ph.txtdis.service;

import static java.lang.Integer.valueOf;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_EVEN;
import static java.time.LocalDate.now;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNumeric;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.split;
import static org.apache.commons.lang3.StringUtils.splitByCharacterType;
import static org.apache.commons.lang3.text.WordUtils.capitalizeFully;
import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.type.BillingType.DELIVERY;
import static ph.txtdis.type.DeliveryType.PICK_UP;
import static ph.txtdis.type.ModuleType.BAD_ORDER;
import static ph.txtdis.type.ModuleType.DELIVERY_REPORT;
import static ph.txtdis.type.ModuleType.INVOICE;
import static ph.txtdis.type.ModuleType.PURCHASE_ORDER;
import static ph.txtdis.type.ModuleType.PURCHASE_RECEIPT;
import static ph.txtdis.type.ModuleType.RETURN_ORDER;
import static ph.txtdis.type.ModuleType.SALES_ORDER;
import static ph.txtdis.type.ModuleType.SALES_RETURN;
import static ph.txtdis.type.PartnerType.VENDOR;
import static ph.txtdis.type.QualityType.BAD;
import static ph.txtdis.type.QualityType.GOOD;
import static ph.txtdis.type.ScriptType.BILLING_APPROVAL;
import static ph.txtdis.type.UserType.AUDITOR;
import static ph.txtdis.type.UserType.LEAD_CHECKER;
import static ph.txtdis.type.UserType.MANAGER;
import static ph.txtdis.type.UserType.SELLER;
import static ph.txtdis.type.UserType.STORE_KEEPER;
import static ph.txtdis.util.DateTimeUtils.toDateDisplay;
import static ph.txtdis.util.NumberUtils.HUNDRED;
import static ph.txtdis.util.NumberUtils.divide;
import static ph.txtdis.util.NumberUtils.formatCurrency;
import static ph.txtdis.util.NumberUtils.formatId;
import static ph.txtdis.util.NumberUtils.isNegative;
import static ph.txtdis.util.NumberUtils.isPositive;
import static ph.txtdis.util.NumberUtils.isZero;
import static ph.txtdis.util.NumberUtils.toPercentRate;
import static ph.txtdis.util.SpringUtil.isUser;
import static ph.txtdis.util.SpringUtil.username;
import static ph.txtdis.util.TextUtils.nullIfEmpty;
import static ph.txtdis.util.Util.areEqual;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Billable;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.dto.Booking;
import ph.txtdis.dto.Channel;
import ph.txtdis.dto.CreationTracked;
import ph.txtdis.dto.CreditDetail;
import ph.txtdis.dto.Customer;
import ph.txtdis.dto.CustomerDiscount;
import ph.txtdis.dto.CustomerReceivable;
import ph.txtdis.dto.EntityDecisionNeeded;
import ph.txtdis.dto.Inventory;
import ph.txtdis.dto.InvoiceBooklet;
import ph.txtdis.dto.Item;
import ph.txtdis.dto.ItemFamily;
import ph.txtdis.dto.Keyed;
import ph.txtdis.dto.Payment;
import ph.txtdis.dto.Price;
import ph.txtdis.dto.PricingType;
import ph.txtdis.dto.QtyPerUom;
import ph.txtdis.dto.SalesforceAccount;
import ph.txtdis.dto.SalesforceEntity;
import ph.txtdis.dto.SalesforceSalesInfo;
import ph.txtdis.dto.VolumeDiscount;
import ph.txtdis.exception.AlreadyBilledBookingException;
import ph.txtdis.exception.BadCreditException;
import ph.txtdis.exception.DateInThePastException;
import ph.txtdis.exception.DeactivatedException;
import ph.txtdis.exception.DifferentDiscountException;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.exception.ExceededCreditLimitException;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.GapInSerialInvoiceIdException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.NotAllowedOffSiteTransactionException;
import ph.txtdis.exception.NotAnItemToBeSoldToCustomerException;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.exception.NotNextWorkDayException;
import ph.txtdis.exception.NotPickedBookingIdException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;
import ph.txtdis.exception.ToBeReturnedItemNotPurchasedWithinTheLastSixMonthException;
import ph.txtdis.exception.UnauthorizedUserException;
import ph.txtdis.exception.UnbilledPickedSalesOrderException;
import ph.txtdis.exception.UnissuedInvoiceIdException;
import ph.txtdis.info.SuccessfulSaveInfo;
import ph.txtdis.salesforce.SalesInfoUploader;
import ph.txtdis.type.ModuleType;
import ph.txtdis.type.PaymentType;
import ph.txtdis.type.QualityType;
import ph.txtdis.type.ScriptType;
import ph.txtdis.type.UomType;
import ph.txtdis.type.VolumeDiscountType;
import ph.txtdis.util.TypeMap;

@Service("billableService")
public class BillableService implements BilledAllPickedSalesOrder, CreationTracked, Detailed, ItemBased<BillableDetail>,
		DecisionNeeded, LatestCredit, NextWorkDayDated, Reset, SalesforceUploadable<SalesforceSalesInfo>, Serviced<Long> {

	private class AlreadyReceivedBookingIdException extends Exception {

		private static final long serialVersionUID = -8123802272614410524L;

		public AlreadyReceivedBookingIdException(String reference, Long id, Long receivingId) {
			super(reference + " No. " + id + "'s items\n"//
					+ "have been received in its\n"//
					+ "R/R No. " + receivingId);
		}
	}

	private class AlreadyReferencedBookingIdException extends Exception {

		private static final long serialVersionUID = -1428885265318163309L;

		public AlreadyReferencedBookingIdException(Long id, Billable b) {
			super("S/O No. " + id + "\nhas been used in\n" + b);
		}
	}

	private class IncompleteOrErroneousCustomerDataException extends Exception {

		private static final long serialVersionUID = -919387748993066310L;

		public IncompleteOrErroneousCustomerDataException(Customer c, String error) {
			super("No booking allowed if customers have incomplete/erroneous data;\n"//
					+ "#" + c.getId() + " " + c + "\n" + error);
		}
	}

	private class InsufficientBadOrderAllowanceException extends Exception {

		private static final long serialVersionUID = -75168436938104495L;

		public InsufficientBadOrderAllowanceException() {
			super("Not enough bad order allowance\n"//
					+ "for this item quantity");
		}
	}

	private class ItemReturningCustomerIncompleteContactDetailsException extends Exception {

		private static final long serialVersionUID = 2874806720155756184L;

		public ItemReturningCustomerIncompleteContactDetailsException(Customer c) {
			super("Item-returning customers\nmust have complete contact details;\n" + c + "'s are not");
		}
	}

	private class NoAssignedCustomerSellerException extends Exception {

		private static final long serialVersionUID = 2883475447261686961L;

		public NoAssignedCustomerSellerException(String customer) {
			super("No assigned seller for\n" + customer);
		}
	}

	private class NotAllowedToReturnBadOrderException extends Exception {

		private static final long serialVersionUID = -3135096519524601477L;

		public NotAllowedToReturnBadOrderException(Customer c) {
			super(c + "\nis not allowed to make\nBad Order Returns at this time.");
		}
	}

	private class NotApprovedPurchaseOrderException extends Exception {

		private static final long serialVersionUID = 7399769514410624900L;

		public NotApprovedPurchaseOrderException(Long id) {
			super("P/O No. " + id + "\nis NOT approved");
		}
	}

	private class NotForDeliveryReportException extends Exception {

		private static final long serialVersionUID = 8820354634366841014L;

		public NotForDeliveryReportException(String customer) {
			super("D/R can only be used for\n" + customer//
					+ "\nif no S/I has been used for a cancelled S/O");
		}
	}

	private class NotFullyPaidCashBillableException extends Exception {

		private static final long serialVersionUID = 6376789991394072985L;

		public NotFullyPaidCashBillableException(String id) {
			super("No booking if CODs have NOT been fully paid;\n"//
					+ "S/I(D/R) No. " + id + " is still not");
		}
	}

	private class NotTheAssignedCustomerSellerException extends Exception {

		private static final long serialVersionUID = 7399769514410624900L;

		public NotTheAssignedCustomerSellerException(String seller, String customer) {
			super("Only " + seller + "\ncan book for\n" + customer);
		}
	}

	private class OpenBadOrReturnOrderException extends Exception {

		private static final long serialVersionUID = -2736815712342898065L;

		public OpenBadOrReturnOrderException(Long id) {
			super("Bad/Return Order No. " + id + "\nis still open");
		}
	}

	private class PickedUpSalesOrderDateNotTodayException extends Exception {

		private static final long serialVersionUID = 7152560141778263735L;

		public PickedUpSalesOrderDateNotTodayException() {
			super("To-be-picked-up S/O's must be booked today");
		}
	}

	private class UndepositedPaymentException extends Exception {

		private static final long serialVersionUID = 4671867557387899786L;

		public UndepositedPaymentException(PaymentType t, Long id) {
			super("No booking if " + lowerCase(t.toString()) + " payments\n"//
					+ "have NOT been deposited or fund-transferred;\n"//
					+ capitalizeFully(t.toString()) + " Collection No. " + id + " is still not");
		}
	}

	private static Logger logger = getLogger(BillableService.class);

	private static final String AGED_COLUMN = "7";

	private static final String AGING_COLUMN = "8";

	@Autowired
	private CustomerReceivableService customerReceivableService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private HolidayService holidayService;

	@Autowired
	private InventoryService inventoryService;

	@Autowired
	private InvoiceBookletService bookletService;

	@Autowired
	private ItemFamilyService familyService;

	@Autowired
	private ItemService itemService;

	@Autowired
	private ReadOnlyService<Billable> billableService;

	@Autowired
	private ReadOnlyService<SalesforceSalesInfo> salesforceService;

	@Autowired
	private RemittanceService remittanceService;

	@Autowired
	private SalesInfoUploader salesInfoUploader;

	@Autowired
	private SavingService<Billable> savingService;

	@Autowired
	private ServerService serverService;

	@Autowired
	private ScriptService scriptService;

	@Autowired
	private SpunService<Billable, Long> spunService;

	@Autowired
	private TypeMap typeMap;

	@Autowired
	private VatService vatService;

	private int linesPerPage;

	private BigDecimal remainingBadOrderAllowance, remainingCredit, unitPrice, vatDivisor;

	private Billable billable;

	private BillableDetail receivingDetail;

	private Customer customer;

	private CreditDetail credit;

	private Integer onPurchaseDaysLevel, onReceiptDaysLevel;

	private Item item;

	private Inventory inventory;

	private List<BillableDetail> originalDetails;

	private List<CustomerDiscount> currentApprovedDiscounts;

	private List<VolumeDiscount> volumeDiscounts;

	private Long numId;

	private ModuleType type;

	private String prefix, idNo, suffix;

	public BillableService() {
		reset();
	}

	@Override
	public boolean canApprove() {
		return !isNew() && isAuthorizedToApprove() && !isASalesOrder();
	}

	@Override
	public boolean canReject() {
		if (isAnInvoice())
			return isUser(MANAGER);
		if (isASalesOrder())
			return isUnprinted();
		return true;
	}

	public boolean canReceiveItems() {
		return canModifyRR() || isUser(STORE_KEEPER);
	}

	public void checkforDuplicates(String id) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException, DuplicateException {
		if (billableService.module(getModule()).getOne("/" + id) != null)
			throw new DuplicateException(getModuleId() + id);
	}

	public void clearItemReturnPaymentDataSetByItsInputDialogDuringDataEntry() {
		setThreePartId(null, null, null);
	}

	@Override
	public boolean closeAppIfInvalid() {
		return isAnInvoice() && get().getIsValid() != null && !get().getIsValid();
	}

	public BillableDetail createDetail(UomType uom, BigDecimal qty) {
		BillableDetail sd = new BillableDetail();
		sd.setId(item.getId());
		sd.setItemName(item.getName());
		sd.setUom(uom);
		sd.setInitialQty(qty);
		sd.setQuality(quality());
		sd.setPriceValue(computeUnitPrice(uom, qty));
		sd.setOnPurchaseDaysLevel(onPurchaseDaysLevel);
		sd.setOnReceiptDaysLevel(onReceiptDaysLevel);
		return sd;
	}

	public Billable findById(String id) throws NumberFormatException, NoServerConnectionException,
			StoppedServerException, FailedAuthenticationException, RestException, InvalidException, NotFoundException {
		Billable b = isAnInvoice() ? findByThreePartId(id) : findById(Long.valueOf(id));
		if (b == null)
			throw new NotFoundException(getModuleId() + " No. " + id);
		return b;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Billable get() {
		if (billable == null)
			reset();
		return billable;
	}

	@Override
	public String getAlternateName() {
		switch (type) {
			case BAD_ORDER:
				return "B/O";
			case DELIVERY_REPORT:
				return "D/R";
			case INVOICE:
				return "S/I";
			case PURCHASE_ORDER:
				return "P/O";
			case PURCHASE_RECEIPT:
			case SALES_RETURN:
				return "R/R";
			case RETURN_ORDER:
				return "RMA";
			case SALES_ORDER:
				return getReferenceName();
			default:
				return null;
		}
	}

	public BigDecimal getBalance() {
		try {
			BigDecimal d = get().getUnpaidValue().abs().subtract(ONE);
			return d.compareTo(ONE) <= 0 ? null : d;
		} catch (Exception e) {
			return null;
		}
	}

	public Billable getBillable(String prefix, Long id, String suffix) throws NoServerConnectionException,
			StoppedServerException, FailedAuthenticationException, RestException, InvalidException {
		return billableService.module(getModule()).getOne("/find?prefix=" + prefix + "&id=" + id + "&suffix=" + suffix);
	}

	@Override
	public ReadOnlyService<Billable> getBillableReadOnlyService() {
		return billableService;
	}

	@Override
	@SuppressWarnings("unchecked")
	public CreditDetail getCredit() {
		if (credit == null)
			credit = getCredit(customer.getCreditDetails(), orderDate());
		return credit;
	}

	@Override
	public String getCreatedBy() {
		return isAReceiving() ? get().getReceivedBy() : get().getCreatedBy();
	}

	@Override
	public ZonedDateTime getCreatedOn() {
		return isAReceiving() ? get().getReceivedOn() : get().getCreatedOn();
	}

	public Long getCustomerId() {
		if (isAPurchaseOrder())
			try {
				setCustomer(customerService.getVendor());
				setCustomerData();
			} catch (Exception e) {
				e.printStackTrace();
				get().setCustomerId(null);
			}
		return get().getCustomerId();
	}

	@Override
	public String getDecidedBy() {
		return get().getDecidedBy();
	}

	@Override
	public ZonedDateTime getDecidedOn() {
		return get().getDecidedOn();
	}

	@Override
	public List<BillableDetail> getDetails() {
		List<BillableDetail> l = get().getDetails();
		if (l != null && isASalesReturn())
			return l.stream().filter(d -> !isZero(d.getReturnedQty())).collect(toList());
		return l == null ? emptyList() : l;
	}

	public BigDecimal getDiscountValue() {
		BigDecimal discount = ZERO;
		if (currentApprovedDiscounts != null) {
			BigDecimal gross = getGross();
			for (CustomerDiscount d : currentApprovedDiscounts) {
				discount = discount.add(gross.multiply(toPercentRate(d.getPercent())));
				gross = gross.subtract(discount);
			}
		}
		return discount;
	}

	@Override
	public String getFontIcon() {
		return getTypeMap().icon(getSpunModule());
	}

	@Override
	public String getHeaderText() {
		switch (type) {
			case BAD_ORDER:
				return "Bad Order";
			case PURCHASE_ORDER:
				return "Purchase Order";
			case PURCHASE_RECEIPT:
				return "P/O Receipt";
			case RETURN_ORDER:
				return "Return Order";
			case SALES_ORDER:
				return "Sales Order";
			case DELIVERY_REPORT:
				return "Delivery Report";
			case INVOICE:
				return "Invoice";
			case SALES_RETURN:
				return "S/O Returns";
			default:
				return "";
		}
	}

	@Override
	public Long getId() {
		return get().getId();
	}

	public Long getIdNo() {
		return isAPurchaseReceipt() ? get().getReceivingId() : get().getNumId();
	}

	public String getIdPrompt() {
		if (isASalesOrder() || isASalesReturn())
			return "S/I(D/R)";
		if (isABadOrder() || isAReturnOrder())
			return "S/I";
		return getAlternateName();
	}

	@Override
	public Boolean getIsValid() {
		return get().getIsValid();
	}

	@Override
	public Item getItem() {
		return item;
	}

	@Override
	public String getItemName() {
		return item == null ? null : item.getDescription();
	}

	@Override
	public ItemService getItemService() {
		return itemService;
	}

	@Override
	public String getModule() {
		return "billable";
	}

	public Integer getOnPurchaseDaysLevel() throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		inventory = inventoryService.getInventory(item.getId());
		return onPurchaseDaysLevel = inventory.getDaysLevel();
	}

	public Integer getOnReceiptDaysLevel(UomType uom, BigDecimal qty) {
		BigDecimal avg = avgDailySoldQty();
		BigDecimal unitQty = qty.multiply(qtyPerUom(uom));
		BigDecimal soh = onHandQty().add(unitQty);
		return onReceiptDaysLevel = isZero(avg) ? 9999 : divide(soh, avg).intValue();
	}

	@Override
	public String getOpenDialogHeading() {
		return "Open a(n) " + getHeaderText();
	}

	@Override
	public String getOrderNo() {
		switch (type) {
			case BAD_ORDER:
			case PURCHASE_ORDER:
			case RETURN_ORDER:
			case SALES_ORDER:
				return formatId(get().getBookingId());
			case DELIVERY_REPORT:
				return get().getOrderNo().replace("-", "");
			case INVOICE:
				return get().getOrderNo();
			case PURCHASE_RECEIPT:
			case SALES_RETURN:
				return formatId(get().getReceivingId());
			default:
				return formatId(get().getId());
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public ReadOnlyService<Billable> getReadOnlyService() {
		return billableService;
	}

	public List<String> getReceivableItemNames() {
		try {
			List<String> l = originalDetails.stream().map(d -> d.getItemName()).collect(toList());
			l = l.stream().filter(n -> nonReturnedItem(n)).collect(toList());
			return l;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getReferenceName() {
		if (isABadOrder())
			return "B/O";
		else if (isAPurchaseOrder() || isAPurchaseReceipt())
			return "P/O";
		else if (isAReturnOrder())
			return "R/O";
		return "S/O";
	}

	public BigDecimal getRemainingBadOrderAllowance() {
		if (isNew())
			return remainingBadOrderAllowance;
		BigDecimal badOrderAllowance = get().getBadOrderAllowanceValue();
		if (badOrderAllowance == null)
			return null;
		return badOrderAllowance.subtract(get().getTotalValue());
	}

	@Override
	public String getRemarks() {
		return get().getRemarks();
	}

	@Override
	@SuppressWarnings("unchecked")
	public SavingService<Billable> getSavingService() {
		return savingService;
	}

	@Override
	public ScriptService getScriptService() {
		return scriptService;
	}

	@Override
	public <T extends EntityDecisionNeeded<Long>> ScriptType getScriptType(T d) {
		return BILLING_APPROVAL;
	}

	@Override
	public Long getSpunId() {
		if (isNew())
			return null;
		if (isABooking())
			return get().getBookingId();
		if (isADeliveryReport())
			return get().getNumId();
		if (isAReceiving())
			return get().getReceivingId();
		return getId();
	}

	@Override
	public String getSpunModule() {
		switch (type) {
			case BAD_ORDER:
				return "badOrder";
			case PURCHASE_ORDER:
				return "purchaseOrder";
			case RETURN_ORDER:
				return "returnOrder";
			case SALES_ORDER:
				return "salesOrder";
			case DELIVERY_REPORT:
				return "deliveryReport";
			case INVOICE:
				return getModule();
			case PURCHASE_RECEIPT:
				return "purchaseReceipt";
			case SALES_RETURN:
				return "salesReturn";
			default:
				return null;
		}
	}

	@Override
	public SpunService<Billable, Long> getSpunService() {
		return spunService;
	}

	@Override
	public TypeMap getTypeMap() {
		return typeMap;
	}

	public UomType getUomOfSelectedItem(String i) {
		try {
			receivingDetail = originalDetails.stream().filter(d -> d.getItemName().equals(i)).findAny().get();
			return receivingDetail.getUom();
		} catch (Exception e) {
			return null;
		}
	}

	public BigDecimal getVat() {
		try {
			return total().subtract(getVatable());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public BigDecimal getVatable() {
		try {
			return divide(total(), getVatDivisor());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void invalidate() {
		get().setIsValid(false);
		get().setDecidedBy(username());
		get().setDecidedOn(ZonedDateTime.now());
	}

	public boolean isABadOrder() {
		return type == BAD_ORDER;
	}

	public boolean isABooking() {
		return isAPurchaseOrder() || isABadOrder() || isAReturnOrder() || isASalesOrder();
	}

	public boolean isADeliveryReport() {
		return type == DELIVERY_REPORT;
	}

	public boolean isAnInvoice() {
		return type == INVOICE;
	}

	@Override
	public boolean isAppendable() {
		if (isAPurchaseOrder() || isAPurchaseReceipt())
			return true;
		return getDetails() == null || getDetails().size() < linesPerPage();
	}

	public boolean isAPurchaseOrder() {
		return type == PURCHASE_ORDER;
	}

	public boolean isAPurchaseReceipt() {
		return type == PURCHASE_RECEIPT;
	}

	public boolean isAReceiving() {
		return isASalesReturn() || isAPurchaseReceipt();
	}

	public boolean isAReturnOrder() {
		return type == RETURN_ORDER;
	}

	public boolean isASalesOrder() {
		return type == SALES_ORDER;
	}

	public boolean isASalesReturn() {
		return type == SALES_RETURN;
	}

	@Override
	public boolean isNew() {
		if (isABillable())
			return get().getBilledOn() == null;
		return isAReceiving() ? get().getReceivedOn() == null : getCreatedOn() == null;
	}

	public List<Booking> listUnpicked(LocalDate d, String truck) {
		try {
			List<Billable> al = billableService.module(getModule()).getList("/unpicked?date=" + d + "&truck=" + truck);
			return al.stream().map(a -> toBooking(a)).collect(toList());
		} catch (Exception e) {
			return emptyList();
		}
	}

	public List<BillableDetail> nonZeroReturnedQtyBillableDetails() {
		try {
			return get().getDetails().stream().filter(d -> !isZero(returnedQty(d))).collect(toList());
		} catch (Exception e) {
			e.printStackTrace();
			return emptyList();
		}
	}

	@Override
	public void reset() {
		nullifyAll();
		set(new Billable());
	}

	public boolean returnIsValid() {
		Boolean b = get().getIsValid();
		return b != null && b;
	}

	public boolean salesOrderCanBeModified() {
		return isASalesOrder() //
				&& (isNew() || get().getPrintedOn() != null);
	}

	public boolean salesReturnCanBeModified() {
		return isASalesReturn() //
				&& ((isNew() && canReceiveItems()) //
						|| (canModifyRR() && get().getReceivedOn() != null && get().getBilledOn() == null));
	}

	@Override
	public void save() throws SuccessfulSaveInfo, NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, InvalidException {
		if (isAReceiving())
			get().setReceivedBy(username());
		else if (isAReturnOrder())
			get().setIsRma(true);
		else if (isABadOrder())
			get().setIsRma(false);
		else if (isADeliveryReport() || (isAnInvoice() && get().getIsValid() == null))
			get().setBilledBy(username());
		set(save(get()));
		scriptService.saveScripts();
		throw new SuccessfulSaveInfo(saveInfo());
	}

	public Billable save(Billable t)
			throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException, InvalidException {
		return savingService.module(getModule()).save(t);
	}

	public void saveDisposalData() throws NotAllowedOffSiteTransactionException {
		logger.info("allowOnlyIfOnSite@saveDisposalData()");
		if (isNew() && isOffSite())
			throw new NotAllowedOffSiteTransactionException();
		get().setReceivingModifiedBy(username());
	}

	public void setItemReturnPaymentData(LocalDate d) {
		get().setOrderDate(d);
		get().setBilledBy(username());
		get().setUnpaidValue(ZERO);
		get().setFullyPaid(true);
		scriptService.set(ScriptType.RETURN_PAYMENT, createItemReturnPaymentScript(d));
	}

	private String createItemReturnPaymentScript(LocalDate d) {
		return getId() + "|" + d + "|" + get().getPrefix() + "|" + get().getNumId() + "|" + get().getSuffix() + "|"
				+ username() + "|" + ZonedDateTime.now();
	}

	public void saveItemReturnReceiptData() throws Exception {
		logger.info("allowOnlyIfOnSite@saveItemReturnReceiptData()");
		if (isNew() && isOffSite())
			throw new NotAllowedOffSiteTransactionException();
		get().setReceivedBy(username());
	}

	@Override
	public <T extends Keyed<Long>> void set(T t) {
		if (t != null) {
			billable = (Billable) t;
			customer = getCustomer();
			if (salesReturnCanBeModified())
				originalDetails = billable.getDetails();
		}
	}

	public void setCustomerRelatedData() {
		setCustomerData();
		get().setDueDate(dueDate());
		get().setDetails(null);
	}

	public void setItemUponValidation(long id) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException, NotFoundException,
			AlreadyBilledBookingException, DuplicateException, NotAnItemToBeSoldToCustomerException,
			DifferentDiscountException, ToBeReturnedItemNotPurchasedWithinTheLastSixMonthException {
		if (id != 0) {
			item = null;
			item = verifyItem(id);
		}
	}

	public void setOrderDateUponValidation(LocalDate d) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException, IncompleteOrErroneousCustomerDataException,
			DateInThePastException, UnauthorizedUserException, UnbilledPickedSalesOrderException,
			NotFullyPaidCashBillableException, NotAllowedOffSiteTransactionException {
		logger.info("allowOnlyIfOnSite@setOrderDateUponValidation(LocalDate)");
		if (isNew() && isOffSite())
			throw new NotAllowedOffSiteTransactionException();
		if (!isUser(MANAGER)) {
			verifyNotInThePast(d);
			verifyUserAuthorization();
			// verifyCustomersHaveCompleteAndCorrectData();
			// verifyAllPickedSalesOrderHaveBeenBilled(d);
			// verifyAllCashBillablesHaveBeenFullyPaid(d);
		}
		get().setOrderDate(d);
	}

	public void setQtyUponValidation(UomType uom, BigDecimal qty) throws InsufficientBadOrderAllowanceException {
		if (!isABadOrder())
			return;
		BigDecimal subtotal = computeUnitPrice(uom, qty).multiply(qty);
		BigDecimal balance = remainingBadOrderAllowance.subtract(subtotal);
		if (balance.compareTo(ZERO) <= 0)
			throw new InsufficientBadOrderAllowanceException();
		remainingBadOrderAllowance = balance;
	}

	public void setThreePartId(String prefix, Long id, String suffix) {
		get().setPrefix(nullIfEmpty(prefix));
		get().setNumId(id);
		get().setSuffix(nullIfEmpty(suffix));
	}

	public void setType(ModuleType t) {
		type = t;
	}

	@Override
	public void updatePerValidity(Boolean isValid, String remarks) {
		if (isValid != null)
			try {
				if (isAnInvoice() && !isValid)
					remittanceService.nullifyPaymentData(get());
				DecisionNeeded.super.updatePerValidity(isValid, remarks);
			} catch (Throwable e) {
				e.printStackTrace();
			}
	}

	public void overrideInvalidation() throws SuccessfulSaveInfo, NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, InvalidException {
		set(setDecisionStatus(get(), null, ""));
		logger.info("Is valid = " + getIsValid());
		logger.info("Decided by = " + getDecidedBy());
		logger.info("Decided on = " + getDecidedOn());
		logger.info("Remarks = " + getRemarks());
		save();
	}

	@Override
	public String decisionTag(Boolean isValid) {
		String s = "VALID";
		if (isValid == null) {
			if (!isASalesOrder())
				return "";
			s = "OVERRULED";
		} else if (isABadOrReturnOrder()) {
			if (isValid)
				s = "OK FOR PICK-UP";
			else
				s = "DISAPPROVED";
		} else if (!isValid)
			s = "IN" + s;
		return "[" + s + ": " + username() + " - " + toDateDisplay(now()) + "] ";
	}

	public BillableDetail updateReceivingDetailReturnedQty(BigDecimal qty) {
		receivingDetail.setReturnedQty(qty);
		return receivingDetail;
	}

	public void updateSummaries(List<BillableDetail> l) {
		if (!(isAnInvoice() && isADeliveryReport()))
			get().setDetails(l);
		if ((isNew() && (isAPurchaseOrder() || isASalesOrder() || isABadOrder())) || isAnInvoice() || isADeliveryReport())
			updateTotals();
		else if (salesReturnCanBeModified())
			get().setReceivingModifiedBy(username());
		computeUnpaid();
	}

	public void updateUponBookingIdValidation(long id) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException, NotApprovedPurchaseOrderException,
			NotFoundException, NotForDeliveryReportException, AlreadyReceivedBookingIdException,
			AlreadyReferencedBookingIdException, NotPickedBookingIdException, NotAllowedOffSiteTransactionException {
		logger.info("allowOnlyIfOnSite@updateUponBookingIdValidation(id)");
		if (isNew() && isOffSite())
			throw new NotAllowedOffSiteTransactionException();
		Billable b = validateBooking(id);
		updateBasedOnBooking(b);
	}

	public void updateUponCustomerIdValidation(Long id) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException, NotFoundException, DeactivatedException,
			BadCreditException, ExceededCreditLimitException, NotAllowedOffSiteTransactionException,
			PickedUpSalesOrderDateNotTodayException, NotNextWorkDayException, NoAssignedCustomerSellerException,
			NotTheAssignedCustomerSellerException, OpenBadOrReturnOrderException, NotAllowedToReturnBadOrderException,
			UndepositedPaymentException, ItemReturningCustomerIncompleteContactDetailsException {
		logger.info("allowOnlyIfOnSite@updateUponCustomerIdValidation(id)");
		if (isNew() && isOffSite())
			throw new NotAllowedOffSiteTransactionException();
		setCustomer(findCustomerById(id));
		verifyNonPickupSalesOrderDateIsNextWorkingDate();
		verifyCurrentUserIsTheCustomerAssignedSeller();
		verifyAllCollectionsHaveBeenDeposited(PaymentType.CASH);
		verifyAllCollectionsHaveBeenDeposited(PaymentType.CHECK);
		verifyItemReturningCustomerHasCompleteContactDetails();
		verifyCustomerHasNoOpenBadOrReturnOrder();
		verifyCustomerHasBadOrderReturnAllowance();
		verifyCustomerHasNoOverdues();
		verifyCustomerHasNotExceededItsCreditLimit(ZERO);
		setCustomerRelatedData();
	}

	private Customer findCustomerById(Long id) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException, NotFoundException {
		Customer c = customerService.find(id);
		if (c.getDeactivatedOn() != null)
			throw new DeactivatedException(c.getName());
		return c;
	}

	public void updateUponOrderNoValidation(String prefix, Long id, String suffix) throws Exception {
		logger.info("allowOnlyIfOnSite@updateUponOrderNoValidation(prefix, id, suffix)");
		if (isNew() && isOffSite())
			throw new NotAllowedOffSiteTransactionException();
		checkforDuplicates(prefix, id, suffix);
		// verifyIdIsPartOfAnIssuedBookletImmediatelyPrecedingItsLast(prefix,
		// id, suffix);
		setThreePartId(prefix, id, suffix);
	}

	public void verifyCustomerHasNotExceededItsCreditLimit(BigDecimal additionalCredit)
			throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException, RestException,
			InvalidException, NotFoundException, ExceededCreditLimitException {
		if (isUser(MANAGER))
			return;
		computeRemainingCredit(additionalCredit);
		if (isNegative(remainingCredit))
			throw new ExceededCreditLimitException(customer, getCreditLimit(), remainingCredit());
	}

	@Override
	public Item verifyItem(Long id) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException, NotFoundException,
			AlreadyBilledBookingException, DuplicateException, NotAnItemToBeSoldToCustomerException,
			DifferentDiscountException, ToBeReturnedItemNotPurchasedWithinTheLastSixMonthException {
		confirmTransactionIsAllowed();
		Item i = confirmItemExistsAndIsNotDeactivated(id);
		confirmItemIsNotOnList(i);
		confirmItemIsNotOnOtherBookingWithTheSameCustomerAndDate(i);
		confirmItemIsAllowedToBeSoldToCurrentCustomer(i);
		confirmItemDiscountEqualsCurrent(i);
		confirmItemTobeReturnedHasBeenPurchasedBefore(i);
		return i;
	}

	private BigDecimal agedValue() throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException, NotFoundException, DeactivatedException {
		return sumUnpaid(customerReceivableService.listReceivables(//
				customer.getId().toString(), //
				AGED_COLUMN, //
				null));
	}

	private BigDecimal agingValue() throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException, NotFoundException {
		String id = customer.getId().toString();
		List<CustomerReceivable> list = customerReceivableService.listReceivables(id, AGING_COLUMN, null);
		return sumUnpaid(list);
	}

	private boolean allItemsReturned(Billable b) {
		if (b.getReceivedOn() == null)
			return false;
		return !b.getDetails().stream().anyMatch(d -> notAllReturned(d));
	}

	private boolean areChannelLimitsEqual(VolumeDiscount vd) {
		return areEqual(vd.getChannelLimit(), customer.getChannel());
	}

	private boolean areDiscountsEqual(Item i) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		List<CustomerDiscount> otherDiscounts = getLatestApprovedDiscounts(i);
		if (currentApprovedDiscounts.size() != otherDiscounts.size())
			return false;
		if (currentApprovedDiscounts.isEmpty())
			return true;
		return discountValues(currentApprovedDiscounts).equals(discountValues(otherDiscounts));
	}

	private BigDecimal avgDailySoldQty() {
		BigDecimal avg = inventory.getAvgDailySoldQty();
		return avg == null ? ZERO : avg;
	}

	private String bookingModule() {
		switch (type) {
			case BAD_ORDER:
				return "badOrder";
			case PURCHASE_ORDER:
			case PURCHASE_RECEIPT:
				return "purchaseOrder";
			case RETURN_ORDER:
				return "returnOrder";
			case DELIVERY_REPORT:
			case INVOICE:
			case SALES_RETURN:
				return "salesOrder";
			default:
				return null;
		}
	}

	private boolean canModifyRR() {
		return isUser(MANAGER) || isUser(LEAD_CHECKER);
	}

	private void checkforDuplicates(String prefix, Long id, String suffix)
			throws DuplicateException, NoServerConnectionException, StoppedServerException, FailedAuthenticationException,
			RestException, InvalidException {
		Billable i = getBillable(prefix, id, suffix);
		if (i != null)
			throw new DuplicateException(getModuleId() + id);
	}

	private Comparator<VolumeDiscount> compareSetTypeVolumeDiscountChannelLimits() {
		return (a, b) -> compareVolumeDiscountChannelLimits(a, b);
	}

	private Comparator<VolumeDiscount> compareTierTypeVolumeDiscountChannelLimitsThenReverseCutOff() {
		return (a, b) -> reverseCompareCutOffsWhenChannelLimitsAreEqual(a, b);
	}

	private int compareVolumeDiscountChannelLimits(VolumeDiscount d1, VolumeDiscount d2) {
		Channel c1 = d1.getChannelLimit();
		Channel c2 = d2.getChannelLimit();
		if (c1 == null)
			return c2 == null ? 0 : 1;
		return c2 == null ? -1 : c1.getName().compareTo(c2.getName());
	}

	private BigDecimal computeDiscountedPrice(BigDecimal qty) {
		if (item.getLatestVolumeDiscount(orderDate()) == null)
			return unitPrice;
		setVolumeDiscounts();
		if (volumeDiscounts.get(0).getType() == VolumeDiscountType.TIER)
			return getVolumeDiscountOfTierTypePrice(qty);
		return getVolumeDiscountOfSetTypePrice(qty);
	}

	private BigDecimal computeGross() {
		try {
			BigDecimal gross = getDetails().stream().map(d -> computeSubtotal(d)).reduce(ZERO, BigDecimal::add);
			return isNegative(gross) ? null : gross;
		} catch (Exception e) {
			return null;
		}
	}

	private void computeRemainingCredit(BigDecimal additionalCredit) throws NoServerConnectionException,
			StoppedServerException, FailedAuthenticationException, RestException, InvalidException, NotFoundException {
		if (remainingCredit == null)
			remainingCredit = getCreditLimit().subtract(agingValue());
		remainingCredit = remainingCredit.subtract(additionalCredit);
	}

	private BigDecimal computeSubtotal(BillableDetail d) {
		return d.getQty().multiply(d.getPriceValue());
	}

	private BigDecimal computeTotal() {
		return getGross().subtract(getDiscountValue());
	}

	private BigDecimal computeUnitPrice(UomType uom, BigDecimal qty) {
		BigDecimal qtyPerUom = getQtyPerUom(uom);
		BigDecimal discountedPrice = computeDiscountedPrice(qty.multiply(qtyPerUom));
		return discountedPrice.multiply(qtyPerUom);
	}

	private void computeUnpaid() {
		if (get().getPayments() == null || get().getPayments().isEmpty())
			get().setUnpaidValue(total());
	}

	private void confirmBookingExists(long id, Billable b) throws NotFoundException {
		if (b == null)
			throw new NotFoundException(getReferenceName() + " No. " + id);
	}

	private void confirmBookingHasBeenPicked(Billable b, Long id) throws NotPickedBookingIdException {
		if (b.getTruck() == null)
			throw new NotPickedBookingIdException(id);
	}

	private void confirmBookingIsStillBillable(long id, Billable b)
			throws AlreadyReferencedBookingIdException, NotPickedBookingIdException {
		confirmBookingIsUnreferenced(id, b);
		confirmBookingHasBeenPicked(b, id);
	}

	private void confirmBookingIsStillOpen(long id, Billable b)
			throws AlreadyReceivedBookingIdException, AlreadyReferencedBookingIdException, NotPickedBookingIdException {
		if (isABillable())
			confirmBookingIsStillBillable(id, b);
		else
			confirmBookingIsStillReceivable(id, b);
	}

	private void confirmBookingIsStillReceivable(long id, Billable b) throws AlreadyReceivedBookingIdException {
		Long receivingId = b.getReceivingId();
		if (receivingId != null)
			throw new AlreadyReceivedBookingIdException(getReferenceName(), id, receivingId);
	}

	private void confirmBookingIsUnreferenced(long id, Billable b) throws AlreadyReferencedBookingIdException {
		if (!isZero(b.getNumId()))
			throw new AlreadyReferencedBookingIdException(id, b);
	}

	private void confirmDeliveryReportingIsAllowed(Billable b)
			throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException, RestException,
			InvalidException, NotFoundException, NotForDeliveryReportException {
		if (!isADeliveryReport())
			return;
		if (!isForDR(b))
			throw new NotForDeliveryReportException(b.getCustomerName());
	}

	private void confirmItemDiscountEqualsCurrent(Item i) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException, DifferentDiscountException {
		if (customerNeverHadDiscountsOrItemIsFirstTableEntry(i) || isABadOrder() || isAPurchaseOrder())
			return;
		if (!areDiscountsEqual(i))
			throw new DifferentDiscountException();
	}

	private void confirmItemIsAllowedToBeSoldToCurrentCustomer(Item item) throws NotAnItemToBeSoldToCustomerException {
		try {
			setLatestPrice(item);
		} catch (Exception e) {
			e.printStackTrace();
			throw new NotAnItemToBeSoldToCustomerException(item, customer);
		}
	}

	private void confirmItemIsNotOnOtherBookingWithTheSameCustomerAndDate(Item i) throws NoServerConnectionException,
			StoppedServerException, FailedAuthenticationException, RestException, InvalidException, DuplicateException {
		if (isUser(MANAGER) //
				|| isAReturnOrder() //
				|| isABadOrder()//
				|| (isASalesOrder() && (!isNew() || customer.getType() == VENDOR)))
			return;
		Billable b = billableService.module(getModule())
				.getOne("/item?id=" + i.getId() + "&customer=" + customer.getId() + "&date=" + orderDate());
		if (b != null)
			throw new DuplicateException(
					i.getName() + " booked for\n" + b.getCustomerName() + " on " + toDateDisplay(b.getOrderDate()));
	}

	private void confirmItemTobeReturnedHasBeenPurchasedBefore(Item i)
			throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException, RestException,
			InvalidException, ToBeReturnedItemNotPurchasedWithinTheLastSixMonthException {
		Billable b = billableService.module(getModule())
				.getOne("/purchased?by=" + customer.getId() + "&item=" + i.getId());
		if (b == null)
			throw new ToBeReturnedItemNotPurchasedWithinTheLastSixMonthException(i, customer);
	}

	private void confirmPurchaseOrderIsApprovedBeforeReceipt(long id, Billable b)
			throws NotApprovedPurchaseOrderException {
		if (isAPurchaseReceipt() && (b.getIsValid() == null || !b.getIsValid()))
			throw new NotApprovedPurchaseOrderException(id);
	}

	private void confirmTransactionIsAllowed() throws AlreadyBilledBookingException {
		String orderNo = get().getOrderNo();
		if (isASalesReturn() && !orderNo.isEmpty())
			throw new AlreadyBilledBookingException(get().getBookingId(), orderNo);
	}

	private String createEachLevelDiscountText(CustomerDiscount d, BigDecimal total, BigDecimal net) {
		BigDecimal perLevel = net.multiply(toPercentRate(d.getPercent()));
		total = total.add(perLevel);
		net = net.subtract(total);
		return "[" + d.getLevel() + ": " + d.getPercent() + "%] " + formatCurrency(perLevel);
	}

	private String customerName() {
		return customer.getName();
	}

	private boolean customerNeverHadDiscountsOrItemIsFirstTableEntry(Item i) throws NoServerConnectionException,
			StoppedServerException, FailedAuthenticationException, RestException, InvalidException {
		if (currentApprovedDiscounts != null && !getDetails().isEmpty())
			return currentApprovedDiscounts.isEmpty();
		currentApprovedDiscounts = getLatestApprovedDiscounts(i);
		get().setDiscountIds(listDiscountIds());
		return true;
	}

	private List<BigDecimal> discountValues(List<CustomerDiscount> l) {
		return l.stream().map(d -> d.getPercent()).collect(toList());
	}

	private LocalDate dueDate() {
		return orderDate().plusDays(getCreditTerm());
	}

	private Billable findById(Long id) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		return billableService.module(getModule()).getOne("/" + getSpunModule() + "?id=" + id);
	}

	private Billable findByThreePartId(String id) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException, NotFoundException {
		setThreePartIdFromOrderNo(id);
		Billable b = billableService.module(getModule())
				.getOne("/find?prefix=" + prefix + "&id=" + idNo + "&suffix=" + suffix);
		return b;
	}

	private Customer getCustomer() {
		try {
			return customerService.find(billable.getCustomerId());
		} catch (Exception e) {
			return null;
		}
	}

	private BigDecimal getCutOff(VolumeDiscount vd) {
		BigDecimal cutoff = new BigDecimal(vd.getCutoff());
		BigDecimal qtyPerUom = getQtyPerUom(vd.getUom());
		return cutoff.multiply(qtyPerUom);
	}

	private List<String> getEachLevelDiscountTextList(List<String> list) {
		BigDecimal net = getGross();
		currentApprovedDiscounts.forEach(d -> list.add(createEachLevelDiscountText(d, ZERO, net)));
		return list;
	}

	private BigDecimal getGross() {
		BigDecimal g = get().getGrossValue();
		return g == null ? ZERO : g;
	}

	private ItemFamily getHighestTierFamilyAmongApprovedDiscountLimits(List<ItemFamily> families) {
		try {
			return getLatestApprovedCustomerDiscountStream()//
					.filter(cd -> families.contains(cd.getFamilyLimit()))//
					.map(cd -> cd.getFamilyLimit()).max(ItemFamily::compareTo).get();
		} catch (Exception e) {
			return null;
		}
	}

	private Stream<CustomerDiscount> getLatestApprovedCustomerDiscountStream() {
		try {
			return customer.getCustomerDiscounts().stream()
					.filter(p -> isApprovedAndStartDateIsNotInTheFuture(p, orderDate()));
		} catch (Exception e) {
			return Stream.empty();
		}
	}

	private List<CustomerDiscount> getLatestApprovedDiscounts(Item i) throws NoServerConnectionException,
			StoppedServerException, FailedAuthenticationException, RestException, InvalidException {
		List<ItemFamily> l = familyService.getItemAncestry(i);
		ItemFamily f = getHighestTierFamilyAmongApprovedDiscountLimits(l);
		return getLatestApprovedDiscounts(f);
	}

	private List<CustomerDiscount> getLatestApprovedDiscounts(ItemFamily f) {
		return getLatestApprovedFamilyFilteredCustomerDiscountStream(f)
				.filter(cd -> cd.getStartDate().isEqual(getStartDateOfLatestDiscount(f))).collect(toList());
	}

	private Stream<CustomerDiscount> getLatestApprovedFamilyFilteredCustomerDiscountStream(ItemFamily family) {
		return getLatestApprovedCustomerDiscountStream().filter(cd -> areEqual(cd.getFamilyLimit(), family));
	}

	private Billable getNotFullyPaidCOD(LocalDate d) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		return billableService.module(getModule()).getOne("/notFullyPaidCOD?seller=" + username() + "&upTo=" + d);
	}

	private Optional<Price> getOptionalPrice(Item i, PricingType t) {
		return i.getPriceList().stream()//
				.filter(p -> p.getType().equals(t) && isApprovedAndStartDateIsNotInTheFuture(p, orderDate()))
				.max(Price::compareTo);
	}

	private BigDecimal getQtyPerUom(UomType uom) {
		for (QtyPerUom qpu : item.getQtyPerUomList())
			if (qpu.getUom() == uom)
				return qpu.getQty();
		return null;
	}

	private LocalDate getStartDateOfLatestDiscount(ItemFamily family) {
		try {
			return getLatestApprovedFamilyFilteredCustomerDiscountStream(family).max(CustomerDiscount::compareTo).get()
					.getStartDate();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private String getTotalInText(BigDecimal t) {
		return "[TOTAL] " + formatCurrency(t);
	}

	private BigDecimal getVatDivisor() throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		if (vatDivisor == null)
			setVatDivisor(ONE.add(vatService.vatRate()));
		return vatDivisor;
	}

	private BigDecimal getVolumeDiscountOfSetTypePrice(BigDecimal qty) {
		BigDecimal discount = ZERO;
		volumeDiscounts.sort(compareSetTypeVolumeDiscountChannelLimits());
		for (VolumeDiscount vd : volumeDiscounts)
			if (areChannelLimitsEqual(vd) || isAnAllChannelVolumeDiscount(vd)) {
				BigDecimal discountSet = qty.divideToIntegralValue(getCutOff(vd));
				discount = discountSet.multiply(vd.getDiscount());
				break;
			}
		BigDecimal net = qty.multiply(unitPrice).subtract(discount);
		return net.divide(qty, 8, HALF_EVEN);
	}

	private BigDecimal getVolumeDiscountOfTierTypePrice(BigDecimal qty) {
		BigDecimal unitDiscount = ZERO;
		volumeDiscounts.sort(compareTierTypeVolumeDiscountChannelLimitsThenReverseCutOff());
		for (VolumeDiscount vd : volumeDiscounts)
			if (getCutOff(vd).compareTo(qty) <= 0 && (areChannelLimitsEqual(vd) || isAnAllChannelVolumeDiscount(vd))) {
				unitDiscount = vd.getDiscount().divide(getQtyPerUom(vd.getUom()), 8, RoundingMode.HALF_EVEN);
				break;
			}
		return unitPrice.subtract(unitDiscount);
	}

	private boolean hasLessThanAHundredBadOrderAllowance() throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		remainingBadOrderAllowance = ZERO;
		BigDecimal badOrder = totalValue("badOrders");
		BigDecimal netRevenue = netRevenue();
		logger.info("Total net revenue = " + netRevenue);
		logger.info("Total bad orders = " + badOrder);
		remainingBadOrderAllowance = divide(netRevenue, HUNDRED).subtract(badOrder);
		logger.info("Remaining bad order = " + remainingBadOrderAllowance);
		get().setBadOrderAllowanceValue(remainingBadOrderAllowance);
		return remainingBadOrderAllowance.compareTo(HUNDRED) < 0;
	}

	private boolean isABadOrReturnOrder() {
		return isABadOrder() || isAReturnOrder();
	}

	private boolean isABillable() {
		return isAnInvoice() || isADeliveryReport();
	}

	private boolean isAnAllChannelVolumeDiscount(VolumeDiscount vd) {
		return vd.getChannelLimit() == null;
	}

	private boolean isAPickUpSales() {
		return customer.getRoute().getType() == PICK_UP;
	}

	private boolean isAuthorizedToApprove() {
		if (isUser(MANAGER))
			return true;
		return isABillable() && isUser(AUDITOR);
	}

	private boolean isForDR(Billable b) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException, NotFoundException {
		Customer c = customerService.find(b.getCustomerId());
		return c.getChannel().getType() == DELIVERY ? true : allItemsReturned(b);
	}

	private Long latestUsedIdInBooklet(InvoiceBooklet b) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		Billable i = billableService.module(getModule())
				.getOne("/latest?prefix=" + b.getPrefix()//
						+ "&suffix=" + b.getSuffix() //
						+ "&start=" + b.getStartId() //
						+ "&end=" + b.getEndId());
		return i == null ? b.getStartId() - 1 : i.getNumId();
	}

	private int linesPerPage() {
		if (linesPerPage == 0)
			linesPerPage = bookletService.getLinesPerPage();
		return linesPerPage;
	}

	private List<Long> listDiscountIds() {
		return currentApprovedDiscounts.stream().map(d -> d.getId()).collect(toList());
	}

	private List<String> listDiscounts() {
		if (isABillable())
			recomputeDiscounts();
		if (isZero(getDiscountValue()))
			return null;
		ArrayList<String> l = new ArrayList<>();
		if (currentApprovedDiscounts.size() > 1)
			l.add(getTotalInText(getDiscountValue()));
		return getEachLevelDiscountTextList(l);
	}

	private BigDecimal netRevenue() throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		BigDecimal sold = totalValue("billed");
		BigDecimal returned = totalValue("returnOrders");
		logger.info("Total sold = " + sold);
		logger.info("Total returned = " + returned);
		return sold.subtract(returned);
	}

	private boolean nonReturnedItem(String n) {
		return !getDetails().stream().anyMatch(d -> d.getReturnedQty() != null && n.equals(d.getItemName()));
	}

	private boolean notAllReturned(BillableDetail d) {
		try {
			return d.getInitialQty().compareTo(d.getReturnedQty()) > 0;
		} catch (Exception e) {
			return true;
		}
	}

	private void nullifyAll() {
		setCustomer(null);
		currentApprovedDiscounts = null;
		idNo = null;
		inventory = null;
		item = null;
		linesPerPage = 0;
		numId = null;
		onPurchaseDaysLevel = null;
		onReceiptDaysLevel = null;
		prefix = null;
		receivingDetail = null;
		remainingBadOrderAllowance = null;
		suffix = null;
		unitPrice = null;
		vatDivisor = null;
		volumeDiscounts = null;
	}

	private BigDecimal onHandQty() {
		BigDecimal soh = inventory.getGoodQty();
		return soh == null ? ZERO : soh;
	}

	private LocalDate orderDate() {
		if (isABadOrReturnOrder())
			return LocalDate.now();
		return get().getOrderDate();
	}

	private BigDecimal qtyPerUom(UomType uom) {
		try {
			return item.getQtyPerUomList().stream()//
					.filter(q -> q.getUom() == uom)//
					.findAny().get().getQty();
		} catch (Exception e) {
			return ZERO;
		}
	}

	private QualityType quality() {
		return isABadOrder() ? BAD : GOOD;
	}

	private void recomputeDiscounts() {
		try {
			Item i = itemService.find(get().getDetails().get(0).getId());
			currentApprovedDiscounts = getLatestApprovedDiscounts(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private BigDecimal remainingCredit() {
		return remainingCredit == null ? ZERO : remainingCredit.abs();
	}

	private BigDecimal returnedQty(BillableDetail d) {
		BigDecimal r = d.getReturnedQty();
		return r == null ? ZERO : r;
	}

	private int reverseCompareCutOffsWhenChannelLimitsAreEqual(VolumeDiscount a, VolumeDiscount b) {
		int comp = compareVolumeDiscountChannelLimits(a, b);
		return comp != 0 ? comp : valueOf(b.getCutoff()).compareTo(valueOf(a.getCutoff()));
	}

	private String saveInfo() {
		if (getOrderNo().equals("0"))
			return getAlternateName() + " invalidation";
		return getModuleId() + getOrderNo();
	}

	private void saveThreePartId() {
		prefix = get().getPrefix();
		numId = get().getNumId();
		suffix = get().getSuffix();
	}

	private void setBillableData(Billable b) {
		if (isAPurchaseReceipt())
			b.getDetails().forEach(d -> d.setReturnedQty(d.getQty()));
		if (isASalesReturn())
			get().setDetails(null);
		set(b);
	}

	private void setCustomer(Customer c) {
		customer = c;
		credit = null;
		remainingCredit = null;
	}

	private void setCustomerData() {
		get().setCustomerId(customer.getId());
		get().setCustomerName(customerName());
		get().setCustomerAddress(customer.getAddress());
	}

	private void setDeliveryId(String id) {
		setInvoiceId("", "-" + id, "");
	}

	private void setInvoiceId(String id) throws NotFoundException {
		String[] ids = split(id, "-");
		if (ids == null || ids.length == 0 || ids.length > 2)
			throw new NotFoundException(getModuleId() + id);
		if (ids.length == 1)
			setInvoiceIdWithoutPrefix(id, ids);
		else
			setInvoiceIdWithPrefix(id, ids);
	}

	private void setInvoiceId(String... ids) {
		prefix = ids[0];
		idNo = ids[1];
		suffix = ids[2];
	}

	private void setInvoiceIdWithNumbersOnly(String orderNo, String idNo) throws NotFoundException {
		if (!isNumeric(idNo.replace("-", "")))
			throw new NotFoundException(getModuleId() + orderNo);
		setInvoiceId("", idNo, "");
	}

	private void setInvoiceIdWithoutPrefix(String orderNo, String[] ids) throws NotFoundException {
		ids = splitByCharacterType(ids[0]);
		if (ids.length > 2)
			throw new NotFoundException(getModuleId() + orderNo);
		if (ids.length == 1)
			setInvoiceIdWithNumbersOnly(orderNo, ids[0]);
		else
			setInvoiceId("", ids[0], ids[1]);
	}

	private void setInvoiceIdWithoutSuffix(String orderNo, String code, String number) throws NotFoundException {
		if (!isNumeric(number))
			throw new NotFoundException(getModuleId() + orderNo);
		setInvoiceId(code, number, "");
	}

	private void setInvoiceIdWithPrefix(String orderNo, String[] ids) throws NotFoundException {
		String[] nos = splitByCharacterType(ids[1]);
		if (nos.length > 2)
			throw new NotFoundException(getModuleId() + orderNo);
		if (nos.length == 1)
			setInvoiceIdWithoutSuffix(orderNo, ids[0], nos[0]);
		else
			setInvoiceId(ids[0], nos[0], nos[1]);
	}

	private void setLatestPrice(Item i) {
		unitPrice = null;
		Optional<Price> o = getOptionalPrice(i, customer.getPrimaryPricingType());
		if (!o.isPresent() && customer.getAlternatePricingType() != null)
			o = getOptionalPrice(i, customer.getAlternatePricingType());
		unitPrice = o.get().getPriceValue();
	}

	private void setThreePartId() {
		if (isAnInvoice())
			setThreePartId(prefix, numId, suffix);
	}

	private void setThreePartIdFromOrderNo(String id) throws NotFoundException {
		if (isAnInvoice())
			setInvoiceId(id);
		else
			setDeliveryId(id);
	}

	private void setVatDivisor(BigDecimal vatDivisor) {
		this.vatDivisor = vatDivisor;
	}

	private void setVolumeDiscounts() {
		List<VolumeDiscount> list = item.getVolumeDiscounts();
		LocalDate date = list.stream().filter(vd -> isApprovedAndStartDateIsNotInTheFuture(vd, orderDate()))
				.max(VolumeDiscount::compareTo).get().getStartDate();
		volumeDiscounts = list.stream().filter(vd -> vd.getStartDate().isEqual(date)).collect(toList());
	}

	private BigDecimal sumUnpaid(List<CustomerReceivable> list) {
		return list.stream().map(r -> r.getUnpaidValue()).reduce(ZERO, (a, b) -> a.add(b));
	}

	private Booking toBooking(Billable a) {
		Booking b = new Booking();
		b.setId(a.getBookingId());
		b.setCustomer(a.getCustomerName());
		b.setLocation(a.getCustomerLocation());
		b.setRoute(a.getRoute());
		return b;
	}

	private BigDecimal total() {
		BigDecimal t = get().getTotalValue();
		return t != null ? t : ZERO;
	}

	private BigDecimal totalValue(String endPoint) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		Billable a = billableService.module(getModule()).getOne("/" + endPoint + "?customer=" + customer.getId());
		return a == null ? BigDecimal.ZERO : a.getTotalValue();
	}

	private void updateBasedOnBooking(Billable b) {
		saveThreePartId();
		originalDetails = b.getDetails();
		setBillableData(b);
		setThreePartId();
		if (isABillable())
			get().setBilledBy(username());
		else if (isAReceiving())
			get().setReceivedBy(username());
	}

	private void updateTotals() {
		get().setGrossValue(computeGross());
		get().setDiscounts(listDiscounts());
		get().setTotalValue(computeTotal());
	}

	private Billable validateBooking(long id)
			throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException, RestException,
			InvalidException, NotApprovedPurchaseOrderException, NotFoundException, NotForDeliveryReportException,
			AlreadyReceivedBookingIdException, AlreadyReferencedBookingIdException, NotPickedBookingIdException {
		Billable b = billableService.module(getModule()).getOne("/" + bookingModule() + "?id=" + id);
		confirmBookingExists(id, b);
		confirmDeliveryReportingIsAllowed(b);
		confirmPurchaseOrderIsApprovedBeforeReceipt(id, b);
		confirmBookingIsStillOpen(id, b);
		return b;
	}

	private void validateOrderDateIsNextWorkDay(LocalDate d) throws NotNextWorkDayException {
		if (isTheNextWorkDay(d))
			return;
		reset();
		throw new NotNextWorkDayException();
	}

	private void verifyAllCashBillablesHaveBeenFullyPaid(LocalDate d)
			throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException, RestException,
			InvalidException, NotFullyPaidCashBillableException {
		if (!isASalesOrder() || isUser(MANAGER))
			return;
		Billable b = getNotFullyPaidCOD(d);
		if (b == null)
			return;
		reset();
		throw new NotFullyPaidCashBillableException(b.getOrderNo());
	}

	private void verifyAllCollectionsHaveBeenDeposited(PaymentType t)
			throws UndepositedPaymentException, NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		if (!isASalesOrder() || isUser(MANAGER))
			return;
		Payment p = remittanceService.getUndepositedPayment(t, username(), orderDate());
		if (p == null)
			return;
		reset();
		throw new UndepositedPaymentException(t, p.getId());
	}

	private void verifyAllPickedSalesOrderHaveBeenBilled(LocalDate d)
			throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException, RestException,
			InvalidException, UnbilledPickedSalesOrderException {
		if (isASalesOrder() && !isUser(MANAGER))
			verifyAllPickedSalesOrderHaveBeenBilled(username(), d);
	}

	private void verifyCurrentUserIsTheCustomerAssignedSeller()
			throws NoAssignedCustomerSellerException, NotTheAssignedCustomerSellerException {
		if (isUser(MANAGER))
			return;
		String seller = customer.getSeller(orderDate());
		if (seller == null)
			throw new NoAssignedCustomerSellerException(customerName());
		if (!seller.equals(username()))
			throw new NotTheAssignedCustomerSellerException(seller, customerName());
	}

	private void verifyCustomerHasBadOrderReturnAllowance() throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException, NotAllowedToReturnBadOrderException {
		if (isABadOrder() && hasLessThanAHundredBadOrderAllowance())
			throw new NotAllowedToReturnBadOrderException(customer);
	}

	private void verifyCustomerHasNoOpenBadOrReturnOrder() throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException, OpenBadOrReturnOrderException {
		if (isABadOrder() || isAReturnOrder()) {
			logger.info("Verifying customer has no open bad or return order");
			Billable b = billableService.module(getModule()).getOne("/pendingReturn?customer=" + customer.getId());
			logger.info("Pending bad or return order = " + b);
			if (b != null)
				throw new OpenBadOrReturnOrderException(b.getBookingId());
		}
	}

	private void verifyCustomerHasNoOverdues()
			throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException, RestException,
			InvalidException, NotFoundException, DeactivatedException, BadCreditException {
		if (isUser(MANAGER))
			return;
		BigDecimal o = agedValue();
		if (isPositive(o))
			throw new BadCreditException(customer, o);
	}

	private void verifyCustomersHaveCompleteAndCorrectData() throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException, IncompleteOrErroneousCustomerDataException {
		if (isUser(MANAGER))
			return;
		verifyStreetAddressExists();
		verifyProvincialAddressIsCorrect();
		verifyCityAddressIsCorrect();
		verifyBarangayAddressesIsCorrect();
		verifyVisitScheduleExists();
		verifyCustomersHaveTheSameWeekOneAndFiveVisitSchedules();
		verifyCustomersWithCreditsOrDiscountsHaveContactDetails();
		verifyCustomersHaveSurnamesThatAreDifferentFromTheirNames();
		verifyCustomersHaveDesignations();
		verifyCustomersHaveMobileNos();
	}

	private void verifyStreetAddressExists() throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException, IncompleteOrErroneousCustomerDataException {
		Customer c = customerService.findNoStreetAddress();
		if (c != null)
			throw new IncompleteOrErroneousCustomerDataException(c, "has no street address");
	}

	private void verifyProvincialAddressIsCorrect() throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException, IncompleteOrErroneousCustomerDataException {
		Customer c = customerService.findNotCorrectProvincialAddress();
		if (c != null)
			throw new IncompleteOrErroneousCustomerDataException(c, "has incorrect provincial address");
	}

	private void verifyCityAddressIsCorrect() throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException, IncompleteOrErroneousCustomerDataException {
		Customer c = customerService.findNotCorrectCityAddress();
		if (c != null)
			throw new IncompleteOrErroneousCustomerDataException(c, "has incorrect city address");
	}

	private void verifyBarangayAddressesIsCorrect() throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException, IncompleteOrErroneousCustomerDataException {
		Customer c = customerService.findNotCorrectBarangayAddress();
		if (c != null)
			throw new IncompleteOrErroneousCustomerDataException(c, "has incorrect barangay address");
	}

	private void verifyVisitScheduleExists() throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException, IncompleteOrErroneousCustomerDataException {
		Customer c = customerService.findNoVisitSchedule();
		if (c != null)
			throw new IncompleteOrErroneousCustomerDataException(c, "has no/incorrect visit schedule");
	}

	private void verifyCustomersHaveDesignations() throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException, IncompleteOrErroneousCustomerDataException {
		Customer c = customerService.findNoDesignation();
		if (c != null)
			throw new IncompleteOrErroneousCustomerDataException(c, "contact has no designation");
	}

	private void verifyCustomersHaveMobileNos() throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException, IncompleteOrErroneousCustomerDataException {
		Customer c = customerService.findNoMobileNo();
		if (c != null)
			throw new IncompleteOrErroneousCustomerDataException(c, "contact has no mobile no.");
	}

	private void verifyCustomersHaveSurnamesThatAreDifferentFromTheirNames()
			throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException, RestException,
			InvalidException, IncompleteOrErroneousCustomerDataException {
		Customer c = customerService.findNoSurname();
		if (c != null)
			throw new IncompleteOrErroneousCustomerDataException(c, "contact has no surname or it's the same as its name");
	}

	private void verifyCustomersHaveTheSameWeekOneAndFiveVisitSchedules()
			throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException, RestException,
			InvalidException, IncompleteOrErroneousCustomerDataException {
		Customer c = customerService.findNotTheSameWeeksOneAndFiveVisitSchedule();
		if (c != null)
			throw new IncompleteOrErroneousCustomerDataException(c, "has different week 1 & 5 visit schedules");
	}

	private void verifyCustomersWithCreditsOrDiscountsHaveContactDetails()
			throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException, RestException,
			InvalidException, IncompleteOrErroneousCustomerDataException {
		Customer c = customerService.findNoContactDetails();
		if (c != null)
			throw new IncompleteOrErroneousCustomerDataException(c, "has no contact details");
	}

	private void verifyIdIsPartOfAnIssuedBookletImmediatelyPrecedingItsLast(String prefix, long id, String suffix)
			throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException, RestException,
			InvalidException, UnissuedInvoiceIdException, GapInSerialInvoiceIdException {
		InvoiceBooklet booklet = bookletService.find(prefix, id, suffix);
		if (booklet == null)
			throw new UnissuedInvoiceIdException(prefix + id + suffix);
		long nextIdInBooklet = latestUsedIdInBooklet(booklet) + 1;
		if (id != nextIdInBooklet)
			throw new GapInSerialInvoiceIdException(nextIdInBooklet);
	}

	private void verifyItemReturningCustomerHasCompleteContactDetails()
			throws ItemReturningCustomerIncompleteContactDetailsException {
		if (!(isABadOrder() || isAReturnOrder()))
			return;
		if (customer.getContactName() == null//
				|| customer.getContactSurname() == null//
				|| customer.getContactTitle() == null//
				|| customer.getMobile() == null//
				|| customer.getContactName().equals(customer.getContactSurname()))
			throw new ItemReturningCustomerIncompleteContactDetailsException(customer);
	}

	private void verifyNonPickupSalesOrderDateIsNextWorkingDate()
			throws PickedUpSalesOrderDateNotTodayException, NotNextWorkDayException {
		if (!isASalesOrder() || isUser(MANAGER))
			return;
		LocalDate d = get().getOrderDate();
		if (isAPickUpSales())
			verifyOrderDateIsToday(d);
		else
			validateOrderDateIsNextWorkDay(d);
	}

	private void verifyNotInThePast(LocalDate d) throws DateInThePastException {
		if (d.isBefore(LocalDate.now())) {
			reset();
			throw new DateInThePastException();
		}
	}

	private void verifyOrderDateIsToday(LocalDate d) throws PickedUpSalesOrderDateNotTodayException {
		if (!d.isEqual(LocalDate.now())) {
			reset();
			throw new PickedUpSalesOrderDateNotTodayException();
		}
	}

	private void verifyUserAuthorization() throws UnauthorizedUserException {
		if (isUser(MANAGER))
			return;
		if (!isUser(SELLER) && (isASalesOrder() || isABadOrder() || isAReturnOrder()))
			throw new UnauthorizedUserException("Sellers only");
		if ((!isUser(STORE_KEEPER) && !isUser(LEAD_CHECKER))
				&& (isASalesReturn() || isAPurchaseOrder() || isAPurchaseReceipt()))
			throw new UnauthorizedUserException("Storekeepers and Checkers only");
	}

	public void invalidateAwaitingApproval(String msg) {
		updatePerValidity(false, msg.replace("\n", " "));
	}

	public boolean invalidSalesOrderCanBeOverriden() {
		logger.info("isUser(MANAGER) = " + isUser(MANAGER));
		logger.info("getIsValid = " + getIsValid());
		logger.info("isUnprinted = " + isUnprinted());
		boolean b = isUser(MANAGER) && getIsValid() != null && !getIsValid() && isUnprinted();
		logger.info("invalidSalesOrderCanBeOverriden = " + b);
		return b;
	}

	public boolean isUnprinted() {
		return get().getPrintedOn() == null;
	}

	public boolean isOffSite() {
		return serverService.isOffSite();
	}

	@Override
	public HolidayService getHolidayService() {
		return holidayService;
	}

	@Override
	public String getUploadedBy() {
		return get().getUploadedBy();
	}

	@Override
	public ZonedDateTime getUploadedOn() {
		return get().getUploadedOn();
	}

	@Override
	public void upload() throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException,
			RestException, InvalidException, NotFoundException {
		List<SalesforceAccount> a = customerService.forUpload();
		if (a != null)
			salesInfoUploader.accounts(a);
		List<SalesforceSalesInfo> i = forUpload();
		if (i != null)
			salesInfoUploader.invoices(i);
		saveUploadedData(salesInfoUploader.start());
	}

	@Override
	public List<SalesforceSalesInfo> forUpload() throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		return salesforceService.module("salesforceSalesInfo").getList();
	}

	@Override
	public void saveUploadedData(List<? extends SalesforceEntity> list) throws NoServerConnectionException,
			StoppedServerException, FailedAuthenticationException, RestException, InvalidException, NotFoundException {
		List<SalesforceAccount> a = new ArrayList<>();
		for (SalesforceEntity entity : list)
			if (entity instanceof SalesforceAccount)
				a.add((SalesforceAccount) entity);
			else
				saveBilling(entity);
	}

	private void saveBilling(SalesforceEntity entity) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException, NotFoundException {
		Billable b = find(entity.getIdNo());
		b.setUploadedBy(username());
		b.setUploadedOn(entity.getUploadedOn());
		save(b);
	}
}
