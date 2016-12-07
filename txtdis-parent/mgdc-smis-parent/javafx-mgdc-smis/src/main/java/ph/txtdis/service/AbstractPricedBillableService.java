package ph.txtdis.service;

public abstract class AbstractPricedBillableService {
	/*
		private static Logger logger = getLogger(AbstractPricedBillableService.class);
	
		@Autowired
		private HolidayService holidayService;
	
		@Autowired
		private RemittanceService remittanceService;
	
		@Autowired
		private SavingService<Billable> savingService;
	
		@Autowired
		private RestServerService serverService;
	
		@Autowired
		private SpunService<Billable, Long> spunService;
	
		@Autowired
		private ClientTypeMap typeMap;
	
		@Value("${go.live}")
		private String goLive;
	
		@Autowired
		protected ChannelService channelService;
	
		@Autowired
		protected CredentialService credentialService;
	
		@Autowired
		protected CustomerService customerService;
	
		@Autowired
		protected ItemService itemService;
	
		@Autowired
		protected ReadOnlyService<Billable> billableReadOnlyService;
	
		@Autowired
		protected ScriptService scriptService;
	
		@Autowired
		protected SyncService syncService;
	
		@Value("${prefix.module}")
		protected String modulePrefix;
	
		private BillableDetail receivingDetail;
	
		protected List<BillableDetail> originalDetails;
	
		protected BigDecimal primaryUomPrice, qty;
	
		protected Billable billable;
	
		protected Customer customer;
	
		protected Item item;
	
		protected Long numId;
	
		protected BillableType type;
	
		protected UomType uom;
	
		public AbstractPricedBillableService() {
			reset();
		}
	
		public boolean canApprove() {
			return !isNew() && isAuthorizedToApprove() && !isASalesOrder();
		}
	
		private boolean isAuthorizedToApprove() {
			if (credentialService.isUser(MANAGER))
				return true;
			return isBilling() && credentialService.isUser(AUDITOR);
		}
	
		protected boolean isBilling() {
			return isAnInvoice() || isADeliveryReport();
		}
	
		public boolean canInvalidSalesOrderBeOverriden() {
			logger.info("\n    isUser(MANAGER) = " + credentialService.isUser(MANAGER));
			logger.info("\n    getIsValid = " + getIsValid());
			logger.info("\n    isUnprinted = " + isUnpicked());
			boolean b = credentialService.isUser(MANAGER) && getIsValid() != null && getIsValid() == false && isUnpicked();
			logger.info("\n    invalidSalesOrderCanBeOverriden = " + b);
			return b;
		}
	
		private boolean isUnpicked() {
			return get().getPickListId() == null;
		}
	
		public boolean canReject() {
			if (isAnInvoice())
				return (credentialService.isUser(MANAGER) || credentialService.isUser(SALES_ENCODER)) && isUnpaid();
			if (isASalesOrder())
				return isUnpicked();
			return true;
		}
	
		private boolean isUnpaid() {
			return get().getPayments() == null || get().getPayments().isEmpty();
		}
	
		public boolean closeAppIfInvalid() {
			return isAnInvoice() && getIsValid() != null && getIsValid() == false;
		}
	
		protected BigDecimal computeSubtotal(BillableDetail d) {
			return d.getQty().multiply(d.getPriceValue());
		}
	
		public BillableDetail createDetail() {
			BillableDetail sd = new BillableDetail();
			sd.setId(item.getId());
			sd.setItemName(item.getName());
			sd.setUom(uom);
			sd = setQty(sd);
			sd.setQuality(quality());
			sd.setQtyPerCase(getQtyPerCase(sd));
			sd.setPriceValue(getPrice(sd));
			logger.info("\n    QtyPerCase = " + sd.getQtyPerCase());
			return sd;
		}
	
		protected BillableDetail setQty(BillableDetail sd) {
			sd.setInitialQty(qty);
			return sd;
		}
	
		private QualityType quality() {
			return isABadOrder() ? BAD : GOOD;
		}
	
		private int getQtyPerCase(BillableDetail sd) {
			UomType uom = sd.getUom();
			return uom != UomType.CS ? 0 : itemService.getQtyPerUom(item, uom).intValue();
		}
	
		protected abstract BigDecimal getPrice(BillableDetail sd);
	
		protected Billable findById(Long id) throws NoServerConnectionException, StoppedServerException,
				FailedAuthenticationException, RestException, InvalidException {
			return findBillable("/" + getSpunModule() + "?id=" + id);
		}
	
		protected Billable findBillable(String endPt) throws NoServerConnectionException, StoppedServerException,
				FailedAuthenticationException, RestException, InvalidException {
			return billableReadOnlyService.module(getModule()).getOne(endPt);
		}
	
		@SuppressWarnings("unchecked")
		public Billable find(String id) throws NoServerConnectionException, StoppedServerException,
				FailedAuthenticationException, RestException, InvalidException, NotFoundException {
			String endpt = "/" + (isADeliveryReport() || isAnInvoice() ? id : getSpunModule() + "?id=" + id);
			Billable e = findBillable(endpt);
			if (e == null)
				throw new NotFoundException(getModuleId() + id);
			return e;
		}
	
		@SuppressWarnings("unchecked")
		public Billable get() {
			if (billable == null)
				reset();
			return billable;
		}
	
		public String getAlternateName() {
			if (isABadOrder())
				return "B/O";
			if (isADeliveryReport())
				return "D/R";
			if (isAnInvoice())
				return "S/I";
			if (isAPurchaseOrder())
				return "P/O";
			if (isAPurchaseReceipt() || isASalesReturn())
				return "R/R";
			if (isAReturnOrder())
				return "RMA";
			if (isASalesOrder())
				return getReferenceName();
			return "";
		}
	
		public BigDecimal getBadOrderAllowanceValue() {
			return get().getBadOrderAllowanceValue();
		}
	
		public BigDecimal getBalance() {
			try {
				BigDecimal d = get().getUnpaidValue().abs().subtract(ONE);
				return d.compareTo(ONE) <= 0 ? null : d;
			} catch (Exception e) {
				return null;
			}
		}
	
		public Billable getBillable(String prefix, Long id, String suffix) throws Exception {
			return findBillable("/find?prefix=" + prefix + "&id=" + id + "&suffix=" + suffix);
		}
	
		public ReadOnlyService<Billable> getBillableReadOnlyService() {
			return billableReadOnlyService;
		}
	
		public String getBilledBy() {
			return get().getBilledBy();
		}
	
		public ZonedDateTime getBilledOn() {
			return get().getBilledOn();
		}
	
		public Long getBookingId() {
			return get().getBookingId();
		}
	
		protected String getBookingModule() {
			if (isABadOrder())
				return "badOrder";
			if (isAPurchaseOrder() || isAPurchaseReceipt())
				return "purchaseOrder";
			if (isAReturnOrder())
				return "returnOrder";
			else
				return "booking";
		}
	
		public String getCreatedBy() {
			return isAReceiving() ? get().getReceivedBy() : get().getCreatedBy();
		}
	
		public ZonedDateTime getCreatedOn() {
			return isAReceiving() ? getReceivedOn() : get().getCreatedOn();
		}
	
		private Customer getCustomer() {
			try {
				return customerService.find(billable.getCustomerId());
			} catch (Exception e) {
				return null;
			}
		}
	
		public String getCustomerAddress() {
			return get().getCustomerAddress();
		}
	
		public Long getCustomerId() {
			return get().getCustomerId();
		}
	
		public String getCustomerName() {
			return get().getCustomerName();
		}
	
		public String getDateLabelName() {
			return "Date";
		}
	
		public String getDecidedBy() {
			return get().getDecidedBy();
		}
	
		public ZonedDateTime getDecidedOn() {
			return get().getDecidedOn();
		}
	
		public String getDecisionTag(Boolean isValid) {
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
			return "[" + s + ": " + credentialService.username() + " - " + toDateDisplay(now()) + "] ";
		}
	
		private boolean isABadOrReturnOrder() {
			return isABadOrder() || isAReturnOrder();
		}
	
		public List<BillableDetail> getDetails() {
			List<BillableDetail> l = get().getDetails();
			if (l == null)
				return emptyList();
			return !isASalesReturn() ? l : l.stream().filter(d -> !isZero(d.getReturnedQty())).collect(toList());
		}
	
		public List<String> getDiscounts() {
			return get().getDiscounts();
		}
	
		public LocalDate getDueDate() {
			return get().getDueDate();
		}
	
		public String getFontIcon() {
			return getTypeMap().icon(getSpunModule());
		}
	
		public String getHeaderText() {
			if (isABadOrder())
				return "Bad Order";
			if (isADeliveryReport())
				return "Delivery Report";
			if (isAnInvoice())
				return "Invoice";
			if (isAPurchaseOrder())
				return "Purchase Order";
			if (isAPurchaseReceipt())
				return "P/O Receipt";
			if (isAReturnOrder())
				return "Return Order";
			if (isASalesOrder())
				return "Sales Order";
			if (isASalesReturn())
				return "Receiving Report";
			else
				return "";
		}
	
		public HolidayService getHolidayService() {
			return holidayService;
		}
	
		public Long getId() {
			return get().getId();
		}
	
		public Long getIdNo() {
			return isAPurchaseReceipt() ? getReceivingId() : getNumId();
		}
	
		protected Long getNumId() {
			return get().getNumId();
		}
	
		public String getIdPrompt() {
			final String No = " No.";
			if (isASalesOrder() || isASalesReturn())
				return "S/I(D/R)" + No;
			if (isABadOrder() || isAReturnOrder())
				return "S/I" + No;
			return getAlternateName() + No;
		}
	
		public LocalDate getInvoiceDate() {
			return getDueDate();
		}
	
		public Boolean getIsValid() {
			return get().getIsValid();
		}
	
		public Item getItem() {
			return item;
		}
	
		public String getItemName() {
			return item == null ? null : item.getDescription();
		}
	
		public ItemService getItemService() {
			return itemService;
		}
	
		public String getModule() {
			return "billable";
		}
	
		public String getModuleIdNo() {
			if (isASalesOrder())
				return "" + getBookingId();
			if (isAPurchaseReceipt() || isADeliveryReport())
				return "" + Math.abs(getNumId());
			if (isAnInvoice())
				return getOrderNo();
			return PricedBillableService.super.getModuleIdNo();
		}
	
		public String getOpenDialogHeader() {
			return "Open a(n) " + getHeaderText();
		}
	
		public LocalDate getOrderDate() {
			LocalDate d = get().getOrderDate();
			setOrderDate(d);
			return d;
		}
	
		public String getOrderNo() {
			if (isABadOrder() || isAPurchaseOrder() || isAReturnOrder() || isASalesOrder())
				return formatId(getBookingId());
			if (isADeliveryReport())
				return get().getOrderNo().replace("-", "");
			if (isAnInvoice())
				return get().getOrderNo();
			if (isAPurchaseReceipt() || isASalesReturn())
				return formatId(getReceivingId());
			else
				return formatId(getId());
		}
	
		public List<BillableDetail> getOriginalDetails() {
			return originalDetails;
		}
	
		public List<String> getPayments() {
			return get().getPayments();
		}
	
		public String getPrefix() {
			return get().getPrefix();
		}
	
		public String getPrintedBy() {
			return get().getPrintedBy();
		}
	
		public ZonedDateTime getPrintedOn() {
			return get().getPrintedOn();
		}
	
		@SuppressWarnings("unchecked")
		public ReadOnlyService<Billable> getReadOnlyService() {
			return billableReadOnlyService;
		}
	
		public String getReceivedBy() {
			return get().getReceivedBy();
		}
	
		public ZonedDateTime getReceivedOn() {
			return get().getReceivedOn();
		}
	
		@SuppressWarnings("unchecked")
		public BillableDetail getReceivingDetail() {
			return receivingDetail;
		}
	
		public Long getReceivingId() {
			return get().getReceivingId();
		}
	
		public String getReceivingLabelName() {
			return isAReceiving() ? "Receiving Report No." : "R/R No.";
		}
	
		public String getReceivingModifiedBy() {
			return get().getReceivingModifiedBy();
		}
	
		public ZonedDateTime getReceivingModifiedOn() {
			return get().getReceivingModifiedOn();
		}
	
		public String getReferenceName() {
			if (isABadOrder())
				return "B/O";
			if (isAPurchaseOrder() || isAPurchaseReceipt())
				return "P/O";
			if (isAReturnOrder())
				return "R/O";
			if (isAReceiving())
				return "Reference";
			return "S/O";
		}
	
		public String getRemarks() {
			return get().getRemarks();
		}
	
		@SuppressWarnings("unchecked")
		public SavingService<Billable> getSavingService() {
			return savingService;
		}
	
		public ScriptService getScriptService() {
			return scriptService;
		}
	
		public <T extends EntityDecisionNeeded<Long>> ScriptType getScriptType(T d) {
			return BILLING_APPROVAL;
		}
	
		public Long getSpunId() {
			if (isNew())
				return null;
			if (isABooking())
				return getBookingId();
			if (isADeliveryReport())
				return getNumId();
			if (isAReceiving())
				return getReceivingId();
			return getId();
		}
	
		public String getSpunModule() {
			if (isABadOrder())
				return "badOrder";
			if (isADeliveryReport())
				return "deliveryReport";
			if (isAnInvoice())
				return getModule();
			if (isAPurchaseOrder())
				return "purchaseOrder";
			if (isAPurchaseReceipt())
				return "purchaseReceipt";
			if (isAReturnOrder())
				return "returnOrder";
			if (isASalesOrder())
				return "salesOrder";
			if (isASalesReturn())
				return "salesReturn";
			return "";
	
		}
	
		public SpunService<Billable, Long> getSpunService() {
			return spunService;
		}
	
		public String getSuffix() {
			return get().getSuffix();
		}
	
		public SyncService getSyncService() {
			return syncService;
		}
	
		public String getTitleText() {
			return credentialService.username() + "@" + modulePrefix + " " + PricedBillableService.super.getTitleText();
		}
	
		public BigDecimal getTotalValue() {
			return get().getTotalValue();
		}
	
		public ClientTypeMap getTypeMap() {
			return typeMap;
		}
	
		public UomType getUomOfSelectedItem(String itemName) {
			try {
				setReceivingDetailAndItsItem(itemName);
				return receivingDetail.getUom();
			} catch (Exception e) {
				return null;
			}
		}
	
		public void invalidate() {
			get().setIsValid(false);
			get().setDecidedBy(credentialService.username());
			get().setDecidedOn(ZonedDateTime.now());
		}
	
		public void invalidateAwaitingApproval(String msg) {
			updatePerValidity(false, msg.replace("\n", " "));
		}
	
		public boolean isABadOrder() {
			return type == BAD_ORDER;
		}
	
		public boolean isABilling() {
			return isAnInvoice() || isADeliveryReport();
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
	
		public boolean isAppendable() {
			return isAPurchaseOrder() || isAPurchaseReceipt() || getDetails() == null;
		}
	
		public boolean isAPurchaseOrder() {
			return type == PURCHASE_ORDER;
		}
	
		public boolean isAPurchaseReceipt() {
			return type == PURCHASE_RECEIPT;
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
	
		public boolean isNew() {
			if (isBilling())
				return isNotBilled();
			return isAReceiving() ? getReceivedOn() == null : getCreatedOn() == null;
		}
	
		private boolean isNotBilled() {
			return get().getBilledOn() == null;
		}
	
		public boolean isOffSite() {
			return serverService.isOffSite();
		}
	
		public boolean isSalesOrderModifiable() {
			return isASalesOrder() && (isNew() || !isUnpicked());
		}
	
		public boolean isSalesOrderReturnable() {
			return isUnbilledSalesReturn() && isNew() && canReceiveSalesOrder();
		}
	
		private boolean isUnbilledSalesReturn() {
			return isASalesReturn() && isNotBilled();
		}
	
		private boolean canReceiveSalesOrder() {
			return canModifySalesReturn() || credentialService.isUser(STORE_KEEPER);
		}
	
		public boolean isSalesReturnModifiable() {
			return isUnbilledSalesReturn() && !isNew() && canModifySalesReturn();
		}
	
		private boolean canModifySalesReturn() {
			return credentialService.isUser(MANAGER) || credentialService.isUser(STOCK_CHECKER);
		}
	
		protected List<Billable> getList(String endPt) {
			try {
				return billableReadOnlyService.module(getModule()).getList(endPt);
			} catch (Exception e) {
				return emptyList();
			}
		}
	
		public List<Booking> listUnpicked(LocalDate d) {
			return getList("/unpicked?date=" + d).stream().map(a -> toBooking(a)).collect(toList());
		}
	
		protected Booking toBooking(Billable a) {
			Booking b = new Booking();
			b.setId(a.getBookingId());
			b.setCustomer(a.getCustomerName());
			b.setLocation(a.getCustomerLocation());
			b.setRoute(a.getRoute());
			return b;
		}
	
		public void overrideInvalidation() throws SuccessfulSaveInfo, NoServerConnectionException, StoppedServerException,
				FailedAuthenticationException, InvalidException {
			set(setDecisionStatus(get(), null, ""));
			save();
		}
	
		public void reset() {
			nullifyAll();
			set(new Billable());
		}
	
		protected void nullifyAll() {
			setCustomer(null);
			setItem(null);
			numId = null;
			setReceivingDetail(null);
			primaryUomPrice = null;
		}
	
		public void setCustomer(Customer c) {
			customer = c;
		}
	
		public void save() throws SuccessfulSaveInfo, NoServerConnectionException, StoppedServerException,
				FailedAuthenticationException, InvalidException {
			if (isAReceiving())
				setReceivedByUser();
			else if (isAReturnOrder())
				get().setIsRma(true);
			else if (isABadOrder())
				get().setIsRma(false);
			else if ((isADeliveryReport() || isAnInvoice()) && getIsValid() == null)
				setBilledByUser();
			set(save(get()));
			scriptService.saveScripts();
			throw new SuccessfulSaveInfo(saveInfo());
		}
	
		protected void setReceivedByUser() {
			get().setReceivedBy(credentialService.username());
			if (isSalesReturnModifiable())
				setReceivingModifiedByUser();
		}
	
		protected void setReceivingModifiedByUser() {
			get().setReceivingModifiedBy(credentialService.username());
		}
	
		protected void setBilledByUser() {
			get().setBilledBy(credentialService.username());
		}
	
		private String saveInfo() {
			if (getOrderNo().equals("0"))
				return getAlternateName() + " invalidation";
			return getModuleId() + getOrderNo();
		}
	
		public <T extends Keyed<Long>> void set(T t) {
			if (t == null)
				return;
			billable = (Billable) t;
			customer = getCustomer();
			if (isSalesReturnModifiable())
				originalDetails = billable.getDetails();
		}
	
		public void setCreatedBy(String username) {
			get().setCreatedBy(username);
		}
	
		public void setCreatedOn(ZonedDateTime timestamp) {
			get().setCreatedOn(timestamp);
		}
	
		public void setCustomerRelatedData() {
			setCustomerInfo();
			setDetails(null);
		}
	
		protected void setCustomerInfo() {
			get().setCustomerId(customer.getId());
			get().setCustomerName(customer.getName());
			get().setCustomerAddress(customer.getAddress());
		}
	
		private void setDetails(List<BillableDetail> l) {
			get().setDetails(l);
		}
	
		public void setId(Long id) {
			get().setId(id);
		}
	
		public void setInvoiceDateUponValidation(LocalDate d) throws Exception {
			validateDate(d);
			setInvoiceDate(d);
		}
	
		private void validateDate(LocalDate d) throws Exception {
			if (isNewAndOffSite())
				throw new NotAllowedOffSiteTransactionException();
			if (!credentialService.isUser(MANAGER) && isNew())
				validateOrderDate(d);
		}
	
		protected void validateOrderDate(LocalDate d) throws DateInThePastException, UnauthorizedUserException, Exception {
			verifyUserAuthorization();
			verifyNotInThePast(d);
			verifyAllPickedSalesOrderHaveBeenBilled(d);
			verifyAllCashBillablesHaveBeenFullyPaid(d);
		}
	
		protected void verifyUserAuthorization() throws UnauthorizedUserException {
			if (credentialService.isUser(MANAGER))
				return;
			if ((!credentialService.isUser(SELLER) && !credentialService.isUser(SALES_ENCODER)) //
					&& (isABadOrder() || isAReturnOrder() || isAnInvoice() || isADeliveryReport()))
				throw new UnauthorizedUserException("Sales Encoders only");
			if (!credentialService.isUser(SELLER) && isASalesOrder())
				throw new UnauthorizedUserException("Sellers only");
			if ((!credentialService.isUser(STORE_KEEPER) && !credentialService.isUser(STOCK_CHECKER)) //
					&& (isASalesReturn() || isAPurchaseOrder() || isAPurchaseReceipt()))
				throw new UnauthorizedUserException("Storekeepers and\nStock Checkers only");
		}
	
		protected void verifyNotInThePast(LocalDate d) throws DateInThePastException {
			if (d.isBefore(goLiveDate()))
				return;
			if (d.isBefore(syncService.getServerDate())) {
				reset();
				throw new DateInThePastException();
			}
		}
	
		protected LocalDate goLiveDate() {
			return DateTimeUtils.toDate(goLive);
		}
	
		private void verifyAllPickedSalesOrderHaveBeenBilled(LocalDate d) throws Exception {
			if (isASalesOrder() && !credentialService.isUser(MANAGER))
				verifyAllPickedSalesOrderHaveBeenBilled(credentialService.username(), d);
		}
	
		protected void verifyAllCashBillablesHaveBeenFullyPaid(LocalDate d) throws Exception {
			if (!isASalesOrder() || credentialService.isUser(MANAGER))
				return;
			Billable b = getNotFullyPaidCOD(d);
			if (b == null)
				return;
			reset();
			throw new NotFullyPaidCashBillableException(b.getOrderNo());
		}
	
		private Billable getNotFullyPaidCOD(LocalDate d) throws NoServerConnectionException, StoppedServerException,
				FailedAuthenticationException, RestException, InvalidException {
			return findBillable("/notFullyPaidCOD?seller=" + seller() + "&upTo=" + d);
		}
	
		protected String seller() {
			return credentialService.username();
		}
	
		private void setInvoiceDate(LocalDate d) {
			get().setDueDate(d);
		}
	
		public void setItem(Item item) {
			this.item = item;
		}
	
		public void setItemUponValidation(long id) throws Exception {
			if (id == 0)
				return;
			setItem(null);
			setItem(verifyItem(id));
		}
	
		public void setModule(BillableType t) {
			type = t;
		}
	
		public void setOrderDateUponValidation(LocalDate d) throws Exception {
			validateDate(d);
			setOrderDate(d);
		}
	
		protected void setOrderDate(LocalDate d) {
			if (d == null && isABadOrReturnOrder())
				d = syncService.getServerDate();
			get().setOrderDate(d);
		}
	
		public void setQtyUponValidation(UomType uom, BigDecimal qty) throws Exception {
			this.uom = uom;
			this.qty = qty;
		}
	
		protected BigDecimal totalValue(String endPoint) throws NoServerConnectionException, StoppedServerException,
				FailedAuthenticationException, RestException, InvalidException {
			Billable a = findBillable("/" + endPoint + "?customer=" + customer.getId());
			return a == null ? BigDecimal.ZERO : a.getTotalValue();
		}
	
		public void setReceivingDetail(ReceivingDetail detail) {
			receivingDetail = (BillableDetail) detail;
		}
	
		public void updatePerValidity(Boolean isValid, String remarks) {
			if (isValid != null)
				try {
					if (isAnInvoice() && !isValid)
						remittanceService.nullifyPaymentData(get());
					PricedBillableService.super.updatePerValidity(isValid, remarks);
				} catch (Throwable e) {
					e.printStackTrace();
				}
		}
	
		public void updateSummaries(List<BillableDetail> l) {
			if (!(isAnInvoice() && isADeliveryReport()))
				setDetails(l);
			if (isNew() && (isAPurchaseOrder() || isASalesOrder() || isABadOrder()) || isAnInvoice() || isADeliveryReport())
				updateTotals();
			computeUnpaid();
		}
	
		private void updateTotals() {
			get().setGrossValue(computeGross());
			get().setDiscounts(getDiscountTextList());
			get().setTotalValue(computeTotal());
		}
	
		private BigDecimal computeGross() {
			try {
				BigDecimal gross = getDetails().stream().map(d -> computeSubtotal(d)).reduce(ZERO, BigDecimal::add);
				return isNegative(gross) ? null : gross;
			} catch (Exception e) {
				return null;
			}
		}
	
		protected List<String> getDiscountTextList() {
			return null;
		}
	
		private BigDecimal computeTotal() {
			return get().getGrossValue().subtract(getTotalPercentageBasedDiscountValue());
		}
	
		protected BigDecimal getTotalPercentageBasedDiscountValue() {
			return BigDecimal.ZERO;
		}
	
		private void computeUnpaid() {
			if (get().getPayments() == null || get().getPayments().isEmpty())
				get().setUnpaidValue(total());
		}
	
		private BigDecimal total() {
			BigDecimal t = get().getTotalValue();
			return t != null ? t : ZERO;
		}
	
		public void updateUponReferenceIdValidation(long id) throws Exception {
			if (isNewAndOffSite())
				throw new NotAllowedOffSiteTransactionException();
			Billable reference = validateBooking(id);
			updateBasedOnBooking(reference);
		}
	
		protected boolean isNewAndOffSite() {
			return isNew() && isOffSite();
		}
	
		protected void updateBasedOnBooking(Billable reference) {
			originalDetails = reference.getDetails();
			setBillableData(reference);
			if (isBilling())
				setBilledByUser();
		}
	
		private void setBillableData(Billable b) {
			if (isAPurchaseReceipt())
				b.getDetails().forEach(d -> d.setReturnedQty(d.getQty()));
			if (isASalesReturn())
				b.setDetails(null);
			if (isAnInvoice())
				b = setOrderNo(b);
			set(b);
		}
	
		private Billable setOrderNo(Billable b) {
			b.setPrefix(getPrefix());
			b.setNumId(getNumId());
			b.setPrefix(getPrefix());
			return b;
		}
	
		public void updateUponCustomerIdValidation(Long id) throws Exception {
			validateCustomer(id);
			setCustomerRelatedData();
		}
	
		protected void validateCustomer(Long id) throws Exception {
			if (isNewAndOffSite())
				throw new NotAllowedOffSiteTransactionException();
			setCustomer(findCustomerById(id));
			//TODO verifyAllCollectionsHaveBeenDeposited(PaymentType.CASH);
		}
	
		private Customer findCustomerById(Long id) throws Exception {
			Customer c = customerService.find(id);
			if (c.getDeactivatedOn() != null)
				throw new DeactivatedException(c.getName());
			return c;
		}
	
		protected void verifyAllCollectionsHaveBeenDeposited(PaymentType t) throws Exception {
			if (!isASalesOrder() || credentialService.isUser(MANAGER))
				return;
			Remittance p = remittanceService.getUndepositedPayment(t, credentialService.username(), getOrderDate());
			if (p == null)
				return;
			reset();
			throw new UndepositedPaymentException(t, p.getId());
		}
	
		protected Billable validateBooking(Long id) throws Exception {
			Billable reference = findReference(id);
			confirmBookingExists(id.toString(), reference);
			confirmBookingIsStillOpen(id.toString(), reference);
			return reference;
		}
	
		protected Billable findReference(long id) throws NoServerConnectionException, StoppedServerException,
				FailedAuthenticationException, RestException, InvalidException {
			return findBillable("/" + getBookingModule() + "?id=" + id);
		}
	
		protected void confirmBookingExists(String id, Billable b) throws NotFoundException {
			if (b == null)
				throw new NotFoundException(getReferenceName() + " No. " + id);
		}
	
		protected void confirmBookingIsStillOpen(String id, Billable b)
				throws AlreadyReceivedBookingIdException, AlreadyReferencedBookingIdException, NotPickedBookingIdException {
			if (isBilling())
				confirmBookingIsStillBillable(id, b);
			else
				confirmBookingIsStillReceivable(id, b);
		}
	
		private void confirmBookingIsStillBillable(String id, Billable b)
				throws AlreadyReferencedBookingIdException, NotPickedBookingIdException {
			confirmBookingIsUnreferenced(id, b);
			confirmBookingHasBeenPicked(id, b);
		}
	
		private void confirmBookingIsUnreferenced(String id, Billable b) throws AlreadyReferencedBookingIdException {
			if (!isZero(b.getNumId()))
				throw new AlreadyReferencedBookingIdException(id, b);
		}
	
		private void confirmBookingHasBeenPicked(String id, Billable b) throws NotPickedBookingIdException {
			if (b.getTruck() == null)
				throw new NotPickedBookingIdException(id);
		}
	
		private void confirmBookingIsStillReceivable(String id, Billable b) throws AlreadyReceivedBookingIdException {
			Long receivingId = b.getReceivingId();
			if (receivingId != null)
				throw new AlreadyReceivedBookingIdException(getReceivingReferenceName(b), id, receivingId);
		}
	
		protected String getReceivingReferenceName(Billable b) {
			return getReferenceName();
		}
	
		public Item verifyItem(Long id) throws Exception {
			confirmTransactionIsAllowed();
			Item i = confirmItemExistsAndIsNotDeactivated(id);
			confirmItemIsAllowedToBeSoldToCurrentCustomer(i);
			return i;
		}
	
		private void confirmTransactionIsAllowed() throws AlreadyBilledBookingException {
			if (isASalesReturn() && !getOrderNo().isEmpty())
				throw new AlreadyBilledBookingException(getBookingId(), getOrderNo());
		}
	
		protected void confirmItemIsAllowedToBeSoldToCurrentCustomer(Item item) throws NotAnItemToBeSoldToCustomerException {
			try {
				primaryUomPrice = getLatestUnitPrice(item);
				logger.info("\n    PrimaryUomPrice = " + primaryUomPrice);
			} catch (Exception e) {
				throw new NotAnItemToBeSoldToCustomerException(item, customer);
			}
		}
	
		private BigDecimal getLatestUnitPrice(Item i) {
			Optional<Price> o = getOptionalPrice(i, customer.getPrimaryPricingType().getName());
			logger.info("\n    PrimaryOptionalPrice = " + o.orElse(new Price()));
			if (!o.isPresent() && customer.getAlternatePricingType() != null)
				o = getOptionalPrice(i, customer.getAlternatePricingType().getName());
			return o.isPresent() ? o.get().getPriceValue() : null;
		}
	
		protected Optional<Price> getOptionalPrice(Item i, String pricing) {
			Stream<Price> prices = i.getPriceList().stream() //
					.filter(p -> p.getType().getName().equalsIgnoreCase(pricing)
							&& isApprovedAndStartDateIsNotInTheFuture(p, getOrderDate()));
			logger.info("\n    Item = " + i);
			logger.info("\n    Pricing = " + pricing);
			logger.info("\n    OrderDate = " + getOrderDate());
			logger.info("\n    Stream<Price> = " + prices);
			return prices.max(Price::compareTo);
		}
	
		public String username() {
			return credentialService.username();
		}
		*/
}
