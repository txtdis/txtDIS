package ph.txtdis.service;

public abstract class AbstractCreditAndInvoiceAndPurchaseAndReturnAndVatTrackedBillableService {
	/*
	   extends AbstractPricedBillableService implements BadOrderedBillableService, ItemReturnableBillableService,
			LatestCredit, PurchasedBillableService, VatableBillableService {
	
		private static Logger logger = getLogger(
				AbstractCreditAndInvoiceAndPurchaseAndReturnAndVatTrackedBillableService.class);
	
		@Autowired
		private CustomerReceivableService customerReceivableService;
	
		@Autowired
		private InvoiceBookletService bookletService;
	
		@Autowired
		private InventoryService inventoryService;
	
		@Autowired
		private VatService vatService;
	
		private int linesPerPage;
	
		private BigDecimal remainingCredit;
	
		private CreditDetail credit;
	
		private Integer onPurchaseDaysLevel, onReceiptDaysLevel;
	
		private Inventory inventory;
	
		private String prefix, idNo, suffix;
	
		@Override
		public void clearItemReturnPaymentDataSetByItsInputDialogDuringDataEntry() {
			setThreePartId(null, null, null);
		}
	
		@Override
		public BillableDetail createDetail() {
			BillableDetail sd = super.createDetail();
			sd.setOnPurchaseDaysLevel(onPurchaseDaysLevel);
			sd.setOnReceiptDaysLevel(onReceiptDaysLevel);
			return sd;
		}
	
		@Override
		@SuppressWarnings("unchecked")
		public CreditDetail getCredit() {
			if (credit == null)
				credit = getCredit(customer.getCreditDetails(), getOrderDate());
			return credit;
		}
	
		@Override
		public Long getCustomerId() {
			if (isAPurchaseOrder())
				try {
					setCustomer(customerService.getVendor());
					setCustomerInfo();
				} catch (Exception e) {
					get().setCustomerId(null);
				}
			return super.getCustomerId();
		}
	
		@Override
		public Integer getOnPurchaseDaysLevel() throws Exception {
			inventory = inventoryService.getInventory(item.getId());
			return onPurchaseDaysLevel = inventory.getDaysLevel();
		}
	
		@Override
		public Integer getOnReceiptDaysLevel(UomType uom, BigDecimal qty) {
			this.uom = uom;
			this.qty = qty;
			return getOnReceiptDaysLevel();
		}
	
		private Integer getOnReceiptDaysLevel() {
			BigDecimal avg = inventory.getAvgDailySoldQty();
			return onReceiptDaysLevel = isZero(avg) ? 9999 : divide(getStockOnHand(), avg).intValue();
		}
	
		private BigDecimal getStockOnHand() {
			return onHandQty().add(getUnitQty());
		}
	
		private BigDecimal getUnitQty() {
			return qty.multiply(itemService.getQtyPerUom(item, uom));
		}
	
		@Override
		public BigDecimal getVat() {
			try {
				return getTotalValue().subtract(getVatable());
			} catch (Exception e) {
				return BigDecimal.ZERO;
			}
		}
	
		@Override
		public BigDecimal getVatable() {
			return vatService.getVatable(getTotalValue());
		}
	
		@Override
		public boolean isAppendable() {
			return super.isAppendable() || getDetails().size() < linesPerPage();
		}
	
		@Override
		protected void nullifyAll() {
			super.nullifyAll();
			prefix = null;
			idNo = null;
			suffix = null;
			inventory = null;
			linesPerPage = 0;
			onPurchaseDaysLevel = null;
			onReceiptDaysLevel = null;
		}
	
		@Override
		public void saveDisposalData() throws NotAllowedOffSiteTransactionException {
			if (isNewAndOffSite())
				throw new NotAllowedOffSiteTransactionException();
			setReceivingModifiedByUser();
		}
	
		@Override
		public void saveItemReturnReceiptData() throws Exception {
			if (isNewAndOffSite())
				throw new NotAllowedOffSiteTransactionException();
			setReceivedByUser();
		}
	
		@Override
		public void setCustomerRelatedData() {
			super.setCustomerRelatedData();
			get().setDueDate(dueDate());
		}
	
		@Override
		public void setItemReturnPaymentData(LocalDate d) {
			setOrderDate(d);
			setBilledByUser();
			setUnpaid(ZERO);
			get().setFullyPaid(true);
			scriptService.set(ScriptType.RETURN_PAYMENT, createItemReturnPaymentScript(d));
		}
	
		private String createItemReturnPaymentScript(LocalDate d) {
			return getId() + "|" + d + "|" + getPrefix() + "|" + getNumId() + "|" + getSuffix() + "|" + username() + "|"
					+ ZonedDateTime.now();
		}
	
		@Override
		protected void validateOrderDate(LocalDate d) throws DateInThePastException, UnauthorizedUserException, Exception {
			super.validateOrderDate(d);
			verifyCustomersHaveCompleteAndCorrectData();
		}
	
		@Override
		protected void validateCustomer(Long id) throws Exception {
			super.validateCustomer(id);
			verifyCurrentUserIsTheCustomerAssignedSeller();
			// TODO verifyAllCollectionsHaveBeenDeposited(PaymentType.CHECK);
			verifyItemReturningCustomerHasCompleteContactDetails();
			verifyCustomerHasNoOpenBadOrReturnOrder();
			// TODO verifyCustomerHasBadOrderReturnAllowance();
			verifyCustomerHasNoOverdues();
			verifyCustomerHasNotExceededItsCreditLimit(ZERO);
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
	
		private void verifyCustomerHasNoOpenBadOrReturnOrder() throws Exception {
			if (isABadOrder() || isAReturnOrder()) {
				logger.info("\n    Verifying customer has no open bad or return order");
				Billable b = findBillable("/pendingReturn?customer=" + customer.getId());
				logger.info("\n    Pending bad or return order = " + b);
				if (b != null)
					throw new OpenBadOrReturnOrderException(b.getBookingId());
			}
		}
	
		protected abstract void verifyCustomerHasBadOrderReturnAllowance() throws Exception;
	
		private void verifyCustomerHasNoOverdues() throws Exception {
			if (credentialService.isUser(MANAGER))
				return;
			BigDecimal o = getAgedValue();
			if (isPositive(o))
				throw new BadCreditException(customer, o);
		}
	
		private void verifyCustomerHasNotExceededItsCreditLimit(BigDecimal additionalCredit) throws Exception {
			if (credentialService.isUser(MANAGER))
				return;
			computeRemainingCredit(additionalCredit);
			if (isNegative(remainingCredit))
				throw new ExceededCreditLimitException(customer, getCreditLimit(), remainingCredit());
		}
	
		private void computeRemainingCredit(BigDecimal additionalCredit) throws Exception {
			if (remainingCredit == null)
				remainingCredit = getCreditLimit().subtract(getAgingValue());
			remainingCredit = remainingCredit.subtract(additionalCredit);
		}
	
		private BigDecimal getAgingValue() throws Exception {
			String id = customer.getId().toString();
			List<CustomerReceivable> list = customerReceivableService.listReceivables(id, AGING_COLUMN, null);
			return sumUnpaid(list);
		}
	
		@Override
		public void updateUponOrderNoValidation(String prefix, Long id, String suffix) throws Exception {
			if (isNewAndOffSite())
				throw new NotAllowedOffSiteTransactionException();
			checkforDuplicates(prefix, id, suffix);
			// verifyIdIsPartOfAnIssuedBookletImmediatelyPrecedingItsLast(prefix, id, suffix);
			setThreePartId(prefix, id, suffix);
		}
	
		private void checkforDuplicates(String prefix, Long id, String suffix) throws Exception {
			Billable i = getBillable(prefix, id, suffix);
			if (i != null)
				throw new DuplicateException(getModuleId() + id);
		}
	
		private void confirmDeliveryReportingIsAllowed(Billable b)
				throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException, RestException,
				InvalidException, NotFoundException, NotForDeliveryReportException {
			if (!isADeliveryReport())
				return;
			if (!isForDR(b))
				throw new NotForDeliveryReportException(b.getCustomerName());
		}
	
		@Override
		public Item verifyItem(Long id) throws Exception {
			Item i = super.verifyItem(id);
			confirmItemIsNotOnList(i);
			confirmItemIsNotOnOtherBookingWithTheSameCustomerAndDate(i);
			confirmItemTobeReturnedHasBeenPurchasedBefore(i);
			return i;
		}
	
		protected void confirmItemIsNotOnOtherBookingWithTheSameCustomerAndDate(Item i) throws NoServerConnectionException,
				StoppedServerException, FailedAuthenticationException, RestException, InvalidException, DuplicateException {
			if (credentialService.isUser(MANAGER) //
					|| isAReturnOrder() //
					|| isABadOrder() //
					|| isASalesOrder() && (!isNew() || customer.getType() == VENDOR))
				return;
			Billable b = findBillable("/item?id=" + i.getId() + "&customer=" + customer.getId() + "&date=" + getOrderDate());
			if (b != null)
				throw new DuplicateException(
						i.getName() + " booked for\n" + b.getCustomerName() + " on " + toDateDisplay(b.getOrderDate()));
		}
	
		protected void confirmItemTobeReturnedHasBeenPurchasedBefore(Item i)
				throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException, RestException,
				InvalidException, ToBeReturnedItemNotPurchasedWithinTheLastSixMonthException {
			Billable b = findBillable("/purchased?by=" + customer.getId() + "&item=" + i.getId());
			if (b == null)
				throw new ToBeReturnedItemNotPurchasedWithinTheLastSixMonthException(i, customer);
		}
	
		private LocalDate dueDate() {
			return getOrderDate().plusDays(getCreditTerm());
		}
	
		@Override
		public Billable findById(String id) throws Exception {
			Billable b = isAnInvoice() ? findByThreePartId(id) : findById(toId(id));
			if (b == null)
				throw new NotFoundException(getModuleId() + " No. " + id);
			super.findById(id);
			return b;
		}
	
		private Long toId(String id) throws NotFoundException {
			try {
				return Long.valueOf(id);
			} catch (NumberFormatException e) {
				throw new NotFoundException(getModuleId() + " No. " + id);
			}
		}
	
		private Billable findByThreePartId(String id) throws NoServerConnectionException, StoppedServerException,
				FailedAuthenticationException, RestException, InvalidException, NotFoundException {
			setThreePartIdFromOrderNo(id);
			Billable b = findBillable("/find?prefix=" + prefix + "&id=" + idNo + "&suffix=" + suffix);
			return b;
		}
	
		@Override
		protected BigDecimal getGross() {
			BigDecimal g = get().getGrossValue();
			return g == null ? ZERO : g;
		}
	
		private List<CustomerDiscount> getCustomerDiscounts() {
			return customer.getCustomerDiscounts();
		}
	
		protected Stream<CustomerDiscount> getLatestApprovedCustomerDiscountStream() {
			try {
				return getCustomerDiscounts().stream().filter(p -> isApprovedAndStartDateIsNotInTheFuture(p, getOrderDate()));
			} catch (Exception e) {
				return Stream.empty();
			}
		}
	
		private boolean isForDR(Billable b) throws NoServerConnectionException, StoppedServerException,
				FailedAuthenticationException, RestException, InvalidException, NotFoundException {
			return customerService.getBillingType(b) == DELIVERY ? true : areAllItemsReturned(b);
		}
	
		private boolean areAllItemsReturned(Billable b) {
			if (b.getReceivedOn() == null)
				return false;
			return !b.getDetails().stream().anyMatch(d -> notAllReturned(d));
		}
	
		private Long latestUsedIdInBooklet(InvoiceBooklet b) throws NoServerConnectionException, StoppedServerException,
				FailedAuthenticationException, RestException, InvalidException {
			Billable i = findBillable("/latest?prefix=" + b.getPrefix()//
					+ "&suffix=" + b.getSuffix() //
					+ "&start=" + b.getStartId() //
					+ "&end=" + b.getEndId());
			return i == null ? b.getStartId() - 1 : i.getNumId();
		}
	
		private int linesPerPage() {
			if (linesPerPage == 0)
				linesPerPage = getLinesPerPage();
			return linesPerPage;
		}
	
		protected int getLinesPerPage() {
			return bookletService.getLinesPerPage();
		}
	
		private boolean notAllReturned(BillableDetail d) {
			try {
				return d.getInitialQty().compareTo(d.getReturnedQty()) > 0;
			} catch (Exception e) {
				return true;
			}
		}
	
		private BigDecimal onHandQty() {
			BigDecimal soh = inventory.getGoodQty();
			return soh == null ? ZERO : soh;
		}
	
		private BigDecimal remainingCredit() {
			return remainingCredit == null ? ZERO : remainingCredit.abs();
		}
	
		protected void saveThreePartId() {
			prefix = getPrefix();
			numId = getNumId();
			suffix = getSuffix();
		}
	
		@Override
		public void setCustomer(Customer c) {
			super.setCustomer(c);
			credit = null;
			remainingCredit = null;
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
	
		protected void setThreePartId() {
			if (isAnInvoice())
				setThreePartId(prefix, numId, suffix);
		}
	
		private void setThreePartId(String prefix, Long id, String suffix) {
			get().setPrefix(nullIfEmpty(prefix));
			get().setNumId(id);
			get().setSuffix(nullIfEmpty(suffix));
		}
	
		private void setThreePartIdFromOrderNo(String id) throws NotFoundException {
			if (isAnInvoice())
				setInvoiceId(id);
			else
				setDeliveryId(id);
		}
	
		private BigDecimal sumUnpaid(List<CustomerReceivable> list) {
			return list.stream().map(r -> r.getUnpaidValue()).reduce(ZERO, BigDecimal::add);
		}
	
		@Override
		protected BigDecimal getTotalPercentageBasedDiscountValue() {
			return BigDecimal.ZERO;
		}
	
		@Override
		protected Billable validateBooking(Long id) throws Exception {
			Billable b = super.validateBooking(id);
			confirmPurchaseOrderIsApprovedBeforeReceipt(b, id);
			confirmDeliveryReportingIsAllowed(b);
			return b;
		}
	
		private void confirmPurchaseOrderIsApprovedBeforeReceipt(Billable b, long id)
				throws NotApprovedPurchaseOrderException {
			if (isAPurchaseReceipt() && (b.getIsValid() == null || b.getIsValid() == false))
				throw new NotApprovedPurchaseOrderException(id);
		}
	
		private void verifyCurrentUserIsTheCustomerAssignedSeller() throws Exception {
			if (credentialService.isUser(MANAGER))
				return;
			if (credentialService.isUser(UserType.SALES_ENCODER) && isBulkOrPickUpRoute(customer.getRoute()))
				return;
			String seller = customer.getSeller(getOrderDate());
			if (seller == null)
				throw new NoAssignedCustomerSellerException(customer.getName());
			if (!seller.equals(username()))
				throw new NotTheAssignedCustomerSellerException(seller, customer.getName());
		}
	
		private boolean isBulkOrPickUpRoute(Route route) {
			return route == null || route.getName().startsWith("BULK") || route.getName().startsWith("PICK-UP");
		}
	
		private BigDecimal getAgedValue() throws Exception {
			return sumUnpaid(customerReceivableService.listReceivables(//
					customer.getId().toString(), //
					AGED_COLUMN, //
					null));
		}
	
		private void verifyCustomersHaveCompleteAndCorrectData() throws Exception {
			if (credentialService.isUser(MANAGER))
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
	
		private void verifyStreetAddressExists() throws Exception {
			Customer c = customerService.findNoStreetAddress();
			if (c != null)
				throw new IncompleteOrErroneousCustomerDataException(c, "has no street address");
		}
	
		private void verifyProvincialAddressIsCorrect() throws Exception {
			Customer c = customerService.findNotCorrectProvincialAddress();
			if (c != null)
				throw new IncompleteOrErroneousCustomerDataException(c, "has incorrect provincial address");
		}
	
		private void verifyCityAddressIsCorrect() throws Exception {
			Customer c = customerService.findNotCorrectCityAddress();
			if (c != null)
				throw new IncompleteOrErroneousCustomerDataException(c, "has incorrect city address");
		}
	
		private void verifyBarangayAddressesIsCorrect() throws Exception {
			Customer c = customerService.findNotCorrectBarangayAddress();
			if (c != null)
				throw new IncompleteOrErroneousCustomerDataException(c, "has incorrect barangay address");
		}
	
		private void verifyVisitScheduleExists() throws Exception {
			Customer c = customerService.findNoVisitSchedule();
			if (c != null)
				throw new IncompleteOrErroneousCustomerDataException(c, "has no/incorrect visit schedule");
		}
	
		private void verifyCustomersHaveDesignations() throws Exception {
			Customer c = customerService.findNoDesignation();
			if (c != null)
				throw new IncompleteOrErroneousCustomerDataException(c, "contact has no designation");
		}
	
		private void verifyCustomersHaveMobileNos() throws Exception {
			Customer c = customerService.findNoMobileNo();
			if (c != null)
				throw new IncompleteOrErroneousCustomerDataException(c, "contact has no mobile no.");
		}
	
		private void verifyCustomersHaveSurnamesThatAreDifferentFromTheirNames() throws Exception {
			Customer c = customerService.findNoSurname();
			if (c != null)
				throw new IncompleteOrErroneousCustomerDataException(c, "contact has no surname or it's the same as its name");
		}
	
		private void verifyCustomersHaveTheSameWeekOneAndFiveVisitSchedules() throws Exception {
			Customer c = customerService.findNotTheSameWeeksOneAndFiveVisitSchedule();
			if (c != null)
				throw new IncompleteOrErroneousCustomerDataException(c, "has different week 1 & 5 visit schedules");
		}
	
		private void verifyCustomersWithCreditsOrDiscountsHaveContactDetails() throws Exception {
			Customer c = customerService.findNoContactDetails();
			if (c != null)
				throw new IncompleteOrErroneousCustomerDataException(c, "has no contact details");
		}
	
		protected void verifyIdIsPartOfAnIssuedBookletImmediatelyPrecedingItsLast(String prefix, long id, String suffix)
				throws Exception {
			InvoiceBooklet booklet = bookletService.find(prefix, id, suffix);
			if (booklet == null)
				throw new UnissuedInvoiceIdException(prefix + id + suffix);
			long nextIdInBooklet = latestUsedIdInBooklet(booklet) + 1;
			if (id != nextIdInBooklet)
				throw new GapInSerialInvoiceIdException(nextIdInBooklet);
		}
		*/
}
