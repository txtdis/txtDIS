package ph.txtdis.mgdc.gsm.service;

import javafx.collections.ObservableList;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import ph.txtdis.dto.*;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.exception.InvalidPhoneException;
import ph.txtdis.exception.UnauthorizedUserException;
import ph.txtdis.info.Information;
import ph.txtdis.info.SuccessfulSaveInfo;
import ph.txtdis.mgdc.gsm.dto.Channel;
import ph.txtdis.mgdc.gsm.dto.Customer;
import ph.txtdis.mgdc.gsm.dto.CustomerDiscount;
import ph.txtdis.mgdc.service.LocationService;
import ph.txtdis.mgdc.service.PricingTypeService;
import ph.txtdis.mgdc.service.RouteService;
import ph.txtdis.type.PartnerType;
import ph.txtdis.type.VisitFrequency;
import ph.txtdis.util.DateTimeUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.type.DeliveryType.PICK_UP;
import static ph.txtdis.type.PriceType.DEALER;
import static ph.txtdis.type.UserType.*;
import static ph.txtdis.util.PhoneUtils.isPhone;
import static ph.txtdis.util.PhoneUtils.persistPhone;
import static ph.txtdis.util.UserUtils.isUser;

public abstract class AbstractCreditAndDiscountGivenCustomerService //
	extends AbstractItemDeliveredCustomerService //
	implements CreditedAndDiscountedCustomerService {

	private static final String DISCOUNT_TAB = "Customer Discount";

	private static final String CREDIT_TAB = "Credit Details";

	private static Logger logger = getLogger(AbstractCreditAndDiscountGivenCustomerService.class);

	@Autowired
	protected RouteService routeService;

	@Autowired
	private ChannelService channelService;

	@Autowired
	private LocationService locationService;

	@Autowired
	private PricingTypeService pricingTypeService;

	@Value("${go.live}")
	private String goLive;

	@Value("${prefix.module}")
	private String modulePrefix;

	private String tab;

	@Override
	public CreditDetail createCreditLineUponValidation(int term,
	                                                   int gracePeriod,
	                                                   BigDecimal creditLimit,
	                                                   LocalDate startDate) throws Exception {
		validateStartDate(getCreditDetails(), startDate);
		return createCreditLine(term, gracePeriod, creditLimit, startDate);
	}

	@Override
	public List<CreditDetail> getCreditDetails() {
		return get().getCreditDetails();
	}

	private CreditDetail createCreditLine(int term, int gracePeriod, BigDecimal creditLimit, LocalDate startDate) {
		CreditDetail c = new CreditDetail();
		c.setTermInDays(term);
		c.setGracePeriodInDays(gracePeriod);
		c.setCreditLimit(creditLimit);
		c.setStartDate(startDate);
		return c;
	}

	@Override
	public void setCreditDetails(ObservableList<CreditDetail> credits) {
		get().setCreditDetails(credits);
	}

	@Override
	public Routing createRouteAssignmentUponValidation(Route route, LocalDate startDate) throws Exception {
		validateStartDate(getRouteHistory(), startDate);
		return createRouteAssignment(route, startDate);
	}

	@Override
	public List<Routing> getRouteHistory() {
		return get().getRouteHistory();
	}

	protected Routing createRouteAssignment(Route route, LocalDate startDate) {
		Routing r = new Routing();
		r.setRoute(route);
		r.setStartDate(startDate);
		return r;
	}

	@Override
	public void setRouteHistory(List<Routing> routings) {
		get().setRouteHistory(routings);
	}

	@Override
	public Customer findNoContactDetails() throws Exception {
		return getOne("/noContactDetails");
	}

	@Override
	public Customer findNoDesignation() throws Exception {
		return getOne("/noDesignation");
	}

	@Override
	public Customer findNoMobileNo() throws Exception {
		return getOne("/noMobile");
	}

	@Override
	public Customer findNoStreetAddress() throws Exception {
		return getOne("/noStreetAddress");
	}

	@Override
	public Customer findNoSurname() throws Exception {
		return getOne("/noSurname");
	}

	@Override
	public Customer findNotCorrectBarangayAddress() throws Exception {
		return getOne("/notCorrectBarangayAddress");
	}

	@Override
	public Customer findNotCorrectCityAddress() throws Exception {
		return getOne("/notCorrectCityAddress");
	}

	@Override
	public Customer findNotCorrectProvincialAddress() throws Exception {
		return getOne("/notCorrectProvincialAddress");
	}

	public Customer findNotTheSameVisitFrequencyAndSchedule() throws Exception {
		return getOne("/notTheSameVisitFrequencyAndSchedule");
	}

	@Override
	public Customer findNotTheSameWeeksOneAndFiveVisitSchedule() throws Exception {
		return getOne("/notTheSameWeeksOneAndFiveVisitSchedules");
	}

	@Override
	public Customer findNoVisitSchedule() throws Exception {
		return getOne("/noVisitSchedule");
	}

	@Override
	public Location getBarangay() {
		return get().getBarangay();
	}

	@Override
	public void setBarangay(Location barangay) {
		get().setBarangay(barangay);
	}

	@Override
	public Channel getChannel() {
		return get().getChannel();
	}

	@Override
	public void setChannel(Channel channel) {
		get().setChannel(channel);
	}

	@Override
	public Location getCity() {
		return get().getCity();
	}

	@Override
	public void setCity(Location city) {
		get().setCity(city);
	}

	@Override
	public String getContactName() {
		return get().getContactName();
	}

	@Override
	public void setContactName(String text) {
		get().setContactName(text);
	}

	@Override
	public String getContactSurname() {
		return get().getContactSurname();
	}

	@Override
	public void setContactSurname(String text) {
		get().setContactSurname(text);
	}

	@Override
	public String getContactTitle() {
		return get().getContactTitle();
	}

	@Override
	public void setContactTitle(String text) {
		get().setContactSurname(text);
	}

	@Override
	public long getCreditTerm(Customer c) {
		return getCredit(c, today()).getTermInDays();
	}

	protected CreditDetail getCredit(Customer c, LocalDate date) {
		return c.getCreditDetails().stream() //
			.filter(p -> !p.getStartDate().isAfter(date) && p.getIsValid() == true) //
			.max((a, b) -> a.getStartDate().compareTo(b.getStartDate())) //
			.orElse(new CreditDetail());
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
	public long getGracePeriod(Customer c) {
		return getCredit(c, today()).getTermInDays();
	}

	@Override
	public String getMobile() {
		return get().getMobile();
	}

	@Override
	public String getName() {
		return get().getName();
	}

	@Override
	public void setName(String name) {
		get().setName(name);
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
	public Location getProvince() {
		return get().getProvince();
	}

	@Override
	public void setProvince(Location value) {
		get().setProvince(value);
	}

	@Override
	public String getStreet() {
		return get().getStreet();
	}

	@Override
	public void setStreet(String text) {
		get().setStreet(text);
	}

	@Override
	public String getTitleName() {
		return getUsername() + "@" + modulePrefix + " " + CreditedAndDiscountedCustomerService.super.getTitleName();
	}

	@Override
	public Customer getVendor() throws Exception {
		return getOne("/vendor");
	}

	@Override
	public VisitFrequency getVisitFrequency() {
		return get().getVisitFrequency();
	}

	@Override
	public void setVisitFrequency(VisitFrequency freq) {
		get().setVisitFrequency(freq);
	}

	@Override
	public List<WeeklyVisit> getVisitSchedule(Channel customer) {
		return customer != null && isAVisitedChannel(customer) ? visitSchedule() : null;
	}

	private boolean isAVisitedChannel(Channel channel) {
		return listVisitedChannels().contains(channel);
	}

	private List<WeeklyVisit> visitSchedule() {
		List<WeeklyVisit> l = get().getVisitSchedule();
		return l == null || l.isEmpty() ? blankSchedule() : l;
	}

	@Override
	public List<Channel> listVisitedChannels() {
		return channelService.listVisitedChannels();
	}

	private List<WeeklyVisit> blankSchedule() {
		List<WeeklyVisit> l = new ArrayList<>();
		for (int wkNo = 1; wkNo <= 5; wkNo++)
			l.add(newWeeklyVisit(wkNo));
		return l;
	}

	private WeeklyVisit newWeeklyVisit(int wkNo) {
		WeeklyVisit v = new WeeklyVisit();
		v.setWeekNo(wkNo);
		return v;
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
	public List<String> listOutletNames() {
		try {
			return getList("/outlets").stream().map(e -> e.getName()).collect(toList());
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
		PartnerType p = get().getType();
		return isNew() && p == null ? asList(PartnerType.values()) : asList(p);
	}

	@Override
	public boolean noChangesNeedingApproval(String tab) {
		this.tab = tab;
		if (tab.equals(CREDIT_TAB))
			return noChanges(getCreditDetails());
		if (tab.equals(DISCOUNT_TAB))
			return noChanges(get().getDiscounts());
		return true;
	}

	private <T extends DecisionNeededValidatedCreatedKeyed<Long>> boolean noChanges(List<T> l) {
		return l == null ? true : !l.stream().anyMatch(d -> d.getIsValid() == null);
	}

	@Override
	public void reset() {
		super.reset();
		tab = null;
	}

	@Override
	public void save() throws Information, Exception {
		get().setPrimaryPricingType(pricingTypeService.findByName(DEALER.toString()));
		set(getRestClientServiceForLists().module(getModuleName()).save(get()));
		throw new SuccessfulSaveInfo(getAbbreviatedModuleNoPrompt() + getOrderNo());
	}

	@Override
	public void setNameUponValidation(String text) throws Exception {
		if (isNew() && !(isUser(MANAGER) || isUser(SALES_ENCODER) || isUser(SELLER)))
			throw new UnauthorizedUserException("Customer Data Encoders only");
		if (findByName(text) != null)
			throw new DuplicateException(text);
		get().setName(text);
	}

	@Override
	public void setParentIfExists(Long id) throws Exception {
		get().setParent(findByOrderNo(id.toString()));
	}

	@Override
	public void setRouteAsPickUpAndChannelAsWarehouseSales() throws Exception {
		if (getRouteHistory().isEmpty())
			setRouteAsPickUp();
		setChannel(channelService.findByName("WAREHOUSE SALES"));
	}

	private void setRouteAsPickUp() throws Exception {
		Route pickUpRoute = routeService.findByName(PICK_UP.toString());
		createRouteAssignment(pickUpRoute, goLive());
	}

	protected LocalDate goLive() {
		return DateTimeUtils.toDate(goLive);
	}

	@Override
	public void setVisitSchedule(List<WeeklyVisit> visits) {
		get().setVisitSchedule(visits);
	}

	@Override
	public void updatePerValidity(Boolean isValid, String remarks) {
		if (tab.equals(CREDIT_TAB))
			approveCreditDetail(isValid, remarks);
		if (tab.equals(DISCOUNT_TAB))
			approveDiscount(isValid, remarks);
	}

	private void approveCreditDetail(Boolean isValid, String remarks) {
		List<CreditDetail> l = approve(getCreditDetails(), isValid, remarks);
		logger.info("\n    ApprovedCredit = " + l);
		get().setCreditDetails(l);
	}

	private void approveDiscount(Boolean isValid, String remarks) {
		List<CustomerDiscount> l = approve(get().getDiscounts(), isValid, remarks);
		logger.info("\n    ApprovedDiscount = " + l);
		get().setDiscounts(l);
	}

	@Override
	public void validatePhoneNo(String ph) throws Exception {
		if (!isPhone(ph))
			throw new InvalidPhoneException(ph);
		get().setMobile(persistPhone(ph));
	}
}
