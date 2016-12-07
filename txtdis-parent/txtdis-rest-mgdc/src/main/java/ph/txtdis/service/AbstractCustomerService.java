package ph.txtdis.service;

import static java.math.BigDecimal.ZERO;
import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.DayOfWeek.THURSDAY;
import static java.time.DayOfWeek.TUESDAY;
import static java.time.DayOfWeek.WEDNESDAY;
import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.dto.PartnerType.EX_TRUCK;
import static ph.txtdis.dto.PartnerType.FINANCIAL;
import static ph.txtdis.dto.PartnerType.OUTLET;
import static ph.txtdis.type.LocationType.BARANGAY;
import static ph.txtdis.type.LocationType.CITY;
import static ph.txtdis.type.LocationType.PROVINCE;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import ph.txtdis.domain.CustomerEntity;
import ph.txtdis.domain.WeeklyVisitEntity;
import ph.txtdis.dto.Customer;
import ph.txtdis.dto.CustomerReceivableReport;
import ph.txtdis.dto.PartnerType;
import ph.txtdis.dto.Route;
import ph.txtdis.dto.WeeklyVisit;
import ph.txtdis.repository.CustomerRepository;
import ph.txtdis.type.VisitFrequency;
import ph.txtdis.util.DateTimeUtils;
import ph.txtdis.util.NumberUtils;

public abstract class AbstractCustomerService
		extends AbstractSpunService<CustomerRepository, CustomerEntity, Customer, Long>
		implements CustomerService, SpunService<Customer, Long> {

	private static Logger logger = getLogger(AbstractCustomerService.class);

	@Autowired
	private CredentialService credentialService;

	@Autowired
	private CreditDetailService creditService;

	@Autowired
	private CustomerDiscountService customerDiscountService;

	@Autowired
	private CustomerReceivableService customerReceivableService;

	@Autowired
	private CustomerVolumeDiscountService volumeDiscountService;

	@Autowired
	private CustomerVolumePromoService volumePromoService;

	@Autowired
	private PricingTypeService pricingService;

	@Autowired
	private PrimaryChannelService channelService;

	@Autowired
	private PrimaryLocationService locationService;

	@Autowired
	private RoutingService routingService;

	@Autowired
	private WeeklyVisitService visitService;

	@Value("${vendor.id}")
	private String vendorId;

	private Customer vendor;

	@Override
	public Customer toBank(String id) {
		return find(id);
	}

	private Customer find(String id) {
		return toDTO(findEntity(id));
	}

	private CustomerEntity findEntity(String id) {
		return repository.findOne(Long.valueOf(id));
	}

	@Override
	public CustomerEntity toBankEntity(String id) {
		return findEntity(id);
	}

	@Override
	public Customer getVendor() {
		if (vendor == null)
			vendor = find(vendorId);
		return vendor;
	}

	@Override
	public List<Customer> findBanks() {
		return findByType(FINANCIAL);
	}

	private List<Customer> findByType(PartnerType type) {
		List<CustomerEntity> l = repository.findByDeactivatedOnNullAndTypeOrderByNameAsc(type);
		return l.stream().map(c -> idAndNameOnly(c)).collect(Collectors.toList());
	}

	@Override
	public List<Customer> findExTrucks() {
		return findByType(EX_TRUCK);
	}

	@Override
	public Customer findByName(String name) {
		return toDTO(findEntityByName(name));
	}

	private CustomerEntity findEntityByName(String name) {
		return repository.findByNameIgnoreCase(name);
	}

	@Override
	public Customer find(Long id) {
		CustomerEntity e = repository.findOne(id);
		return toDTO(e);
	}

	@Override
	public Customer findByVendorId(Long id) {
		CustomerEntity e = repository.findByVendorId(id);
		return toDTO(e);
	}

	@Override
	public Customer findNoContactDetails() {
		List<CustomerEntity> l = repository
				.findByDeactivatedOnNullAndTypeAndContactNameNullAndCreditDetailsNotNull(OUTLET);
		if (l == null)
			l = repository.findByDeactivatedOnNullAndTypeAndContactNameNullAndCustomerDiscountsNotNull(OUTLET);
		return filterBySeller(l, credentialService.username());
	}

	@Override
	public Customer findNoDesignation() {
		List<CustomerEntity> l = repository
				.findByDeactivatedOnNullAndTypeAndContactNameNotNullAndContactTitleNull(OUTLET);
		return filterBySeller(l, credentialService.username());
	}

	@Override
	public Customer findNoMobileNo() {
		List<CustomerEntity> l = repository.findByDeactivatedOnNullAndTypeAndContactNameNotNullAndMobileNull(OUTLET);
		return filterBySeller(l, credentialService.username());
	}

	@Override
	public Customer findNoStreetAddress() {
		List<CustomerEntity> l = repository.findByDeactivatedOnNullAndTypeAndStreetNull(OUTLET);
		return filterBySeller(l, credentialService.username());
	}

	@Override
	public Customer findNoSurname() {
		List<CustomerEntity> l = repository
				.findByDeactivatedOnNullAndTypeAndContactNameNotNullAndContactSurnameNull(OUTLET);
		if (l == null) {
			l = repository.findByDeactivatedOnNullAndContactNameNotNullAndContactSurnameNotNull(OUTLET);
			l = l.stream().filter(u -> u.getContactName().equals(u.getContactSurname())).collect(Collectors.toList());
		}
		return filterBySeller(l, credentialService.username());
	}

	@Override
	public Customer findNotCorrectBarangayAddress() {
		List<CustomerEntity> l = repository.findByDeactivatedOnNullAndTypeAndBarangayTypeNot(OUTLET, BARANGAY);
		return filterBySeller(l, credentialService.username());
	}

	@Override
	public Customer findNotCorrectCityAddress() {
		List<CustomerEntity> l = repository.findByDeactivatedOnNullAndTypeAndCityTypeNot(OUTLET, CITY);
		return filterBySeller(l, credentialService.username());
	}

	@Override
	public Customer findNotCorrectProvincialAddress() {
		List<CustomerEntity> l = repository.findByDeactivatedOnNullAndTypeAndProvinceTypeNot(OUTLET, PROVINCE);
		return filterBySeller(l, credentialService.username());
	}

	@Override
	public Customer findNotTheSameVisitFrequencyAndSchedule() {
		List<CustomerEntity> l = repository
				.findByDeactivatedOnNullAndTypeAndChannelVisitedTrueAndVisitFrequencyNotNullAndVisitScheduleNotNullOrderByVisitScheduleWeekNoAsc(
						OUTLET);
		l = l.stream().filter(u -> count(u.getVisitFrequency()) != count(u.getVisitSchedule()))
				.collect(Collectors.toList());
		return filterBySeller(l, credentialService.username());
	}

	@Override
	public Customer findNotTheSameWeeksOneAndFiveVisitSchedule() {
		List<CustomerEntity> l = repository
				.findByDeactivatedOnNullAndTypeAndChannelVisitedTrueAndVisitFrequencyNotNullAndVisitScheduleNotNullOrderByVisitScheduleWeekNoAsc(
						OUTLET);
		l = l.stream().filter(v -> !v.getVisitSchedule().get(0).equals(v.getVisitSchedule().get(4)))
				.collect(Collectors.toList());
		return filterBySeller(l, credentialService.username());
	}

	@Override
	public Customer findNoVisitSchedule() {
		List<CustomerEntity> l = repository
				.findByDeactivatedOnNullAndTypeAndChannelVisitedTrueAndVisitFrequencyNull(OUTLET);
		if (l == null)
			l = repository.findByDeactivatedOnNullAndTypeAndChannelVisitedTrueAndVisitScheduleNull(OUTLET);
		return filterBySeller(l, credentialService.username());
	}

	@Override
	public List<Customer> list() {
		Iterable<CustomerEntity> l = repository.findAll();
		return l == null ? null
				: StreamSupport.stream(l.spliterator(), false).map(c -> basicInfoOnly(c)).collect(Collectors.toList());
	}

	@Override
	public List<Customer> listByScheduledRouteAndGoodCreditStanding(Long routeId) {
		List<CustomerEntity> l = repository.findByDeactivatedOnNullAndType(PartnerType.OUTLET);
		List<Customer> customers = l.stream() //
				.map(c -> toDTO(c)) //
				.filter(c -> isSelectedRouteOnScheduleAndIsCustomerOfGoodStanding(c, routeId)) //
				.sorted((a, b) -> a.getName().compareTo(b.getName())) //
				.collect(Collectors.toList());
		customers = new ArrayList<>(customers);
		customers.addAll(fiveBlankCustomers());
		return customers;
	}

	private List<Customer> fiveBlankCustomers() {
		List<Customer> l = new ArrayList<>();
		for (int i = 0; i < 5; i++)
			l.add(new Customer());
		return l;
	}

	private boolean isSelectedRouteOnScheduleAndIsCustomerOfGoodStanding(Customer c, Long routeId) {
		if (doesCustomerHaveOverdues(c))
			return false;
		return isSelectedRouteOnSchedule(c, routeId);
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
		return NumberUtils.isPositive(unpaid);
	}

	private long daysFromGoLive() {
		return goLiveDate().until(LocalDate.now(), ChronoUnit.DAYS);
	}

	public abstract LocalDate goLiveDate();

	private boolean isSelectedRouteOnSchedule(Customer c, Long routeId) {
		Route r = c.getRoute();
		if (r == null)
			return false;
		if (!r.getId().equals(routeId))
			return false;
		return isSelectedRouteOnSchedule(c, getScheduleDate(r));
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

	private LocalDate getScheduleDate(Route r) {
		LocalDate date = nextWorkDay(LocalDate.now());
		if (r.getName().startsWith("PRE-SELL"))
			date = nextWorkDay(date);
		return date;
	}

	private LocalDate nextWorkDay(LocalDate date) {
		LocalDate tomorrow = date.plusDays(1L);
		if (tomorrow.getDayOfWeek() == SUNDAY)
			return tomorrow.plusDays(1L);
		return tomorrow;
	}

	private WeeklyVisit week(List<WeeklyVisit> visits, LocalDate date) {
		return visits.stream() //
				.filter(v -> v.getWeekNo() == DateTimeUtils.weekNo(date)) //
				.findFirst().orElse(null);
	}

	@Override
	public List<Customer> searchByName(String name) {
		List<CustomerEntity> l = repository.findByNameContaining(name);
		return l == null ? null : l.stream().map(c -> basicInfoOnly(c)).collect(Collectors.toList());
	}

	private Customer idAndNameOnly(CustomerEntity c) {
		if (c == null)
			return null;
		Customer n = new Customer();
		n.setId(c.getId());
		n.setName(c.getName());
		return n;
	}

	private Customer basicInfoOnly(CustomerEntity e) {
		if (e == null)
			return null;
		Customer c = idAndNameOnly(e);
		c.setRouteHistory(routingService.toRoutings(e.getRouteHistory()));
		c.setStreet(e.getStreet());
		c.setBarangay(locationService.toDTO(e.getBarangay()));
		c.setCity(locationService.toDTO(e.getCity()));
		c.setProvince(locationService.toDTO(e.getProvince()));
		return c;
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

	private Customer filterBySeller(List<CustomerEntity> l, String seller) {
		Optional<CustomerEntity> o = l.stream().filter(c -> seller.equals(c.getSeller())).findFirst();
		return o.isPresent() ? idAndNameOnly(o.get()) : null;
	}

	@Override
	protected Customer toDTO(CustomerEntity e) {
		return e == null ? null : newCustomer(e);
	}

	private Customer newCustomer(CustomerEntity e) {
		Customer c = basicInfoOnly(e);
		c.setAlternatePricingType(pricingService.toDTO(e.getAlternatePricingType()));
		c.setChannel(channelService.toDTO(e.getChannel()));
		c.setContactName(e.getContactName());
		c.setContactSurname(e.getContactSurname());
		c.setContactTitle(e.getContactTitle());
		c.setCreatedBy(e.getCreatedBy());
		c.setCreatedOn(e.getCreatedOn());
		c.setCreditDetails(creditService.toList(e.getCreditDetails()));
		c.setCustomerDiscounts(customerDiscountService.toList(e.getCustomerDiscounts()));
		c.setVolumeDiscounts(volumeDiscountService.toList(e.getVolumeDiscounts()));
		c.setVolumePromos(volumePromoService.toList(e.getVolumePromos()));
		c.setDeactivatedBy(e.getDeactivatedBy());
		c.setDeactivatedOn(e.getDeactivatedOn());
		c.setLastModifiedBy(e.getLastModifiedBy());
		c.setLastModifiedOn(e.getLastModifiedOn());
		c.setMobile(e.getMobile());
		c.setParent(toDTO(e.getParent()));
		c.setPrimaryPricingType(pricingService.toDTO(e.getPrimaryPricingType()));
		c.setType(e.getType());
		c.setVendorId(e.getVendorId());
		c.setVisitFrequency(e.getVisitFrequency());
		c.setVisitSchedule(visitService.toList(e.getVisitSchedule()));
		logger.info("\n    Customer: " + c.getType() + " - " + c.getName());
		return c;
	}

	@Override
	public CustomerEntity toEntity(Customer c) {
		return c == null ? null : getEntity(c);
	}

	private CustomerEntity getEntity(Customer c) {
		CustomerEntity e = findSavedEntity(c);
		if (e == null)
			e = new CustomerEntity();
		if (e.getDeactivatedBy() == null && c.getDeactivatedBy() != null)
			return deactivate(e, c);
		e.setName(c.getName());
		e.setStreet(c.getStreet());
		e.setBarangay(locationService.toEntity(c.getBarangay()));
		e.setCity(locationService.toEntity(c.getCity()));
		e.setProvince(locationService.toEntity(c.getProvince()));
		e.setAlternatePricingType(pricingService.toEntity(c.getAlternatePricingType()));
		e.setChannel(channelService.toEntity(c.getChannel()));
		e.setContactName(c.getContactName());
		e.setContactSurname(c.getContactSurname());
		e.setContactTitle(c.getContactTitle());
		e.setMobile(c.getMobile());
		e.setParent(toEntity(c.getParent()));
		e.setPrimaryPricingType(pricingService.toEntity(c.getPrimaryPricingType()));
		e.setRouteHistory(routingService.toEntities(c.getRouteHistory()));
		e.setType(c.getType());
		e.setVendorId(c.getVendorId());
		e.setVisitFrequency(c.getVisitFrequency());
		e.setVisitSchedule(visitService.toEntities(c.getVisitSchedule()));
		e.setCreditDetails(creditService.toEntities(c.getCreditDetails()));
		e.setCustomerDiscounts(customerDiscountService.toEntities(c.getCustomerDiscounts()));
		e.setVolumeDiscounts(volumeDiscountService.toEntities(c.getVolumeDiscounts()));
		e.setVolumePromos(volumePromoService.toEntities(c.getVolumePromos()));
		logger.info("\n    CustomerEntity: " + e.getType() + " - " + e.getName());
		return e;
	}

	private CustomerEntity findSavedEntity(Customer c) {
		Long id = c.getId();
		if (id != null)
			return findEntity(id);
		return findEntityByName(c.getName());
	}

	private CustomerEntity deactivate(CustomerEntity e, Customer c) {
		e.setDeactivatedBy(c.getDeactivatedBy());
		e.setDeactivatedOn(ZonedDateTime.now());
		return e;
	}
}
