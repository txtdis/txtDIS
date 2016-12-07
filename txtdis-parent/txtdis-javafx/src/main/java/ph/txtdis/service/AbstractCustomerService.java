package ph.txtdis.service;

import static java.time.LocalDate.now;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.springframework.util.StringUtils.capitalize;
import static ph.txtdis.type.ScriptType.CREDIT_APPROVAL;
import static ph.txtdis.type.ScriptType.CUSTOMER_DISCOUNT_APPROVAL;
import static ph.txtdis.type.UserType.MANAGER;
import static ph.txtdis.util.DateTimeUtils.toHypenatedYearMonthDay;
import static ph.txtdis.util.PhoneUtils.isPhone;
import static ph.txtdis.util.PhoneUtils.persistPhone;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import ph.txtdis.dto.Billable;
import ph.txtdis.dto.Channel;
import ph.txtdis.dto.CreditDetail;
import ph.txtdis.dto.Customer;
import ph.txtdis.dto.CustomerDiscount;
import ph.txtdis.dto.EntityDecisionNeeded;
import ph.txtdis.dto.Keyed;
import ph.txtdis.dto.Location;
import ph.txtdis.dto.PartnerType;
import ph.txtdis.dto.Route;
import ph.txtdis.dto.Routing;
import ph.txtdis.dto.WeeklyVisit;
import ph.txtdis.excel.ExcelWriter;
import ph.txtdis.exception.DateInThePastException;
import ph.txtdis.exception.DeactivatedException;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.InvalidPhoneException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.NotAllowedOffSiteTransactionException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;
import ph.txtdis.exception.UnauthorizedUserException;
import ph.txtdis.fx.table.AppTable;
import ph.txtdis.info.Information;
import ph.txtdis.info.SuccessfulSaveInfo;
import ph.txtdis.type.BillingType;
import ph.txtdis.type.ScriptType;
import ph.txtdis.type.UserType;
import ph.txtdis.type.VisitFrequency;
import ph.txtdis.util.ClientTypeMap;
import ph.txtdis.util.DateTimeUtils;

public abstract class AbstractCustomerService implements CustomerService {

	private static final String DISCOUNT_TAB = "Customer Discount";

	private static final String CREDIT_TAB = "Credit Details";

	@Autowired
	private ChannelService channelService;

	@Autowired
	private CredentialService credentialService;

	@Autowired
	private ItemFamilyService familyService;

	@Autowired
	private LocationService locationService;

	@Autowired
	private PricingTypeService pricingTypeService;

	@Autowired
	private ReadOnlyService<Customer> readOnlyCustomerService;

	@Autowired
	private SavingService<Customer> savingService;

	@Autowired
	private ScriptService scriptService;

	@Autowired
	private SpunService<Customer, Long> spunService;

	@Autowired
	private RestServerService serverService;

	@Autowired
	private ExcelWriter excel;

	@Autowired
	private ClientTypeMap typeMap;

	@Value("${prefix.module}")
	private String modulePrefix;

	private List<Customer> customers;

	private String tab;

	protected Customer customer;

	public AbstractCustomerService() {
		reset();
	}

	@Override
	public void reset() {
		customer = new Customer();
		customers = null;
		tab = null;
	}

	@Override
	public boolean canApprove() {
		return credentialService.isUser(MANAGER);
	}

	@Override
	public CreditDetail createCreditLineUponValidation(int term, int gracePeriod, BigDecimal creditLimit,
			LocalDate startDate) throws DateInThePastException, DuplicateException {
		validateStartDate(creditDetails(), startDate);
		return createCreditLine(term, gracePeriod, creditLimit, startDate);
	}

	private List<CreditDetail> creditDetails() {
		if (get().getCreditDetails() == null)
			get().setCreditDetails(new ArrayList<>());
		return get().getCreditDetails();
	}

	private CreditDetail createCreditLine(int term, int gracePeriod, BigDecimal creditLimit, LocalDate startDate) {
		CreditDetail customer = new CreditDetail();
		customer.setTermInDays(term);
		customer.setGracePeriodInDays(gracePeriod);
		customer.setCreditLimit(creditLimit);
		customer.setStartDate(startDate);
		updateCreditDetails(customer);
		return customer;
	}

	private void updateCreditDetails(CreditDetail credit) {
		List<CreditDetail> list = new ArrayList<>(get().getCreditDetails());
		list.add(credit);
		get().setCreditDetails(list);
	}

	@Override
	public Routing createRouteAssignmentUponValidation(Route route, LocalDate startDate)
			throws DateInThePastException, DuplicateException {
		validateStartDate(routeHistory(), startDate);
		return createRouteAssignment(route, startDate);
	}

	private List<Routing> routeHistory() {
		if (get().getRouteHistory() == null)
			setRouteHistory(emptyList());
		return get().getRouteHistory();
	}

	private Routing createRouteAssignment(Route route, LocalDate startDate) {
		Routing r = new Routing();
		r.setRoute(route);
		r.setStartDate(startDate);
		updateRouteHistory(r);
		return r;
	}

	private void updateRouteHistory(Routing routing) {
		List<Routing> list = new ArrayList<>(get().getRouteHistory());
		list.add(routing);
		setRouteHistory(list);
	}

	protected List<CustomerDiscount> customerDiscounts() {
		if (get().getCustomerDiscounts() == null)
			get().setCustomerDiscounts(new ArrayList<>());
		return get().getCustomerDiscounts();
	}

	@Override
	public Customer findByName(String text) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		Customer customer = getOne("/find?name=" + text);
		if (customer != null && customer.getDeactivatedOn() != null)
			throw new DeactivatedException(customer.getName());
		return customer;
	}

	private Customer getOne(String endPt) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		return readOnlyCustomerService.module(getModule()).getOne(endPt);
	}

	@Override
	public Customer findByVendorId(Long id) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		return getOne("/get?vendorId=" + id);
	}

	@Override
	public Customer findNoContactDetails() throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		return getOne("/noContactDetails");
	}

	@Override
	public Customer findNoDesignation() throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		return getOne("/noDesignation");
	}

	@Override
	public Customer findNoMobileNo() throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		return getOne("/noMobile");
	}

	@Override
	public Customer findNoStreetAddress() throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		return getOne("/noStreetAddress");
	}

	@Override
	public Customer findNoSurname() throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		return getOne("/noSurname");
	}

	@Override
	public Customer findNotCorrectBarangayAddress() throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		return getOne("/notCorrectBarangayAddress");
	}

	@Override
	public Customer findNotCorrectCityAddress() throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		return getOne("/notCorrectCityAddress");
	}

	@Override
	public Customer findNotCorrectProvincialAddress() throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		return getOne("/notCorrectProvincialAddress");
	}

	public Customer findNotTheSameVisitFrequencyAndSchedule() throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		return getOne("/notTheSameVisitFrequencyAndSchedule");
	}

	@Override
	public Customer findNotTheSameWeeksOneAndFiveVisitSchedule() throws NoServerConnectionException,
			StoppedServerException, FailedAuthenticationException, RestException, InvalidException {
		return getOne("/notTheSameWeeksOneAndFiveVisitSchedules");
	}

	@Override
	public Customer findNoVisitSchedule() throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		return getOne("/noVisitSchedule");
	}

	@Override
	@SuppressWarnings("unchecked")
	public Customer get() {
		if (customer == null)
			reset();
		return customer;
	}

	@Override
	public String getAlternateName() {
		return capitalize(getModule());
	}

	@Override
	public Location getBarangay() {
		return get().getBarangay();
	}

	@Override
	public BillingType getBillingType(Billable b) throws Exception {
		Customer c = find(b.getCustomerId());
		Channel ch = c.getChannel();
		return ch == null ? null : ch.getBillingType();
	}

	@Override
	public Channel getChannel() {
		return get().getChannel();
	}

	@Override
	public String getCreatedBy() {
		return get().getCreatedBy();
	}

	@Override
	public ZonedDateTime getCreatedOn() {
		return get().getCreatedOn();
	}

	@Override
	public Location getCity() {
		return get().getCity();
	}

	@Override
	public String getDeactivatedBy() {
		return get().getDeactivatedBy();
	}

	@Override
	public ZonedDateTime getDeactivatedOn() {
		return get().getDeactivatedOn();
	}

	@Override
	public String getDecidedBy() {
		return credentialService.username();
	}

	@Override
	public ZonedDateTime getDecidedOn() {
		return ZonedDateTime.now();
	}

	@Override
	public String getModule() {
		return "customer";
	}

	@Override
	public String getName() {
		return get().getName();
	}

	@Override
	public Location getProvince() {
		return get().getProvince();
	}

	@Override
	public List<Routing> getRouteHistory() {
		return get().getRouteHistory();
	}

	@Override
	public String getStreet() {
		return get().getStreet();
	}

	@Override
	public VisitFrequency getVisitFrequency() {
		return get().getVisitFrequency();
	}

	@Override
	public Long getId() {
		return get().getId();
	}

	@Override
	public Boolean getIsValid() {
		return get().getDeactivatedOn() != null;
	}

	@Override
	public ItemFamilyService getItemFamilyService() {
		return familyService;
	}

	private List<Customer> getList(String endpoint) throws Exception {
		return readOnlyCustomerService.module(getModule()).getList(endpoint);
	}

	@Override
	public ReadOnlyService<Customer> getListedReadOnlyService() {
		return readOnlyCustomerService;
	}

	@Override
	public Long getParentId() {
		return getParent() == null ? null : getParent().getId();
	}

	private Customer getParent() {
		return get().getParent();
	}

	@Override
	public String getParentName() {
		return getParent() == null ? null : getParent().getName();
	}

	@Override
	@SuppressWarnings("unchecked")
	public ReadOnlyService<Customer> getReadOnlyService() {
		return readOnlyCustomerService;
	}

	@Override
	public String getRemarks() {
		return "";
	}

	@Override
	@SuppressWarnings("unchecked")
	public SavingService<Customer> getSavingService() {
		return savingService;
	}

	@Override
	public ScriptService getScriptService() {
		return scriptService;
	}

	@Override
	public <T extends EntityDecisionNeeded<Long>> ScriptType getScriptType(T d) {
		return d instanceof CreditDetail ? CREDIT_APPROVAL : CUSTOMER_DISCOUNT_APPROVAL;
	}

	@Override
	public SpunService<Customer, Long> getSpunService() {
		return spunService;
	}

	@Override
	public String getTitleText() {
		return credentialService.username() + "@" + modulePrefix + " " + CustomerService.super.getTitleText();
	}

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public Customer getVendor() throws Exception {
		return getOne("/vendor");
	}

	@Override
	public List<WeeklyVisit> getVisitSchedule(Channel customer) {
		return customer != null && isAVisitedChannel(customer) ? visitSchedule() : null;
	}

	private boolean isAVisitedChannel(Channel customer) {
		String n = customer.getName();
		return !listVisitedChannels().contains(n);
	}

	private List<WeeklyVisit> visitSchedule() {
		List<WeeklyVisit> l = get().getVisitSchedule();
		return l.isEmpty() ? blankSchedule() : l;
	}

	private List<WeeklyVisit> blankSchedule() {
		List<WeeklyVisit> l = new ArrayList<>();
		for (int i = 1; i <= 5; i++)
			l.add(new WeeklyVisit(i, false, false, false, false, false, false, false));
		return l;
	}

	@Override
	public boolean isDeliveryScheduledOnThisDate(Customer c, LocalDate d) {
		return c.getVisitSchedule().stream().anyMatch(v -> isVisitScheduleThisWeek(v, d) && isVisitScheduleThisDay(v, d));
	}

	private boolean isVisitScheduleThisWeek(WeeklyVisit v, LocalDate d) {
		int weekNo = DateTimeUtils.weekNo(d);
		return v.getWeekNo() == weekNo;
	}

	private boolean isVisitScheduleThisDay(WeeklyVisit v, LocalDate d) {
		DayOfWeek day = d.getDayOfWeek();
		if (v.isSun() && day == DayOfWeek.SUNDAY)
			return true;
		if (v.isMon() && day == DayOfWeek.MONDAY)
			return true;
		if (v.isTue() && day == DayOfWeek.TUESDAY)
			return true;
		if (v.isWed() && day == DayOfWeek.WEDNESDAY)
			return true;
		if (v.isThu() && day == DayOfWeek.THURSDAY)
			return true;
		if (v.isFri() && day == DayOfWeek.FRIDAY)
			return true;
		if (v.isSat() && day == DayOfWeek.SATURDAY)
			return true;
		return false;
	}

	@Override
	public boolean isOffSite() {
		return serverService.isOffSite();
	}

	@Override
	public List<Customer> list() {
		return customers;
	}

	@Override
	public List<String> listBankNames() {
		return listBanks().stream().map(c -> c.getName()).collect(Collectors.toList());
	}

	@Override
	public List<Customer> listBanks() {
		try {
			return getList("/banks");
		} catch (Exception e) {
			return emptyList();
		}
	}

	@Override
	public List<Location> listBarangays(Location city) {
		return locationService.listBarangays(city);
	}

	@Override
	public List<Channel> listChannels() {
		try {
			return channelService.list();
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}

	@Override
	public List<Location> listCities(Location province) {
		return locationService.listCities(province);
	}

	@Override
	public List<Customer> listByScheduledRouteAndGoodCreditStanding(Route r) {
		try {
			return getList("/route?id=" + r.getId());
		} catch (Exception e) {
			return emptyList();
		}
	}

	@Override
	public List<String> listExTrucks() {
		try {
			return getList("/exTrucks").stream().map(e -> e.getName()).collect(Collectors.toList());
		} catch (Exception e) {
			return emptyList();
		}
	}

	@Override
	public List<Location> listProvinces() {
		return locationService.listProvinces();
	}

	@Override
	public List<PartnerType> listTypes() {
		return isNew() ? asList(PartnerType.values()) : asList(get().getType());
	}

	@Override
	public List<Channel> listVisitedChannels() {
		return channelService.listVisitedChannels();
	}

	@Override
	public boolean noChangesNeedingApproval(String tab) {
		this.tab = tab;
		if (tab.equals(CREDIT_TAB))
			return noChanges(get().getCreditDetails());
		if (tab.equals(DISCOUNT_TAB))
			return noChanges(get().getCustomerDiscounts());
		return true;
	}

	private <T extends EntityDecisionNeeded<Long>> boolean noChanges(List<T> l) {
		return l == null ? true : !l.stream().anyMatch(d -> d.getIsValid() == null);
	}

	@Override
	public void save() throws Information, Exception {
		scriptService.saveScripts();
		get().setPrimaryPricingType(pricingTypeService.findByName("DEALER"));
		set(savingService.module(getModule()).save(get()));
		throw new SuccessfulSaveInfo(getModuleId() + getOrderNo());
	}

	private String getExcelSheetName() {
		return "Active.Customers";
	}

	@Override
	@SuppressWarnings("unchecked")
	public void saveAsExcel(AppTable<Customer>... tables) throws IOException {
		excel.filename(excelName()).sheetname(getExcelSheetName()).table(tables).write();
	}

	private String excelName() {
		return getExcelSheetName() + "." + toHypenatedYearMonthDay(now());
	}

	@Override
	public List<Customer> search(String text) throws Exception {
		return customers = getList(text.isEmpty() ? "" : "/search?name=" + text);
	}

	@Override
	public <T extends Keyed<Long>> void set(T t) {
		customer = (Customer) t;
	}

	@Override
	public void setBarangay(Location barangay) {
		get().setBarangay(barangay);
	}

	@Override
	public void setChannel(Channel channel) {
		get().setChannel(channel);
	}

	@Override
	public void setCity(Location city) {
		get().setCity(city);
	}

	@Override
	public void setId(Long id) {
		get().setId(id);
	}

	@Override
	public void setName(String name) {
		get().setName(name);
	}

	@Override
	public void setNameIfUnique(String text) throws Exception {
		if (isNew()) {
			if (isOffSite())
				throw new NotAllowedOffSiteTransactionException();
			if (!(credentialService.isUser(UserType.MANAGER) || credentialService.isUser(UserType.SALES_ENCODER)
					|| credentialService.isUser(UserType.SELLER)))
				throw new UnauthorizedUserException("Customer Data Encoders only");
		}
		if (findByName(text) != null)
			throw new DuplicateException(text);
		get().setName(text);
	}

	@Override
	public void setParentIfExists(Long id) throws Exception {
		get().setParent(find(id.toString()));
	}

	@Override
	public void setProvince(Location value) {
		get().setProvince(value);
	}

	@Override
	public void setRouteHistory(List<Routing> routings) {
		get().setRouteHistory(routings);
	}

	@Override
	public void setStreet(String text) {
		get().setStreet(text);
	}

	@Override
	public void setType(PartnerType value) {
		get().setType(value);
	}

	@Override
	public void setVisitFrequency(VisitFrequency freq) {
		get().setVisitFrequency(freq);
	}

	@Override
	public void setVisitSchedule(List<WeeklyVisit> visits) {
		get().setVisitSchedule(visits);
	}

	protected void updateCustomerDiscounts(CustomerDiscount customerDiscount) {
		List<CustomerDiscount> list = new ArrayList<>(get().getCustomerDiscounts());
		list.add(customerDiscount);
		get().setCustomerDiscounts(list);
	}

	@Override
	public void updatePerValidity(Boolean isValid, String remarks) {
		if (tab.equals(CREDIT_TAB))
			approveCreditDetail(isValid, remarks);
		if (tab.equals(DISCOUNT_TAB))
			approveDiscount(isValid, remarks);
	}

	private void approveCreditDetail(Boolean isValid, String remarks) {
		List<CreditDetail> list = approve(get().getCreditDetails(), isValid, remarks);
		get().setCreditDetails(list);
	}

	private void approveDiscount(Boolean isValid, String remarks) {
		List<CustomerDiscount> list = approve(get().getCustomerDiscounts(), isValid, remarks);
		get().setCustomerDiscounts(list);
	}

	@Override
	public void validatePhoneNo(String ph) throws Exception {
		if (!isPhone(ph))
			throw new InvalidPhoneException(ph);
		get().setMobile(persistPhone(ph));
	}

	@Override
	public String username() {
		return credentialService.username();
	}
}
