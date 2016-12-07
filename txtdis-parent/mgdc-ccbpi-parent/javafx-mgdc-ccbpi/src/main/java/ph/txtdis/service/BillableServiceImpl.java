package ph.txtdis.service;

import org.springframework.stereotype.Service;

//TODO
@Service("billableService")
public class BillableServiceImpl {//extends AbstractBillableService implements CokeBillableService {
	/*
	private static final String ORDER_CONFIRMATION_MODULE = "orderConfirmation";
	
	@Autowired
	private ChannelService channelService;
	
	@Autowired
	private RouteService routeService;
	
	@Value("${route.initial}")
	private String routeInitial;
	
	@Override
	protected BigDecimal computeSubtotal(BillableDetail d) {
		return NumberUtils.divide(super.computeSubtotal(d), new BigDecimal(d.getQtyPerCase()));
	}
	
	@Override
	public Billable findById(String id) throws NumberFormatException, NoServerConnectionException,
			StoppedServerException, FailedAuthenticationException, RestException, InvalidException, NotFoundException {
		if (isADeliveryList())
			return findDeliveryList(id);
		if (isADeliveryList())
			return findLoadManifest(id);
		return findOrderConfirmation(id);
	}
	
	private Billable findDeliveryList(String id) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException, NotFoundException {
		Billable b = billableReadOnlyService.module(getSpunModule())
				.getOne("/" + getSpunModule() + "?shipment=" + shipmentId(id) + "&route=" + route(id));
		return throwNotFoundExceptionIfNull(b, id);
	}
	
	private Long shipmentId(String id) throws NotFoundException {
		try {
			return Long.valueOf(shipment(id));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			throw new NotFoundException(getModuleId() + id);
		}
	}
	
	private String shipment(String id) {
		return StringUtils.substringBefore(id, routeInitial);
	}
	
	private String route(String id) {
		return routeInitial + StringUtils.substringAfter(id, routeInitial);
	}
	
	private Billable throwNotFoundExceptionIfNull(Billable b, String id) throws NotFoundException {
		if (b == null)
			throw new NotFoundException(getModuleId() + id);
		return b;
	}
	
	private Billable findLoadManifest(String id) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException, NotFoundException {
		Billable b = billableReadOnlyService.module(getSpunModule())
				.getOne("/" + getSpunModule() + "?shipment=" + shipmentId(id));
		return throwNotFoundExceptionIfNull(b, id);
	}
	
	private Billable findOrderConfirmation(String id) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException, NotFoundException {
		Billable b = orderConfirmationReadOnlyService().getOne("/" + ORDER_CONFIRMATION_MODULE + "?outletId="
				+ outletId(id) + "&date=" + orderDate(id) + "&orderNo=" + orderNo(id));
		return throwNotFoundExceptionIfNull(b, id);
	}
	
	private ReadOnlyService<Billable> orderConfirmationReadOnlyService() {
		return billableReadOnlyService.module(ORDER_CONFIRMATION_MODULE);
	}
	
	private Long outletId(String id) throws NotFoundException {
		try {
			return Long.valueOf(ocsOutletId(id));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			throw new NotFoundException("OCS No." + id);
		}
	}
	
	private String ocsOutletId(String id) {
		return StringUtils.substringBefore(id, "-");
	}
	
	private LocalDate orderDate(String id) throws NotFoundException {
		try {
			return LocalDate.parse(ocsDate(id), DateTimeUtils.orderConfirmationFormat());
		} catch (DateTimeParseException e) {
			e.printStackTrace();
			throw new NotFoundException(getModuleId() + id);
		}
	}
	
	private String ocsDate(String id) {
		return StringUtils.substringBetween(id, "-", "/");
	}
	
	private Long orderNo(String id) throws NotFoundException {
		try {
			return Long.valueOf(ocsOrderNo(id));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			throw new NotFoundException(getModuleId() + id);
		}
	}
	
	private String ocsOrderNo(String id) {
		return StringUtils.substringAfter(id, "/");
	}
	
	@Override
	public String getAlternateName() {
		if (isADeliveryList() || isALoadManifest())
			return "Shipment";
		if (isAnOrderConfirmation())
			return "Order";
		return super.getAlternateName();
	}
	
	@Override
	public Long getCustomerId() {
		return get().getCustomerVendorId();
	}
	
	@Override
	public String getHeaderText() {
		if (isADeliveryList())
			return "Delivery List";
		if (isALoadManifest())
			return "Load Manifest";
		if (isAnOrderConfirmation())
			return "Order Confirmation";
		if (isASalesReturn())
			return "Order Return";
		return super.getHeaderText();
	}
	
	@Override
	public String getModuleIdNo() {
		if (isADeliveryList())
			return "DDL No. " + getIdNo() + getSuffix();
		if (isALoadManifest())
			return "L/M No. " + getIdNo();
		if (isAnOrderConfirmation() || isASalesReturn())
			return "OCS No. " + orderConfirmationNo(get());
		return super.getModuleIdNo();
	}
	
	@Override
	protected BigDecimal getPrice(BillableDetail d) {
		try {
			return getOptionalPrice(item, getPricing()).get().getPriceValue();
		} catch (Exception e) {
			e.printStackTrace();
			return BigDecimal.ZERO;
		}
	}
	
	private String getPricing() {
		return isAnOrderConfirmation() ? "DEALER" : "PURCHASE";
	}
	
	@Override
	public String getReferenceName() {
		if (isADeliveryListOrAnOrderConfirmationOrALoadManifest())
			return "Route";
		if (!isAReceiving())
			return super.getReferenceName();
		return "OCS";
	}
	
	@Override
	public String getReferenceOrderNo() {
		return orderConfirmationNo(get());
	}
	
	@Override
	public String getSubhead() {
		return "";
	}
	
	@Override
	public String getSpunModule() {
		if (isADeliveryList())
			return "deliveryList";
		if (isALoadManifest())
			return "loadManifest";
		if (isAnOrderConfirmation())
			return "orderConfirmation";
		return super.getSpunModule();
	}
	
	@Override
	public String getTitleText() {
		return credentialService.username() + "@" + modulePrefix + " " + (isNew() ? newModule() : getModuleIdNo());
	}
	
	@Override
	public List<BigDecimal> getTotals(List<BillableDetail> l) {
		BigDecimal total = l.stream().map(BillableDetail::getSubtotalValue).reduce(BigDecimal.ZERO, BigDecimal::add);
		get().setTotalValue(total);
		get().setGrossValue(total);
		get().setUnpaidValue(isAnOrderConfirmation() ? total : BigDecimal.ZERO);
		return Arrays.asList(total);
	}
	
	@Override
	public boolean isAppendable() {
		return isNew() && (isADeliveryListOrAnOrderConfirmationOrALoadManifest() || isASalesReturn());
	}
	
	@Override
	public List<String> listRoutes() {
		if (isNew())
			return routeService.listNames();
		return Arrays.asList(get().getSuffix());
	}
	
	@Override
	public List<String> listTypes() {
		if (isNew())
			return channelService.listNames();
		return Arrays.asList(get().getPrefix());
	}
	
	@Override
	public List<Booking> listUnpicked(LocalDate d) {
		try {
			List<Billable> l = orderConfirmationReadOnlyService().getList("/unpicked?date=" + d);
			return l.stream().map(a -> toBooking(a)).collect(toList());
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}
	
	@Override
	public void open(String id) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException, NotFoundException {
		set(findOrderConfirmationByBookingId(id));
	}
	
	private Billable findOrderConfirmationByBookingId(String id) throws NoServerConnectionException,
			StoppedServerException, FailedAuthenticationException, RestException, InvalidException, NotFoundException {
		Billable b = orderConfirmationReadOnlyService().getOne("/booking?id=" + id);
		return throwNotFoundExceptionIfNull(b, id);
	}
	
	@Override
	protected Booking toBooking(Billable a) {
		Booking b = new Booking();
		b.setId(a.getBookingId());
		b.setCustomer(a.getCustomerName());
		b.setLocation(orderConfirmationNo(a));
		b.setRoute(a.getSuffix());
		return b;
	}
	
	private String orderConfirmationNo(Billable a) {
		if (a.getCustomerVendorId() == null)
			return "";
		return a.getCustomerVendorId() + "-" + DateTimeUtils.toOrderConfirmationDate(a.getOrderDate()) + "/"
				+ a.getNumId();
	}
	
	@Override
	public void setCustomerRelatedData() {
		super.setCustomerRelatedData();
		get().setCustomerVendorId(customer.getVendorId());
	}
	
	@Override
	public void setDueDate(LocalDate due) {
		get().setDueDate(due);
	}
	
	@Override
	public void setRoute(String route) {
		get().setSuffix(route);
	}
	
	@Override
	public void setType(String type) {
		get().setPrefix(type);
	}
	
	@Override
	public void updateUponCustomerVendorIdValidation(Long id) throws Exception {
		validateCustomer(id);
		setCustomerRelatedData();
	}
	
	@Override
	public void updateUponIdNoValidation(Long id) {
		get().setNumId(id);
	}
	
	@Override
	public void updateUponOrderNoValidation(String prefix, Long id, String suffix) throws Exception {
	}
	
	@Override
	public void updateUponReferenceOrderNoValidation(String id) throws Exception {
		if (!isNew())
			return;
		if (isOffSite())
			throw new NotAllowedOffSiteTransactionException();
		updateBasedOnBooking(validateReferenceOrderNo(id));
	}
	
	private Billable validateReferenceOrderNo(String id) throws Exception {
		Billable reference = findOrderConfirmation(id);
		confirmBookingExists(id, reference);
		confirmBookingIsStillOpen(id, reference);
		return reference;
	}
	
	@Override
	protected void validateCustomer(Long id) throws Exception {
		if (isNewAndOffSite())
			throw new NotAllowedOffSiteTransactionException();
		setCustomer(findCustomerByVendorId(id));
	}
	
	private Customer findCustomerByVendorId(Long id) throws Exception {
		Customer c = customerService.findByVendorId(id);
		if (c == null)
			throw new NotFoundException("Customer No." + id);
		return c;
	}
	
	@Override
	public Item verifyItem(Long id) throws Exception {
		return itemService.findByVendorId(id);
	}
	*/
}
