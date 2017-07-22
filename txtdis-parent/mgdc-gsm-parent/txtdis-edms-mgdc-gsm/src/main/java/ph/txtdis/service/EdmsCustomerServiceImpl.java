package ph.txtdis.service;

import static java.math.BigDecimal.ZERO;
import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.DayOfWeek.THURSDAY;
import static java.time.DayOfWeek.TUESDAY;
import static java.time.DayOfWeek.WEDNESDAY;
import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;
import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.apache.commons.lang3.text.WordUtils.capitalizeFully;
import static ph.txtdis.service.EdmsLocationServiceImpl.MANILA;
import static ph.txtdis.service.EdmsLocationServiceImpl.MANILA_DISTRICTS;
import static ph.txtdis.type.PartnerType.OUTLET;
import static ph.txtdis.type.PriceType.DEALER;
import static ph.txtdis.type.VisitFrequency.F4;
import static ph.txtdis.util.Code.ACTIVE;
import static ph.txtdis.util.Code.CASH_AND_CREDIT;
import static ph.txtdis.util.Code.CASH_ONLY;
import static ph.txtdis.util.Code.COD;
import static ph.txtdis.util.Code.CUSTOMER_PREFIX;
import static ph.txtdis.util.Code.EDMS;
import static ph.txtdis.util.Code.NO;
import static ph.txtdis.util.Code.VOID;
import static ph.txtdis.util.Code.YES;
import static ph.txtdis.util.DateTimeUtils.toTimestampText;
import static ph.txtdis.util.TextUtils.blankIfNull;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.EdmsAgingReceivable;
import ph.txtdis.domain.EdmsCreditDetail;
import ph.txtdis.domain.EdmsCustomer;
import ph.txtdis.domain.EdmsCustomerBank;
import ph.txtdis.domain.EdmsInvoice;
import ph.txtdis.domain.EdmsOutletType;
import ph.txtdis.domain.EdmsWeeklyVisit;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.CreditDetail;
import ph.txtdis.dto.Location;
import ph.txtdis.dto.PricingType;
import ph.txtdis.dto.Route;
import ph.txtdis.dto.Routing;
import ph.txtdis.dto.WeeklyVisit;
import ph.txtdis.mgdc.gsm.dto.Channel;
import ph.txtdis.mgdc.gsm.dto.Customer;
import ph.txtdis.repository.EdmsAgingReceivableRepository;
import ph.txtdis.repository.EdmsChannelRepository;
import ph.txtdis.repository.EdmsCreditDetailRepository;
import ph.txtdis.repository.EdmsCustomerBankRepository;
import ph.txtdis.repository.EdmsCustomerRepository;
import ph.txtdis.repository.EdmsWeeklyVisitRepository;
import ph.txtdis.type.PartnerType;
import ph.txtdis.type.VisitFrequency;
import ph.txtdis.util.Code;

@Service("customerService")
public class EdmsCustomerServiceImpl //
		implements EdmsCustomerService {

	@Autowired
	private EdmsAgingReceivableRepository agingRepository;

	@Autowired
	private EdmsChannelRepository channelRepository;

	@Autowired
	private EdmsCreditDetailRepository creditDetailRepository;

	@Autowired
	private EdmsCustomerBankRepository customerBankRepository;

	@Autowired
	private EdmsCustomerRepository customerRepository;

	@Autowired
	private EdmsWeeklyVisitRepository weeklyVisitRepository;

	@Autowired
	private AutoNumberService autoNumberService;

	@Autowired
	private EdmsChannelService channelService;

	@Autowired
	private EdmsService edmsService;

	@Autowired
	private EdmsLocationService locationService;

	@Autowired
	private EdmsRouteService routeService;

	@Autowired
	private EdmsSellerService sellerService;

	@Autowired
	private EdmsVendorService vendorService;

	@Value("${client.name}")
	private String clientName;

	@Value("${client.street}")
	private String street;

	@Value("${client.barangay}")
	private String barangay;

	@Value("${client.city}")
	private String city;

	@Value("${client.province}")
	private String province;

	@Value("${client.user}")
	private String username;

	@Value("${prefix.customer}")
	private String prefix;

	@Override
	public Customer save(Customer c) {
		saveCustomerAndAutoNo(c);
		saveCreditDetail(c);
		return c;
	}

	private void saveCustomerAndAutoNo(Customer c) {
		EdmsCustomer e = customerRepository.findByCodeIgnoreCase(code(c));
		if (e == null)
			e = createCustomerAndSaveAutoNo(c);
		e = updateCustomer(e, c);
		saveVisitSchedule(e, c);
	}

	private String code(Customer c) {
		return prefix + leftPad(c.getId().toString(), 8, "0");
	}

	private EdmsCustomer createCustomerAndSaveAutoNo(Customer c) {
		saveAutoNo(c);
		saveCustomerBank(c);
		saveCustomerAR(c);
		return saveCustomer(c);
	}

	private void saveAutoNo(Customer c) {
		autoNumberService.saveAutoNo( //
				CUSTOMER_PREFIX, //
				c.getId().toString());
	}

	private void saveCustomerBank(Customer c) {
		EdmsCustomerBank e = new EdmsCustomerBank();
		e.setCustomerCode(code(c));
		customerBankRepository.save(e);
	}

	private void saveCustomerAR(Customer c) {
		EdmsAgingReceivable e = new EdmsAgingReceivable();
		e.setCustomerCode(code(c));
		e.setAsOfDate(c.getCreatedOn().toLocalDate().toString());
		agingRepository.save(e);
	}

	private EdmsCustomer saveCustomer(Customer c) {
		EdmsCustomer e = new EdmsCustomer();
		e.setExclusivity(Code.FE);
		e.setSupervisor(vendorService.getSupervisor());
		e.setDistrictCode(vendorService.getDistrictCode());
		e.setTerritoryCode(vendorService.getTerritoryCode());
		e.setAreaCode(vendorService.getAreaCode());
		e.setCreatedBy(username);
		e.setCreatedOn(toTimestampText(c.getCreatedOn()));
		return e;
	}

	private EdmsCustomer updateCustomer(EdmsCustomer e, Customer c) {
		e.setCode(code(c));
		e.setName(name(c));
		e.setBusinessName(e.getName());
		e.setSellerCode(route(c));
		e.setChannel(channel(c));
		e.setStatus(status(c));
		e.setVisitFrequency(visitFrequency(c));
		e.setVisitDay(visitDay(c));
		e.setStreet(street(c));
		e.setBarangay(barangay(c));
		e.setCity(city(c));
		e.setProvince(province(c));
		e.setOwner(owner(c));
		e.setPhoneNo(phoneNo(c));
		return customerRepository.save(e);
	}

	private String name(Customer c) {
		String name = c.getName();
		return name == null ? null : capitalizeFully(name, ' ', '(');
	}

	private String route(Customer c) {
		Route route = c.getRoute();
		return route == null ? null : capitalizeFully(route.getName(), ' ', '-');
	}

	private String street(Customer c) {
		String street = c.getStreet();
		return street == null ? null : capitalizeFully(street, ' ', '.');
	}

	private String channel(Customer c) {
		Channel channel = c.getChannel();
		EdmsOutletType outlet = null;
		if (channel != null)
			outlet = channelRepository.findByNameContainingIgnoreCase(channel.getName());
		return outlet == null ? "SSS - Sari-Sari Small" : outlet.getName();
	}

	private String status(Customer c) {
		return c.getDeactivatedOn() == null ? ACTIVE : VOID;
	}

	private String visitFrequency(Customer c) {
		VisitFrequency f = c.getVisitFrequency();
		return f == null ? "" : f.toString();
	}

	private String visitDay(Customer c) {
		List<WeeklyVisit> l = c.getVisitSchedule();
		return l == null || l.isEmpty() ? day(SUNDAY) : visitDay(l);
	}

	private String day(DayOfWeek d) {
		return capitalizeFully(d.toString());
	}

	private String visitDay(List<WeeklyVisit> l) {
		String day = visitDay(l.get(0));
		if (day == null)
			day = visitDay(l.get(1));
		if (day == null)
			day = day(SUNDAY);
		return day;
	}

	private String visitDay(WeeklyVisit v) {
		if (v.isMon())
			return day(MONDAY);
		if (v.isTue())
			return day(TUESDAY);
		if (v.isWed())
			return day(WEDNESDAY);
		if (v.isThu())
			return day(THURSDAY);
		if (v.isFri())
			return day(FRIDAY);
		if (v.isSat())
			return day(SATURDAY);
		return null;
	}

	private String phoneNo(Customer c) {
		return capitalizeFully(blankIfNull(c.getMobile()));
	}

	private String barangay(Customer c) {
		return locationService.getBarangay(c);
	}

	private String city(Customer c) {
		return locationService.getCity(c);
	}

	public String province(Customer c) {
		return locationService.getProvince(c);
	}

	private String owner(Customer c) {
		return capitalizeFully(blankIfNull(c.getContactName()));
	}

	private void saveVisitSchedule(EdmsCustomer ec, Customer c) {
		EdmsWeeklyVisit ev = new EdmsWeeklyVisit();
		ev.setCode(ec.getCode());
		ev.setFrequency(ec.getVisitFrequency());
		ev = weeklySchedule(ev, ec, c);
		weeklyVisitRepository.save(ev);
	}

	private EdmsWeeklyVisit weeklySchedule(EdmsWeeklyVisit ev, EdmsCustomer ec, Customer c) {
		List<WeeklyVisit> l = c.getVisitSchedule();
		DayOfWeek day = DayOfWeek.valueOf(ec.getVisitDay().toUpperCase());
		if (l == null)
			ev = week1schedule(ev, day);
		else
			for (WeeklyVisit v : l)
				if (v.getWeekNo() == 1)
					ev = week1schedule(ev, day);
				else if (v.getWeekNo() == 2)
					ev = week2schedule(ev, day);
				else if (v.getWeekNo() == 3)
					ev = week3schedule(ev, day);
				else if (v.getWeekNo() == 4)
					ev = week4schedule(ev, day);
		return ev;
	}

	private EdmsWeeklyVisit week1schedule(EdmsWeeklyVisit v, DayOfWeek day) {
		if (day == SUNDAY)
			v.setWeek1sun(nextWeek1SundaySequence());
		else if (day == MONDAY)
			v.setWeek1mon(nextWeek1MondaySequence());
		else if (day == TUESDAY)
			v.setWeek1tue(nextWeek1TuesdaySequence());
		else if (day == WEDNESDAY)
			v.setWeek1wed(nextWeek1WednesdaySequence());
		else if (day == THURSDAY)
			v.setWeek1thu(nextWeek1ThursdaySequence());
		else if (day == FRIDAY)
			v.setWeek1fri(nextWeek1FridaySequence());
		else if (day == SATURDAY)
			v.setWeek1sat(nextWeek1SaturdaySequence());
		return v;
	}

	private String nextWeek1SundaySequence() {
		EdmsWeeklyVisit v = weeklyVisitRepository.findFirstByWeek1sunNotOrderByIdDesc("");
		return v == null ? "1" : increment(v.getWeek1sun());
	}

	private String increment(String sequence) {
		return String.valueOf(Integer.valueOf(sequence) + 1);
	}

	private String nextWeek1MondaySequence() {
		EdmsWeeklyVisit v = weeklyVisitRepository.findFirstByWeek1monNotOrderByIdDesc("");
		return v == null ? "1" : increment(v.getWeek1mon());
	}

	private String nextWeek1TuesdaySequence() {
		EdmsWeeklyVisit v = weeklyVisitRepository.findFirstByWeek1tueNotOrderByIdDesc("");
		return v == null ? "1" : increment(v.getWeek1tue());
	}

	private String nextWeek1WednesdaySequence() {
		EdmsWeeklyVisit v = weeklyVisitRepository.findFirstByWeek1wedNotOrderByIdDesc("");
		return v == null ? "1" : increment(v.getWeek1wed());
	}

	private String nextWeek1ThursdaySequence() {
		EdmsWeeklyVisit v = weeklyVisitRepository.findFirstByWeek1thuNotOrderByIdDesc("");
		return v == null ? "1" : increment(v.getWeek1thu());
	}

	private String nextWeek1FridaySequence() {
		EdmsWeeklyVisit v = weeklyVisitRepository.findFirstByWeek1friNotOrderByIdDesc("");
		return v == null ? "1" : increment(v.getWeek1fri());
	}

	private String nextWeek1SaturdaySequence() {
		EdmsWeeklyVisit v = weeklyVisitRepository.findFirstByWeek1satNotOrderByIdDesc("");
		return v == null ? "1" : increment(v.getWeek1sat());
	}

	private EdmsWeeklyVisit week2schedule(EdmsWeeklyVisit v, DayOfWeek day) {
		if (day == SUNDAY)
			v.setWeek2sun(nextWeek2SundaySequence());
		else if (day == MONDAY)
			v.setWeek2mon(nextWeek2MondaySequence());
		else if (day == TUESDAY)
			v.setWeek2tue(nextWeek2TuesdaySequence());
		else if (day == WEDNESDAY)
			v.setWeek2wed(nextWeek2WednesdaySequence());
		else if (day == THURSDAY)
			v.setWeek2thu(nextWeek2ThursdaySequence());
		else if (day == FRIDAY)
			v.setWeek2fri(nextWeek2FridaySequence());
		else if (day == SATURDAY)
			v.setWeek2sat(nextWeek2SaturdaySequence());
		return v;
	}

	private String nextWeek2SundaySequence() {
		EdmsWeeklyVisit v = weeklyVisitRepository.findFirstByWeek2sunNotOrderByIdDesc("");
		return v == null ? "1" : increment(v.getWeek2sun());
	}

	private String nextWeek2MondaySequence() {
		EdmsWeeklyVisit v = weeklyVisitRepository.findFirstByWeek2monNotOrderByIdDesc("");
		return v == null ? "1" : increment(v.getWeek2mon());
	}

	private String nextWeek2TuesdaySequence() {
		EdmsWeeklyVisit v = weeklyVisitRepository.findFirstByWeek2tueNotOrderByIdDesc("");
		return v == null ? "1" : increment(v.getWeek2tue());
	}

	private String nextWeek2WednesdaySequence() {
		EdmsWeeklyVisit v = weeklyVisitRepository.findFirstByWeek2wedNotOrderByIdDesc("");
		return v == null ? "1" : increment(v.getWeek2wed());
	}

	private String nextWeek2ThursdaySequence() {
		EdmsWeeklyVisit v = weeklyVisitRepository.findFirstByWeek2thuNotOrderByIdDesc("");
		return v == null ? "1" : increment(v.getWeek2thu());
	}

	private String nextWeek2FridaySequence() {
		EdmsWeeklyVisit v = weeklyVisitRepository.findFirstByWeek2friNotOrderByIdDesc("");
		return v == null ? "1" : increment(v.getWeek2fri());
	}

	private String nextWeek2SaturdaySequence() {
		EdmsWeeklyVisit v = weeklyVisitRepository.findFirstByWeek2satNotOrderByIdDesc("");
		return v == null ? "1" : increment(v.getWeek2sat());
	}

	private EdmsWeeklyVisit week3schedule(EdmsWeeklyVisit v, DayOfWeek day) {
		if (day == SUNDAY)
			v.setWeek3sun(nextWeek3SundaySequence());
		else if (day == MONDAY)
			v.setWeek3mon(nextWeek3MondaySequence());
		else if (day == TUESDAY)
			v.setWeek3tue(nextWeek3TuesdaySequence());
		else if (day == WEDNESDAY)
			v.setWeek3wed(nextWeek3WednesdaySequence());
		else if (day == THURSDAY)
			v.setWeek3thu(nextWeek3ThursdaySequence());
		else if (day == FRIDAY)
			v.setWeek3fri(nextWeek3FridaySequence());
		else if (day == SATURDAY)
			v.setWeek3sat(nextWeek3SaturdaySequence());
		return v;
	}

	private String nextWeek3SundaySequence() {
		EdmsWeeklyVisit v = weeklyVisitRepository.findFirstByWeek3sunNotOrderByIdDesc("");
		return v == null ? "1" : increment(v.getWeek3sun());
	}

	private String nextWeek3MondaySequence() {
		EdmsWeeklyVisit v = weeklyVisitRepository.findFirstByWeek3monNotOrderByIdDesc("");
		return v == null ? "1" : increment(v.getWeek3mon());
	}

	private String nextWeek3TuesdaySequence() {
		EdmsWeeklyVisit v = weeklyVisitRepository.findFirstByWeek3tueNotOrderByIdDesc("");
		return v == null ? "1" : increment(v.getWeek3tue());
	}

	private String nextWeek3WednesdaySequence() {
		EdmsWeeklyVisit v = weeklyVisitRepository.findFirstByWeek3wedNotOrderByIdDesc("");
		return v == null ? "1" : increment(v.getWeek3wed());
	}

	private String nextWeek3ThursdaySequence() {
		EdmsWeeklyVisit v = weeklyVisitRepository.findFirstByWeek3thuNotOrderByIdDesc("");
		return v == null ? "1" : increment(v.getWeek3thu());
	}

	private String nextWeek3FridaySequence() {
		EdmsWeeklyVisit v = weeklyVisitRepository.findFirstByWeek3friNotOrderByIdDesc("");
		return v == null ? "1" : increment(v.getWeek3fri());
	}

	private String nextWeek3SaturdaySequence() {
		EdmsWeeklyVisit v = weeklyVisitRepository.findFirstByWeek3satNotOrderByIdDesc("");
		return v == null ? "1" : increment(v.getWeek3sat());
	}

	private EdmsWeeklyVisit week4schedule(EdmsWeeklyVisit v, DayOfWeek day) {
		if (day == SUNDAY)
			v.setWeek4sun(nextWeek4SundaySequence());
		else if (day == MONDAY)
			v.setWeek4mon(nextWeek4MondaySequence());
		else if (day == TUESDAY)
			v.setWeek4tue(nextWeek4TuesdaySequence());
		else if (day == WEDNESDAY)
			v.setWeek4wed(nextWeek4WednesdaySequence());
		else if (day == THURSDAY)
			v.setWeek4thu(nextWeek4ThursdaySequence());
		else if (day == FRIDAY)
			v.setWeek4fri(nextWeek4FridaySequence());
		else if (day == SATURDAY)
			v.setWeek4sat(nextWeek4SaturdaySequence());
		return v;
	}

	private String nextWeek4SundaySequence() {
		EdmsWeeklyVisit v = weeklyVisitRepository.findFirstByWeek4sunNotOrderByIdDesc("");
		return v == null ? "1" : increment(v.getWeek4sun());
	}

	private String nextWeek4MondaySequence() {
		EdmsWeeklyVisit v = weeklyVisitRepository.findFirstByWeek4monNotOrderByIdDesc("");
		return v == null ? "1" : increment(v.getWeek4mon());
	}

	private String nextWeek4TuesdaySequence() {
		EdmsWeeklyVisit v = weeklyVisitRepository.findFirstByWeek4tueNotOrderByIdDesc("");
		return v == null ? "1" : increment(v.getWeek4tue());
	}

	private String nextWeek4WednesdaySequence() {
		EdmsWeeklyVisit v = weeklyVisitRepository.findFirstByWeek4wedNotOrderByIdDesc("");
		return v == null ? "1" : increment(v.getWeek4wed());
	}

	private String nextWeek4ThursdaySequence() {
		EdmsWeeklyVisit v = weeklyVisitRepository.findFirstByWeek4thuNotOrderByIdDesc("");
		return v == null ? "1" : increment(v.getWeek4thu());
	}

	private String nextWeek4FridaySequence() {
		EdmsWeeklyVisit v = weeklyVisitRepository.findFirstByWeek4friNotOrderByIdDesc("");
		return v == null ? "1" : increment(v.getWeek4fri());
	}

	private String nextWeek4SaturdaySequence() {
		EdmsWeeklyVisit v = weeklyVisitRepository.findFirstByWeek4satNotOrderByIdDesc("");
		return v == null ? "1" : increment(v.getWeek4sat());
	}

	private void saveCreditDetail(Customer c) {
		EdmsCreditDetail e = creditDetailRepository.findByCustomerCode(code(c));
		CreditDetail cd = getCredit(c);
		if (e == null)
			e = createCreditDetail(e, cd, code(c));
		updateEdmsCreditDetail(e, cd);
	}

	private EdmsCreditDetail createCreditDetail(EdmsCreditDetail e, CreditDetail cd, String code) {
		e = new EdmsCreditDetail();
		e.setCustomerCode(code);
		e.setChequeAllowed(isChequeAllowed(cd));
		return e;
	}

	private Byte isChequeAllowed(CreditDetail cd) {
		return cd == null || cd.getGracePeriodInDays() == 0 ? NO : YES;
	}

	private CreditDetail getCredit(Customer c) {
		return c.getCreditDetails().stream() //
				.filter(p -> !p.getStartDate().isAfter(LocalDate.now())) //
				.max(comparing(CreditDetail::getStartDate)) //
				.orElse(null);
	}

	private void updateEdmsCreditDetail(EdmsCreditDetail e, CreditDetail cd) {
		e.setPayment(payment(cd));
		e.setPaymentTermCode(paymentTerm(cd));
		e.setCreditLimit(creditLimit(cd));
		creditDetailRepository.save(e);
	}

	private String payment(CreditDetail cd) {
		return isCOD(cd) ? CASH_ONLY : CASH_AND_CREDIT;
	}

	private boolean isCOD(CreditDetail cd) {
		return cd == null || cd.getTermInDays() == 0;
	}

	private String paymentTerm(CreditDetail cd) {
		return isCOD(cd) ? COD : cd.getTermInDays() + " Days";
	}

	private BigDecimal creditLimit(CreditDetail cd) {
		return isCOD(cd) ? ZERO : cd.getCreditLimit();
	}

	@Override
	public String getCode(Billable b) {
		String name = b.getAlias();
		if (name == null)
			name = b.getCustomerName();
		return prefix + leftPad(b.getCustomerId().toString(), 8, "0");
	}

	@Override
	public List<String> getProvinces() {
		Iterable<EdmsCustomer> i = customerRepository.findAll();
		return StreamSupport.stream(i.spliterator(), false).map(c -> c.getProvince().toUpperCase().trim()).distinct().collect(Collectors.toList());
	}

	@Override
	public List<String> getCities(String province) {
		List<EdmsCustomer> l = customerRepository.findByProvinceContainingIgnoreCase(province);
		return l.stream().map(c -> correctCity(c)).distinct().collect(Collectors.toList());
	}

	private boolean isAManilaDistrict(String city) {
		return MANILA_DISTRICTS.stream().anyMatch(d -> d.equalsIgnoreCase(city));
	}

	private Customer toCustomer(EdmsCustomer e) {
		Customer c = toCustomer(e.getName());
		c.setStreet(getStreet(e));
		c.setProvince(getProvince(e));
		c.setCity(getCity(e));
		c.setBarangay(getBarangay(c, e));
		c.setChannel(toChannel(e));
		c.setCreditDetails(toCreditDetails(e));
		c.setPrimaryPricingType(dealerPrice());
		c.setRouteHistory(toRouteHistory(e));
		c.setType(OUTLET);
		c.setVisitFrequency(toVisitFrequency(e.getVisitFrequency()));
		c.setVisitSchedule(toVisitSchedule(e));
		return c;
	}

	private PricingType dealerPrice() {
		PricingType p = new PricingType();
		p.setName(DEALER.toString());
		return p;
	}

	private Customer toCustomer(String name) {
		Customer c = new Customer();
		c.setName(name.toUpperCase().trim().replace("`", "'"));
		return c;
	}

	private String getStreet(EdmsCustomer e) {
		String st = getStreetNo(e) + getStreetName(e);
		return !st.isEmpty() ? st : null;
	}

	private String getStreetNo(EdmsCustomer e) {
		String no = e.getStreetNo();
		return no == null || no.isEmpty() ? "" : no + " ";
	}

	private String getStreetName(EdmsCustomer e) {
		String st = e.getStreet();
		return st == null || st.isEmpty() ? "" : st.toUpperCase().trim();
	}

	private Location getProvince(EdmsCustomer e) {
		String prov = e.getProvince();
		if (prov == null || prov.isEmpty())
			prov = province;
		return locationService.toProvince(prov.toUpperCase().trim());
	}

	private Location getCity(EdmsCustomer e) {
		return e == null ? null : getCityFromProperName(e);
	}

	private Location getCityFromProperName(EdmsCustomer e) {
		String city = e.getCity();
		return city == null || city.isEmpty() ? null : getCityFromProperName(city.toUpperCase().trim());
	}

	private Location getCityFromProperName(String city) {
		String name = correctCity(city);
		return locationService.toCity(name);
	}

	private String correctCity(String city) {
		city = city//
				.replace("CITY OF ", "")//
				.replace(" ,CITY", "")//
				.replace("SAN ADRESS", "SAN ANDRES")//
				.replace("BASECO", "PORT AREA")//
				.replace("CALOOCAN", "KALOOKAN");
		return isAManilaDistrict(city) ? EdmsLocationServiceImpl.MANILA : city;
	}

	private String correctCity(EdmsCustomer c) {
		String city = c.getCity();
		return city == null ? null : correctCity(city.toUpperCase().trim());
	}

	private Location getBarangay(Customer c, EdmsCustomer e) {
		String city = locationService.getCity(c);
		return noBarangayOrCityExists(e, city) ? null : getBarangayFromCustomer(e, city.toUpperCase().trim());
	}

	private boolean noBarangayOrCityExists(EdmsCustomer e, String city) {
		return noBarangayExists(e) || noCityExists(e) || noCityExists(city);
	}

	private boolean noBarangayExists(EdmsCustomer e) {
		String brgy = e.getBarangay();
		return brgy == null || brgy.isEmpty();
	}

	private boolean noCityExists(EdmsCustomer e) {
		String city = e.getCity();
		return noCityExists(city);
	}

	private boolean noCityExists(String city) {
		return city == null || city.isEmpty();
	}

	private Location getBarangayFromCustomer(EdmsCustomer e, String city) {
		String barangay = getBarangayFromProperNames(e, city);
		return locationService.getBarangay(barangay, city);
	}

	private String getBarangayFromProperNames(EdmsCustomer e, String city) {
		String barangay = e.getBarangay();
		if (city.equals(MANILA))
			barangay = e.getCity();
		return correctBarangay(barangay.toUpperCase().trim());
	}

	private String correctBarangay(String barangay) {
		return barangay//
				.replace("SAN ADRESS", "SAN ANDRES")//
				.replace("BASECO", "PORT AREA")//
				.replace("BRGY.", "BARANGAY")//
				.replace("BRGY", "BARANGAY");
	}

	private Channel toChannel(EdmsCustomer e) {
		return channelService.toDTO(e.getChannel());
	}

	private List<CreditDetail> toCreditDetails(EdmsCustomer ec) {
		EdmsCreditDetail ecd = creditDetailRepository.findByCustomerCode(ec.getCode());
		if (ecd == null || ecd.getPaymentTermCode().equals(Code.COD))
			return null;
		CreditDetail cd = new CreditDetail();
		cd.setCreditLimit(ecd.getCreditLimit());
		cd.setTermInDays(toTerm(ecd.getPaymentTermCode()));
		cd.setStartDate(edmsService.goLiveDate());
		cd.setIsValid(true);
		cd.setDecidedBy(EDMS);
		cd.setDecidedOn(edmsService.goLiveTimestamp());
		return Arrays.asList(cd);
	}

	private int toTerm(String terms) {
		return Integer.valueOf(StringUtils.substringBefore(terms, " ").trim());
	}

	private List<Routing> toRouteHistory(EdmsCustomer c) {
		Route r = routeService.toNameOnlyRouteFromSeller(c.getSellerCode());
		return toRouteHistory(r);
	}

	private List<Routing> toRouteHistory(Route r) {
		Routing e = new Routing();
		e.setStartDate(edmsService.goLiveDate());
		e.setRoute(r);
		return asList(e);
	}

	private VisitFrequency toVisitFrequency(String f) {
		if (f == null || f.isEmpty())
			return F4;
		f = StringUtils.substringBefore(f, ".");
		return VisitFrequency.valueOf(f);
	}

	private List<WeeklyVisit> toVisitSchedule(EdmsCustomer c) {
		EdmsWeeklyVisit v = weeklyVisitRepository.findByCode(c.getCode());
		return v == null ? null : asList(week1(v), week2(v), week3(v), week4(v), week5(v));
	}

	private WeeklyVisit week1(EdmsWeeklyVisit e) {
		WeeklyVisit v = new WeeklyVisit();
		v.setWeekNo(1);
		v.setSun(e.getWeek1sun() != null && !e.getWeek1sun().isEmpty());
		v.setMon(e.getWeek1mon() != null && !e.getWeek1mon().isEmpty());
		v.setTue(e.getWeek1tue() != null && !e.getWeek1tue().isEmpty());
		v.setWed(e.getWeek1wed() != null && !e.getWeek1wed().isEmpty());
		v.setThu(e.getWeek1thu() != null && !e.getWeek1thu().isEmpty());
		v.setFri(e.getWeek1fri() != null && !e.getWeek1fri().isEmpty());
		v.setSat(e.getWeek1sat() != null && !e.getWeek1sat().isEmpty());
		return v;
	}

	private WeeklyVisit week2(EdmsWeeklyVisit e) {
		WeeklyVisit v = new WeeklyVisit();
		v.setWeekNo(2);
		v.setSun(e.getWeek2sun() != null && !e.getWeek2sun().isEmpty());
		v.setMon(e.getWeek2mon() != null && !e.getWeek2mon().isEmpty());
		v.setTue(e.getWeek2tue() != null && !e.getWeek2tue().isEmpty());
		v.setWed(e.getWeek2wed() != null && !e.getWeek2wed().isEmpty());
		v.setThu(e.getWeek2thu() != null && !e.getWeek2thu().isEmpty());
		v.setFri(e.getWeek2fri() != null && !e.getWeek2fri().isEmpty());
		v.setSat(e.getWeek2sat() != null && !e.getWeek2sat().isEmpty());
		return v;
	}

	private WeeklyVisit week3(EdmsWeeklyVisit e) {
		WeeklyVisit v = new WeeklyVisit();
		v.setWeekNo(3);
		v.setSun(e.getWeek3sun() != null && !e.getWeek3sun().isEmpty());
		v.setMon(e.getWeek3mon() != null && !e.getWeek3mon().isEmpty());
		v.setTue(e.getWeek3tue() != null && !e.getWeek3tue().isEmpty());
		v.setWed(e.getWeek3wed() != null && !e.getWeek3wed().isEmpty());
		v.setThu(e.getWeek3thu() != null && !e.getWeek3thu().isEmpty());
		v.setFri(e.getWeek3fri() != null && !e.getWeek3fri().isEmpty());
		v.setSat(e.getWeek3sat() != null && !e.getWeek3sat().isEmpty());
		return v;
	}

	private WeeklyVisit week4(EdmsWeeklyVisit e) {
		WeeklyVisit v = new WeeklyVisit();
		v.setWeekNo(4);
		v.setSun(e.getWeek4sun() != null && !e.getWeek4sun().isEmpty());
		v.setMon(e.getWeek4mon() != null && !e.getWeek4mon().isEmpty());
		v.setTue(e.getWeek4tue() != null && !e.getWeek4tue().isEmpty());
		v.setWed(e.getWeek4wed() != null && !e.getWeek4wed().isEmpty());
		v.setThu(e.getWeek4thu() != null && !e.getWeek4thu().isEmpty());
		v.setFri(e.getWeek4fri() != null && !e.getWeek4fri().isEmpty());
		v.setSat(e.getWeek4sat() != null && !e.getWeek4sat().isEmpty());
		return v;
	}

	private WeeklyVisit week5(EdmsWeeklyVisit e) {
		WeeklyVisit v = new WeeklyVisit();
		v.setWeekNo(5);
		v.setSun(e.getWeek1sun() != null && !e.getWeek1sun().isEmpty());
		v.setMon(e.getWeek1mon() != null && !e.getWeek1mon().isEmpty());
		v.setTue(e.getWeek1tue() != null && !e.getWeek1tue().isEmpty());
		v.setWed(e.getWeek1wed() != null && !e.getWeek1wed().isEmpty());
		v.setThu(e.getWeek1thu() != null && !e.getWeek1thu().isEmpty());
		v.setFri(e.getWeek1fri() != null && !e.getWeek1fri().isEmpty());
		v.setSat(e.getWeek1sat() != null && !e.getWeek1sat().isEmpty());
		return v;
	}

	@Override
	public List<Customer> list() {
		Iterable<EdmsCustomer> i = customerRepository.findAll();
		return StreamSupport.stream(i.spliterator(), false).map(e -> toCustomer(e)).collect(Collectors.toList());
	}

	@Override
	public List<Customer> listonAction() {
		return sellerService.getAll().map(s -> s.getTruckCode().toUpperCase().trim()).map(n -> toExTruck(n)).collect(Collectors.toList());
	}

	private Customer toExTruck(String name) {
		Customer c = toCustomer(name);
		c.setType(PartnerType.EX_TRUCK);
		c.setStreet(street.toUpperCase());
		c.setBarangay(locationService.toBarangay(barangay));
		c.setCity(locationService.toCity(city));
		c.setProvince(locationService.toProvince(province));
		c.setPrimaryPricingType(dealerPrice());
		c.setRouteHistory(toRouteHistory(name));
		return c;
	}

	private List<Routing> toRouteHistory(String truck) {
		Route r = routeService.toNameOnlyRouteFromTruck(truck);
		return toRouteHistory(r);
	}

	@Override
	public String getName(EdmsInvoice i) {
		EdmsCustomer e = customerRepository.findByCodeIgnoreCase(i.getCustomerCode());
		return e == null ? null : e.getName().toUpperCase().trim().replace("`", "'");
	}
}
