package ph.txtdis.service;

import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.service.LocationServiceImpl.MANILA;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.EdmsCreditDetail;
import ph.txtdis.domain.EdmsCustomer;
import ph.txtdis.domain.EdmsInvoice;
import ph.txtdis.domain.EdmsWeeklyVisit;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.Channel;
import ph.txtdis.dto.CreditDetail;
import ph.txtdis.dto.Customer;
import ph.txtdis.dto.Location;
import ph.txtdis.dto.PartnerType;
import ph.txtdis.dto.Route;
import ph.txtdis.dto.Routing;
import ph.txtdis.dto.WeeklyVisit;
import ph.txtdis.repository.EdmsCreditDetailRepository;
import ph.txtdis.repository.EdmsCustomerRepository;
import ph.txtdis.repository.EdmsWeeklyVisitRepository;
import ph.txtdis.type.VisitFrequency;
import ph.txtdis.util.Code;
import ph.txtdis.util.DateTimeUtils;

@Service("customerService")
public class CustomerServiceImpl implements CustomerService, DealerPriced {

	private static Logger logger = getLogger(CustomerServiceImpl.class);

	@Autowired
	private EdmsCreditDetailRepository edmsCreditDetailRepository;

	@Autowired
	private EdmsCustomerRepository edmsCustomerRepository;

	@Autowired
	private EdmsWeeklyVisitRepository edmsWeeklyVisitRepository;

	@Autowired
	private AutoNumberService autoNumberService;

	@Autowired
	private ChannelService channelService;

	@Autowired
	private EdmsService edmsService;

	@Autowired
	private EdmsLocationService locationService;

	@Autowired
	private RouteService routeService;

	@Autowired
	private SellerService sellerService;

	@Autowired
	private VendorService vendorService;

	@Value("${client.name}")
	private String clientName;

	@Value("${client.barangay}")
	private String clientBarangay;

	@Value("${client.city}")
	private String clientCity;

	@Value("${client.province}")
	private String clientProvince;

	@Value("${prefix.customer}")
	private String prefix;

	@Override
	public Customer save(Customer c) {
		saveCustomerAndAutoNo(c);
		saveCreditDetail(c);
		return c;
	}

	private void saveCustomerAndAutoNo(Customer c) {
		EdmsCustomer e = edmsCustomerRepository.findByNameIgnoreCase(c.getName());
		if (e == null)
			e = saveCreatedCustomerAndAutoNo(c);
		updateEdmsCustomer(e, c);
	}

	private EdmsCustomer saveCreatedCustomerAndAutoNo(Customer c) {
		saveAutoNo(c);
		return createEdmsCustomer(c);
	}

	private EdmsCustomer createEdmsCustomer(Customer c) {
		EdmsCustomer e = new EdmsCustomer();
		e.setCode(getCode(c));
		e.setName(c.getName());
		e.setBusinessName(c.getName());
		e.setTin("");
		e.setSeller("");
		e.setExclusivity(Code.FE);
		e.setSupervisor(vendorService.getSupervisor());
		e.setBldg("");
		e.setStreetNo("");
		e.setDistrictCode(vendorService.getDistrictCode());
		e.setTerritoryCode(vendorService.getTerritoryCode());
		e.setAreaCode(vendorService.getAreaCode());
		e.setYearsOwned("0");
		e.setHomeAddress("");
		e.setBirthDate("");
		e.setCreatedBy(c.getCreatedBy());
		e.setCreatedOn(DateTimeUtils.toTimestampText(c.getCreatedOn()));
		return e;
	}

	private String getCode(Customer c) {
		return prefix + StringUtils.leftPad(c.getId().toString(), 5, "0");
	}

	private void updateEdmsCustomer(EdmsCustomer e, Customer c) {
		e.setSellerCode(sellerService.getCode(c));
		e.setChannel(getChannel(c));
		e.setStatus(getStatus(c));
		e.setVisitFrequency(getVisitFrequency(c));
		e.setVisitDay("");
		e.setStreet(c.getStreet());
		e.setBarangay(getEdmsBarangay(c));
		e.setCity(getEdmsCity(c));
		e.setProvince(c.getProvince().getName());
		e.setDistrictCode(vendorService.getDistrictCode());
		e.setOwner(c.getContactName());
		e.setPhoneNo(c.getMobile());
		e.setModifiedBy(c.getLastModifiedBy());
		e.setModifiedOn(DateTimeUtils.toTimestampText(c.getLastModifiedOn()));
		edmsCustomerRepository.save(e);
	}

	private String getChannel(Customer c) {
		Channel channel = c.getChannel();
		return channel == null ? "" : c.getChannel().getName();
	}

	private String getStatus(Customer c) {
		return c.getDeactivatedOn() == null ? Code.ACTIVE : Code.VOID;
	}

	private String getEdmsCity(Customer c) {
		String city = getCity(c);
		return !city.equals(MANILA) ? city : getBarangay(c);
	}

	private String getBarangay(Customer c) {
		Location brgy = c.getBarangay();
		return brgy == null ? null : brgy.getName();
	}

	private String getEdmsBarangay(Customer c) {
		String brgy = getBarangay(c);
		return brgy == null ? null : getEdmsBarangay(getCity(c), brgy);
	}

	private String getEdmsBarangay(String city, String brgy) {
		return brgy == null || city.equals(MANILA) ? null : brgy;
	}

	private String getVisitFrequency(Customer c) {
		VisitFrequency f = c.getVisitFrequency();
		return f == null ? null : f.toString();
	}

	private void saveAutoNo(Customer c) {
		autoNumberService.saveAutoNo( //
				Code.CUSTOMER_PREFIX, //
				c.getIdNo());
	}

	private void saveCreditDetail(Customer c) {
		CreditDetail credit = c.getCredit(LocalDate.now());
		if (credit != null)
			saveCreditDetail(credit, getCode(c));
	}

	private void saveCreditDetail(CreditDetail credit, String code) {
		EdmsCreditDetail e = edmsCreditDetailRepository.findByCustomerCode(code);
		if (e == null)
			e = createEdmsCreditDetail(e, code);
		updateEdmsCreditDetail(e, credit);
	}

	private EdmsCreditDetail createEdmsCreditDetail(EdmsCreditDetail e, String code) {
		e = new EdmsCreditDetail();
		e.setCustomerCode(code);
		e.setDiscountGiven(Code.YES);
		e.setBaseDiscount(BigDecimal.ZERO);
		e.setCashDiscount(BigDecimal.ZERO);
		e.setDiscountValue(BigDecimal.ZERO);
		e.setOtherDiscount(BigDecimal.ZERO);
		e.setPercentDiscount(BigDecimal.ZERO);
		e.setChequeAllowed(Code.YES);
		return e;
	}

	private void updateEdmsCreditDetail(EdmsCreditDetail e, CreditDetail credit) {
		e.setPaymentTermCode(getPaymentTerm(credit));
		e.setCreditLimit(credit.getCreditLimit());
		edmsCreditDetailRepository.save(e);
	}

	@Override
	public String getCode(Billable i) {
		String customerName = i.getAlias();
		if (customerName == null)
			customerName = i.getCustomerName();
		EdmsCustomer e = edmsCustomerRepository.findByNameIgnoreCase(customerName);
		return e.getCode();
	}

	private String getPaymentTerm(CreditDetail c) {
		return c.getTermInDays() == 0 ? Code.COD : c.getTermInDays() + " Days";
	}

	@Override
	public List<String> getProvinces() {
		Iterable<EdmsCustomer> i = edmsCustomerRepository.findAll();
		return StreamSupport.stream(i.spliterator(), false).map(c -> c.getProvince().toUpperCase().trim()).distinct()
				.collect(Collectors.toList());
	}

	@Override
	public List<String> getCities(String province) {
		List<EdmsCustomer> l = edmsCustomerRepository.findByProvinceContainingIgnoreCase(province);
		return l.stream().map(c -> correctCity(c)).distinct().collect(Collectors.toList());
	}

	private boolean isAManilaDistrict(String city) {
		return LocationServiceImpl.MANILA_DISTRICTS.stream().anyMatch(d -> d.equals(city));
	}

	@Override
	public Customer findById(Long id) {
		EdmsCustomer e = edmsCustomerRepository.findOne(id);
		return e == null ? null : toCustomer(e);
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
		c.setType(PartnerType.OUTLET);
		c.setVisitFrequency(toVisitFrequency(e.getVisitFrequency()));
		c.setVisitSchedule(toVisitSchedule(e));
		logger.info("\n      Customer: " + c);
		return c;
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
			prov = clientProvince;
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
		return isAManilaDistrict(city) ? LocationServiceImpl.MANILA : city;
	}

	private String correctCity(EdmsCustomer c) {
		String city = c.getCity();
		return city == null ? null : correctCity(city.toUpperCase().trim());
	}

	private Location getBarangay(Customer c, EdmsCustomer e) {
		return c == null ? null : getBarangayFromCustomer(c, e);
	}

	private Location getBarangayFromCustomer(Customer c, EdmsCustomer e) {
		String city = getCity(c);
		return noBarangayOrCityExists(e, city) ? null : getBarangayFromCustomer(e, city.toUpperCase().trim());
	}

	private boolean noBarangayOrCityExists(EdmsCustomer e, String city) {
		return noBarangayExists(e) || noCityExists(e) || noCityExists(city);
	}

	private boolean noBarangayExists(EdmsCustomer e) {
		return e.getBarangay() == null || e.getBarangay().isEmpty();
	}

	private boolean noCityExists(EdmsCustomer e) {
		return e.getCity() == null || e.getCity().isEmpty();
	}

	private boolean noCityExists(String city) {
		return city == null || city.isEmpty();
	}

	private String getCity(Customer c) {
		Location city = c.getCity();
		return city == null ? null : city.getName();
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
		EdmsCreditDetail ecd = edmsCreditDetailRepository.findByCustomerCode(ec.getCode());
		if (ecd == null || ecd.getPaymentTermCode().equals(Code.COD))
			return null;
		CreditDetail cd = new CreditDetail();
		cd.setCreditLimit(ecd.getCreditLimit());
		cd.setTermInDays(toTerm(ecd.getPaymentTermCode()));
		cd.setStartDate(edmsService.goLiveDate());
		cd.setIsValid(true);
		cd.setDecidedBy(Code.EDMS);
		cd.setDecidedOn(edmsService.goLiveTimestamp());
		logger.info("\n    Credit: " + cd);
		return Arrays.asList(cd);
	}

	private int toTerm(String terms) {
		return Integer.valueOf(StringUtils.substringBefore(terms, " ").trim());
	}

	private List<Routing> toRouteHistory(EdmsCustomer c) {
		Route r = routeService.toNameOnlyDTOFromSeller(c.getSellerCode());
		return toRouteHistory(r);
	}

	private List<Routing> toRouteHistory(Route r) {
		Routing e = new Routing();
		e.setStartDate(edmsService.goLiveDate());
		e.setRoute(r);
		logger.info("\n    Route: " + e.getRoute());
		return Arrays.asList(e);
	}

	private VisitFrequency toVisitFrequency(String f) {
		if (f == null || f.isEmpty())
			return VisitFrequency.F4;
		f = StringUtils.substringBefore(f, ".");
		return VisitFrequency.valueOf(f);
	}

	private List<WeeklyVisit> toVisitSchedule(EdmsCustomer c) {
		EdmsWeeklyVisit v = edmsWeeklyVisitRepository.findByCode(c.getCode());
		if (v == null)
			return null;
		return Arrays.asList(week1(v), week2(v), week3(v), week4(v), week5(v));
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
		logger.info("\n    Week 1: " + daysVisited(v));
		return v;
	}

	private String daysVisited(WeeklyVisit v) {
		return (v.isSun() ? "su " : "   ") //
				+ (v.isMon() ? "mo " : "   ") //
				+ (v.isTue() ? "tu " : "   ") //
				+ (v.isWed() ? "we " : "   ") //
				+ (v.isThu() ? "th " : "   ") //
				+ (v.isSat() ? "sa " : "   ");
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
		logger.info("\n    Week 2: " + daysVisited(v));
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
		logger.info("\n    Week 3: " + daysVisited(v));
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
		logger.info("\n    Week 4: " + daysVisited(v));
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
		logger.info("\n    Week 5: " + daysVisited(v));
		return v;
	}

	@Override
	public List<Customer> list() {
		Iterable<EdmsCustomer> i = edmsCustomerRepository.findAll();
		return StreamSupport.stream(i.spliterator(), false).map(e -> toCustomer(e)).collect(Collectors.toList());
	}

	@Override
	public List<Customer> listExTrucks() {
		return sellerService.getAll().map(s -> s.getTruckCode().toUpperCase().trim()).map(n -> toExTruck(n))
				.collect(Collectors.toList());
	}

	private Customer toExTruck(String name) {
		logger.info("\n    ExTruck: " + name);
		Customer c = toCustomer(name);
		c.setType(PartnerType.EX_TRUCK);
		c.setBarangay(locationService.toBarangay(clientBarangay));
		c.setCity(locationService.toCity(clientCity));
		c.setProvince(locationService.toProvince(clientProvince));
		c.setPrimaryPricingType(dealerPrice());
		c.setRouteHistory(toRouteHistory(name));
		return c;
	}

	private List<Routing> toRouteHistory(String truck) {
		Route r = routeService.toNameOnlyDTOFromTruck(truck);
		return toRouteHistory(r);
	}

	@Override
	public String getName(EdmsInvoice i) {
		EdmsCustomer e = edmsCustomerRepository.findByCode(i.getCustomerCode());
		return e == null ? null : e.getName().toUpperCase().trim().replace("`", "'");
	}
}
