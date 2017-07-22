package ph.txtdis.mgdc.gsm.service.server;

import static java.math.BigDecimal.ZERO;
import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.THURSDAY;
import static java.time.DayOfWeek.TUESDAY;
import static java.time.DayOfWeek.WEDNESDAY;
import static java.time.LocalDate.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static ph.txtdis.type.LocationType.BARANGAY;
import static ph.txtdis.type.LocationType.CITY;
import static ph.txtdis.type.LocationType.PROVINCE;
import static ph.txtdis.type.PartnerType.EX_TRUCK;
import static ph.txtdis.type.PartnerType.FINANCIAL;
import static ph.txtdis.type.PartnerType.INTERNAL;
import static ph.txtdis.type.PartnerType.OUTLET;
import static ph.txtdis.type.PartnerType.VENDOR;
import static ph.txtdis.type.RouteType.PRE_SELL;
import static ph.txtdis.util.DateTimeUtils.endOfDay;
import static ph.txtdis.util.DateTimeUtils.toDate;
import static ph.txtdis.util.NumberUtils.isPositive;
import static ph.txtdis.util.TextUtils.HAS_OVERDUES;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ph.txtdis.dto.CustomerReceivableReport;
import ph.txtdis.dto.Location;
import ph.txtdis.dto.PricingType;
import ph.txtdis.dto.Route;
import ph.txtdis.dto.Routing;
import ph.txtdis.dto.WeeklyVisit;
import ph.txtdis.mgdc.domain.LocationEntity;
import ph.txtdis.mgdc.domain.PricingTypeEntity;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.mgdc.gsm.domain.CreditDetailEntity;
import ph.txtdis.mgdc.gsm.domain.CustomerDiscountEntity;
import ph.txtdis.mgdc.gsm.domain.CustomerEntity;
import ph.txtdis.mgdc.gsm.domain.RoutingEntity;
import ph.txtdis.mgdc.gsm.domain.WeeklyVisitEntity;
import ph.txtdis.mgdc.gsm.dto.Customer;
import ph.txtdis.mgdc.service.server.HolidayService;
import ph.txtdis.mgdc.service.server.PricingTypeService;
import ph.txtdis.service.CredentialService;
import ph.txtdis.service.ReadOnlyService;
import ph.txtdis.service.SavingService;
import ph.txtdis.type.PartnerType;
import ph.txtdis.type.VisitFrequency;
import ph.txtdis.util.DateTimeUtils;

@Service("customerService")
public class CustomerServiceImpl //
		extends AbstractCustomerService //
		implements CreditedAndDiscountedCustomerService {

	private static final String CUSTOMER = "customer";

	@Autowired
	private AllBillingService billingService;

	@Autowired
	private ChannelService channelService;

	@Autowired
	private CredentialService credentialService;

	@Autowired
	private CreditDetailService creditService;

	@Autowired
	private CustomerDiscountService customerDiscountService;

	@Autowired
	private CustomerReceivableService customerReceivableService;

	@Autowired
	private HolidayService holidayService;

	@Autowired
	private ImportedLocationService locationService;

	@Autowired
	private PricingTypeService pricingService;

	@Autowired
	private ReadOnlyService<Customer> readOnlyService;

	@Autowired
	private RoutingService routingService;

	@Autowired
	private SavingService<Customer> savingService;

	@Autowired
	private WeeklyVisitService visitService;

	@Value("${vendor.name}")
	private String vendorName;

	@Value("${vendor.barangay}")
	private String vendorBarangay;

	@Value("${vendor.city}")
	private String vendorCity;

	@Value("${vendor.province}")
	private String vendorProvince;

	@Value("#{'${branch.names}'.split(',')}")
	private List<String> branchNames;

	@Value("#{'${branch.barangays}'.split(',')}")
	private List<String> branchBarangays;

	@Value("#{'${branch.cities}'.split(',')}")
	private List<String> branchCities;

	@Value("#{'${branch.provinces}'.split(',')}")
	private List<String> branchProvinces;

	@Value("#{'${bank.names}'.split(',')}")
	private List<String> bankNames;

	@Value("${vendor.dis.go.live}")
	private String edmsGoLive;

	@Value("${vendor.id}")
	private String vendorId;

	private Customer vendor;

	@Override
	protected CustomerEntity entity(Customer c) {
		CustomerEntity e = findEntity(c);
		if (e == null)
			e = new CustomerEntity();
		if (e.getDeactivatedBy() == null && c.getDeactivatedBy() != null)
			return deactivate(e, c.getDeactivatedBy());
		if (e.getDeactivatedBy() != null && c.getDeactivatedBy() == null)
			return reactivate(e);
		e.setName(c.getName());
		e.setType(c.getType());
		e.setStreet(c.getStreet());
		e.setBarangay(toEntity(c.getBarangay()));
		e.setCity(toEntity(c.getCity()));
		e.setProvince(toEntity(c.getProvince()));
		e.setChannel(channelService.toEntity(c.getChannel()));
		e.setContactName(c.getContactName());
		e.setContactSurname(c.getContactSurname());
		e.setContactTitle(c.getContactTitle());
		e.setMobile(c.getMobile());
		e.setParent(toEntity(c.getParent()));
		e.setPrimaryPricingType(toEntity(c.getPrimaryPricingType()));
		e.setRouteHistory(routeHistory(e, c));
		e.setVisitFrequency(c.getVisitFrequency());
		e.setVisitSchedule(visitService.toEntities(c.getVisitSchedule()));
		if (!c.getCreditDetails().isEmpty())
			e.setCreditDetails(creditDetails(e, c));
		if (!c.getDiscounts().isEmpty())
			e.setCustomerDiscounts(customerDiscounts(e, c));
		return e;
	}

	private CustomerEntity reactivate(CustomerEntity e) {
		e.setDeactivatedBy(null);
		e.setDeactivatedOn(null);
		return e;
	}

	private LocationEntity toEntity(Location l) {
		return locationService.toEntity(l);
	}

	private PricingTypeEntity toEntity(PricingType p) {
		return pricingService.toEntity(p);
	}

	private List<RoutingEntity> routeHistory(CustomerEntity e, Customer c) {
		return routingService.toEntities(c.getRouteHistory(), e);
	}

	private List<CreditDetailEntity> creditDetails(CustomerEntity e, Customer c) {
		if (e.getCreditDetails().isEmpty())
			return creditService.toEntities(c.getCreditDetails());
		if (e.getCreditDetails().size() < c.getCreditDetails().size())
			return creditService.getNewAndOldCreditDetails(e, c);
		if (creditService.hasDecisionOnNewCreditDetailsBeenMade(e, c))
			return creditService.getUpdatedCreditDetailDecisions(e, c);
		return e.getCreditDetails();
	}

	private List<CustomerDiscountEntity> customerDiscounts(CustomerEntity e, Customer c) {
		if (e.getCustomerDiscounts().isEmpty())
			return customerDiscountService.toEntities(c.getDiscounts(), e);
		if (e.getCustomerDiscounts().size() < c.getDiscounts().size())
			return customerDiscountService.getNewAndOldCustomerDiscounts(e, c);
		if (customerDiscountService.hasDecisionOnNewCustomerDiscountsBeenMade(e, c))
			return customerDiscountService.updateCustomerDiscountDecisions(e, c);
		return e.getCustomerDiscounts();
	}

	@Override
	public List<Customer> findAllOnAction() {
		return findAllByType(EX_TRUCK);
	}

	@Override
	public Customer findEmployeeIdAndName(Long id) {
		CustomerEntity e = repository.findByDeactivatedOnNullAndTypeAndId(INTERNAL, id);
		return idAndNameOnly(e);
	}

	@Override
	public Customer findNoContactDetails() {
		List<CustomerEntity> l = repository.findByDeactivatedOnNullAndTypeAndContactNameNullAndCreditDetailsNotNull(OUTLET);
		if (l == null)
			l = repository.findByDeactivatedOnNullAndTypeAndContactNameNullAndCustomerDiscountsNotNull(OUTLET);
		return filterBySeller(l, username());
	}

	private Customer filterBySeller(List<CustomerEntity> l, String seller) {
		Optional<CustomerEntity> o = l.stream().filter(c -> seller.equals(c.getSeller())).findFirst();
		return o.isPresent() ? idAndNameOnly(o.get()) : null;
	}

	private String username() {
		return credentialService.username();
	}

	@Override
	public Customer findNoDesignation() {
		List<CustomerEntity> l = repository.findByDeactivatedOnNullAndTypeAndContactNameNotNullAndContactTitleNull(OUTLET);
		return filterBySeller(l, username());
	}

	@Override
	public Customer findNoMobileNo() {
		List<CustomerEntity> l = repository.findByDeactivatedOnNullAndTypeAndContactNameNotNullAndMobileNull(OUTLET);
		return filterBySeller(l, username());
	}

	@Override
	public Customer findNoStreetAddress() {
		List<CustomerEntity> l = repository.findByDeactivatedOnNullAndTypeAndStreetNull(OUTLET);
		return filterBySeller(l, username());
	}

	@Override
	public Customer findNoSurname() {
		List<CustomerEntity> l = repository.findByDeactivatedOnNullAndTypeAndContactNameNotNullAndContactSurnameNull(OUTLET);
		if (l == null) {
			l = repository.findByDeactivatedOnNullAndContactNameNotNullAndContactSurnameNotNull(OUTLET);
			l = l.stream().filter(u -> u.getContactName().equals(u.getContactSurname())).collect(Collectors.toList());
		}
		return filterBySeller(l, username());
	}

	@Override
	public Customer findIncorrectBarangayAddress() {
		List<CustomerEntity> l = repository.findByDeactivatedOnNullAndTypeAndBarangayTypeNot(OUTLET, BARANGAY);
		return filterBySeller(l, username());
	}

	@Override
	public Customer findIncorrectCityAddress() {
		List<CustomerEntity> l = repository.findByDeactivatedOnNullAndTypeAndCityTypeNot(OUTLET, CITY);
		return filterBySeller(l, username());
	}

	@Override
	public Customer findIncorrectProvincialAddress() {
		List<CustomerEntity> l = repository.findByDeactivatedOnNullAndTypeAndProvinceTypeNot(OUTLET, PROVINCE);
		return filterBySeller(l, username());
	}

	@Override
	public Customer findDifferentVisitFrequencyAndSchedule() {
		List<CustomerEntity> l = repository
				.findByDeactivatedOnNullAndTypeAndChannelVisitedTrueAndVisitFrequencyNotNullAndVisitScheduleNotNullOrderByVisitScheduleWeekNoAsc(OUTLET);
		l = l.stream().filter(u -> count(u.getVisitFrequency()) != count(u.getVisitSchedule())).collect(Collectors.toList());
		return filterBySeller(l, username());
	}

	private int count(VisitFrequency f) {
		switch (f) {
			case F1:
				return 1;
			case F2:
				return 2;
			case F4:
				return 4;
			case F8:
				return 8;
			case F12:
				return 12;
			default:
				return 24;
		}
	}

	private int count(List<WeeklyVisitEntity> l) {
		int i = 0;
		for (WeeklyVisitEntity v : l) {
			if (v.getSun() == true)
				++i;
			if (v.getMon() == true)
				++i;
			if (v.getTue() == true)
				++i;
			if (v.getWed() == true)
				++i;
			if (v.getThu() == true)
				++i;
			if (v.getFri() == true)
				++i;
			if (v.getSat() == true)
				++i;
		}
		return i;
	}

	@Override
	public Customer findDifferentWeeksOneAndFiveVisitSchedule() {
		List<CustomerEntity> l = repository
				.findByDeactivatedOnNullAndTypeAndChannelVisitedTrueAndVisitFrequencyNotNullAndVisitScheduleNotNullOrderByVisitScheduleWeekNoAsc(OUTLET);
		l = l.stream().filter(v -> !v.getVisitSchedule().get(0).equals(v.getVisitSchedule().get(4))).collect(Collectors.toList());
		return filterBySeller(l, username());
	}

	@Override
	public Customer findNoVisitSchedule() {
		List<CustomerEntity> l = repository.findByDeactivatedOnNullAndTypeAndChannelVisitedTrueAndVisitFrequencyNull(OUTLET);
		if (l == null)
			l = repository.findByDeactivatedOnNullAndTypeAndChannelVisitedTrueAndVisitScheduleNull(OUTLET);
		return filterBySeller(l, username());
	}

	@Override
	public List<Customer> findAllByScheduledRouteAndGoodCreditStanding(Long routeId) {
		List<Customer> l = new ArrayList<>( //
				repository.findByDeactivatedOnNullAndType(OUTLET).stream() //
						.map(c -> toModel(c)) //
						.filter(c -> isSelectedRouteOnSchedule(c, routeId)) //
						.map(c -> addBadCreditTagToCustomersWithOverdue(c)) //
						.sorted(comparing(Customer::getName)) //
						.collect(toList()));
		l.addAll(fiveBlankCustomers());
		return l;
	}

	private boolean isSelectedRouteOnSchedule(Customer c, Long routeId) {
		Route r = getRoute(c, LocalDate.now());
		if (r == null)
			return false;
		if (!r.getId().equals(routeId))
			return false;
		return isSelectedRouteOnSchedule(c, getScheduleDate(r));
	}

	private Route getRoute(Customer c, LocalDate date) {
		return c.getRouteHistory().stream() //
				.filter(p -> !p.getStartDate().isAfter(date)) //
				.max(comparing(Routing::getStartDate)) //
				.orElse(new Routing()).getRoute();
	}

	private boolean isSelectedRouteOnSchedule(Customer c, LocalDate date) {
		List<WeeklyVisit> visits = c.getVisitSchedule();
		if (visits == null || visits.isEmpty())
			return false;
		return isSelectedRouteOnSchedule(date.getDayOfWeek(), week(visits, date));
	}

	private boolean isSelectedRouteOnSchedule(DayOfWeek day, WeeklyVisit visit) {
		if (visit == null)
			return false;
		if (visit.isMon() && day == MONDAY)
			return true;
		if (visit.isTue() && day == TUESDAY)
			return true;
		if (visit.isWed() && day == WEDNESDAY)
			return true;
		if (visit.isThu() && day == THURSDAY)
			return true;
		if (visit.isFri() && day == FRIDAY)
			return true;
		if (visit.isSat() && day == SATURDAY)
			return true;
		return false;
	}

	private Customer addBadCreditTagToCustomersWithOverdue(Customer c) {
		if (doesCustomerHaveOverdues(c))
			c.setName(c.getName() + HAS_OVERDUES);
		return c;
	}

	private boolean doesCustomerHaveOverdues(Customer c) {
		try {
			return doesCustomerHaveOverdues(c.getId());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean doesCustomerHaveOverdues(Long id) throws Exception {
		CustomerReceivableReport ar = customerReceivableService.generateCustomerReceivableReport(id, 1, daysFromGoLive());
		BigDecimal unpaid = ar.getReceivables().stream().map(r -> r.getUnpaidValue()).reduce(ZERO, BigDecimal::add);
		return isPositive(unpaid);
	}

	private long daysFromGoLive() {
		return edmsGoLive().until(LocalDate.now(), DAYS);
	}

	private LocalDate edmsGoLive() {
		return toDate(edmsGoLive);
	}

	private List<Customer> fiveBlankCustomers() {
		List<Customer> l = new ArrayList<>();
		for (int i = 0; i < 5; i++)
			l.add(new Customer());
		return l;
	}

	private WeeklyVisit week(List<WeeklyVisit> visits, LocalDate date) {
		return visits.stream() //
				.filter(v -> v.getWeekNo() == DateTimeUtils.weekNo(date)) //
				.findFirst().orElse(null);
	}

	private LocalDate getScheduleDate(Route r) {
		LocalDate date = holidayService.nextWorkDay(LocalDate.now());
		if (r.getName().startsWith(PRE_SELL.toString()))
			date = holidayService.nextWorkDay(date);
		return date;
	}

	@Override
	public Customer getVendor() {
		if (vendor == null)
			vendor = findByKey(vendorId);
		return vendor;
	}

	@Override
	public List<Customer> listOutletIdsAndNames() {
		return findAllByType(OUTLET);
	}

	@Override
	protected Customer basicInfoOnly(CustomerEntity e) {
		Customer c = super.basicInfoOnly(e);
		if (c == null)
			return null;
		c.setStreet(e.getStreet());
		c.setBarangay(toLocation(e.getBarangay()));
		c.setCity(toLocation(e.getCity()));
		c.setProvince(toLocation(e.getProvince()));
		c.setChannel(channelService.toModel(e.getChannel()));
		c.setRouteHistory(routingService.toRoutings(e.getRouteHistory()));
		c.setDeactivatedBy(e.getDeactivatedBy());
		c.setDeactivatedOn(e.getDeactivatedOn());
		c.setVisitFrequency(e.getVisitFrequency());
		c.setVisitSchedule(visitService.toModels(e.getVisitSchedule()));
		return c;
	}

	private Location toLocation(LocationEntity e) {
		return locationService.toModel(e);
	}

	@Override
	protected Customer newCustomer(CustomerEntity e) {
		Customer c = super.newCustomer(e);
		c.setContactName(e.getContactName());
		c.setContactSurname(e.getContactSurname());
		c.setContactTitle(e.getContactTitle());
		c.setMobile(e.getMobile());
		c.setCreditDetails(creditService.toList(e.getCreditDetails()));
		c.setDiscounts(customerDiscountService.toModels(e.getCustomerDiscounts()));
		c.setPrimaryPricingType(pricingService.toModel(e.getPrimaryPricingType()));
		return c;
	}

	@Override
	public void cancelAllCustomerDiscountsIfMonthlyAverageIsLessthanRequired(BigDecimal noOfmonths, BigDecimal requiredQty) {
		List<BillableEntity> billings = billingService.findAllValidOutletBillingsBetweenDates(start(noOfmonths), now());
		customerDiscountService.cancelDiscountsOfOutletsWithAverageMonthlySalesBelowRequiredQty(noOfmonths, requiredQty, billings);
	}

	private LocalDate start(BigDecimal noOfmonths) {
		return now().minusMonths(noOfmonths.longValue());
	}

	@Override
	@Transactional
	public void importAll() throws Exception {
		createVendor();
		createBranches();
		importonAction();
		createBanks();
		importOutlets();
	}

	private void createVendor() {
		CustomerEntity c = createCustomer(vendorName, VENDOR);
		c.setBarangay(locationService.getByName(vendorBarangay));
		c.setCity(locationService.getByName(vendorCity));
		c.setProvince(locationService.getByName(vendorProvince));
		repository.save(c);
	}

	private CustomerEntity createCustomer(String name, PartnerType t) {
		CustomerEntity c = new CustomerEntity();
		c.setName(name);
		c.setType(t);
		return c;
	}

	private void createBranches() {
		for (int i = 0; i < branchNames.size(); i++)
			createBranch(i);
	}

	private void createBranch(int i) {
		CustomerEntity c = createCustomer(branchNames.get(i), INTERNAL);
		c.setBarangay(locationService.getByName(branchBarangays.get(i)));
		c.setCity(locationService.getByName(branchCities.get(i)));
		c.setProvince(locationService.getByName(branchProvinces.get(i)));
		repository.save(c);
	}

	private void importonAction() throws Exception {
		importCustomers("/onAction");
	}

	private void importCustomers(String endPt) throws Exception {
		List<Customer> l = readOnlyService.module(CUSTOMER).getList(endPt);
		l.forEach(c -> repository.save(toEntity(c)));
	}

	private void createBanks() {
		bankNames.forEach(b -> createBank(b));
	}

	private void createBank(String name) {
		CustomerEntity c = createCustomer(name, FINANCIAL);
		repository.save(c);
	}

	private void importOutlets() throws Exception {
		importCustomers("");
	}

	@Override
	@Transactional
	public Customer save(Customer c) {
		try {
			c = super.save(c);
			return isNotForEdmsPosting(c) ? c : saveToEdms(c);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private boolean isNotForEdmsPosting(Customer c) {
		return c.getParent() != null || c.getType() != OUTLET;
	}

	@Override
	public Customer saveToEdms(Customer c) throws Exception {
		savingService.module(CUSTOMER).save(c);
		return c;
	}

	@Override
	public void deactivateAllNonBuyingOutletsAfterThePrescribedPeriod(BigDecimal noOfMonths) {
		List<BillableEntity> billings = billingService.findAllValidOutletBillingsBetweenDates(start(noOfMonths), now());
		List<Long> inactiveOutletIds = inactiveOutletIds(billings, noOfMonths);
		if (!inactiveOutletIds.isEmpty())
			deactivateAllNonBuyingOutlets(inactiveOutletIds);
	}

	private List<Long> inactiveOutletIds(List<BillableEntity> billings, BigDecimal noOfMonths) {
		List<Long> outletIds = listOutletsCreatedBefore(noOfMonths).stream() //
				.map(c -> c.getId()) //
				.collect(toList());
		List<Long> activeOutletIds = billings.stream() //
				.map(b -> b.getCustomer().getId()) //
				.distinct().collect(toList());
		outletIds.removeAll(activeOutletIds);
		return outletIds;
	}

	private List<CustomerEntity> listOutletsCreatedBefore(BigDecimal noOfMonths) {
		LocalDate start = start(noOfMonths);
		ZonedDateTime creation = endOfDay(start);
		return repository.findByDeactivatedOnNullAndTypeAndCreatedOnLessThan(OUTLET, creation);
	}

	private void deactivateAllNonBuyingOutlets(List<Long> inactiveOutletIds) {
		Iterable<CustomerEntity> inactiveOutlets = repository.findAll(inactiveOutletIds);
		repository.save( //
				stream(inactiveOutlets.spliterator(), false) //
						.map(c -> deactivate(c, "INACTIVITY")) //
						.collect(toList()));
	}
}