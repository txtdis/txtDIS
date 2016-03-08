package ph.txtdis.controller;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static ph.txtdis.type.PartnerType.CUSTOMER;
import static ph.txtdis.type.PartnerType.FINANCIAL;
import static ph.txtdis.util.SpringUtils.username;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static ph.txtdis.type.LocationType.BARANGAY;
import static ph.txtdis.type.LocationType.CITY;
import static ph.txtdis.type.LocationType.PROVINCE;

import ph.txtdis.domain.Customer;
import ph.txtdis.domain.WeeklyVisit;
import ph.txtdis.repository.CustomerRepository;
import ph.txtdis.type.VisitFrequency;

@RestController("customerController")
@RequestMapping("/customers")
public class CustomerController extends SpunController<CustomerRepository, Customer, Long> {

	@Value("${vendor.id}")
	private String vendorId;

	@RequestMapping(path = "/banks", method = GET)
	public ResponseEntity<?> findBanks() {
		List<Customer> l = repository.findByDeactivatedOnNullAndTypeOrderByNameAsc(FINANCIAL);
		l = l.stream().map(c -> idAndNameOnly(c)).collect(toList());
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/find", method = GET)
	public ResponseEntity<?> findByName(@RequestParam("name") String s) {
		Customer c = repository.findByName(s);
		return new ResponseEntity<>(c, OK);
	}

	@RequestMapping(path = "/noContactDetails", method = GET)
	public ResponseEntity<?> findNoContactDetails() {
		List<Customer> l = repository.findByDeactivatedOnNullAndTypeAndContactNameNullAndCreditDetailsNotNull(CUSTOMER);
		if (l == null)
			l = repository.findByDeactivatedOnNullAndTypeAndContactNameNullAndCustomerDiscountsNotNull(CUSTOMER);
		Customer c = filterBySeller(l, username());
		return new ResponseEntity<>(c, OK);
	}

	@RequestMapping(path = "/noDesignation", method = GET)
	public ResponseEntity<?> findNoDesignation() {
		List<Customer> l = repository.findByDeactivatedOnNullAndTypeAndContactNameNotNullAndContactTitleNull(CUSTOMER);
		Customer c = filterBySeller(l, username());
		return new ResponseEntity<>(c, OK);
	}

	@RequestMapping(path = "/noMobile", method = GET)
	public ResponseEntity<?> findNoMobileNo() {
		List<Customer> l = repository.findByDeactivatedOnNullAndTypeAndContactNameNotNullAndMobileNull(CUSTOMER);
		Customer c = filterBySeller(l, username());
		return new ResponseEntity<>(c, OK);
	}

	@RequestMapping(path = "/noStreetAddress", method = GET)
	public ResponseEntity<?> findNoStreetAddress() {
		List<Customer> l = repository.findByDeactivatedOnNullAndTypeAndStreetNull(CUSTOMER);
		Customer c = filterBySeller(l, username());
		return new ResponseEntity<>(c, OK);
	}

	@RequestMapping(path = "/noSurname", method = GET)
	public ResponseEntity<?> findNoSurname() {
		List<Customer> l = repository
				.findByDeactivatedOnNullAndTypeAndContactNameNotNullAndContactSurnameNull(CUSTOMER);
		if (l == null) {
			l = repository.findByDeactivatedOnNullAndContactNameNotNullAndContactSurnameNotNull(CUSTOMER);
			l = l.stream().filter(u -> u.getContactName().equals(u.getContactSurname())).collect(toList());
		}
		Customer c = filterBySeller(l, username());
		return new ResponseEntity<>(c, OK);
	}

	@RequestMapping(path = "/notCorrectBarangayAddress", method = GET)
	public ResponseEntity<?> findNotCorrectBarangayAddress() {
		List<Customer> l = repository.findByDeactivatedOnNullAndTypeAndBarangayTypeNot(CUSTOMER, BARANGAY);
		Customer c = filterBySeller(l, username());
		return new ResponseEntity<>(c, OK);
	}

	@RequestMapping(path = "/notCorrectCityAddress", method = GET)
	public ResponseEntity<?> findNotCorrectCityAddress() {
		List<Customer> l = repository.findByDeactivatedOnNullAndTypeAndCityTypeNot(CUSTOMER, CITY);
		Customer c = filterBySeller(l, username());
		return new ResponseEntity<>(c, OK);
	}

	@RequestMapping(path = "/notCorrectProvincialAddress", method = GET)
	public ResponseEntity<?> findNotCorrectProvincialAddress() {
		List<Customer> l = repository.findByDeactivatedOnNullAndTypeAndProvinceTypeNot(CUSTOMER, PROVINCE);
		Customer c = filterBySeller(l, username());
		return new ResponseEntity<>(c, OK);
	}

	@RequestMapping(path = "/notTheSameVisitFrequencyAndSchedule", method = GET)
	public ResponseEntity<?> findNotTheSameVisitFrequencyAndSchedule() {
		List<Customer> l = repository
				.findByDeactivatedOnNullAndTypeAndChannelVisitedTrueAndVisitFrequencyNotNullAndVisitScheduleNotNull(
						CUSTOMER);
		l = l.stream().filter(u -> count(u.getVisitFrequency()) != count(u.getVisitSchedule()))
				.collect(Collectors.toList());
		Customer c = filterBySeller(l, username());
		return new ResponseEntity<>(c, OK);
	}

	@RequestMapping(path = "/notTheSameWeeksOneAndFiveVisitSchedules", method = GET)
	public ResponseEntity<?> findNotTheSameWeeksOneAndFiveVisitSchedule() {
		List<Customer> l = repository
				.findByDeactivatedOnNullAndTypeAndChannelVisitedTrueAndVisitFrequencyNotNullAndVisitScheduleNotNull(
						CUSTOMER);
		l = l.stream().filter(v -> !v.getVisitSchedule().get(0).equals(v.getVisitSchedule().get(4)))
				.collect(Collectors.toList());
		Customer c = filterBySeller(l, username());
		return new ResponseEntity<>(c, OK);
	}

	@RequestMapping(path = "/noVisitSchedule", method = GET)
	public ResponseEntity<?> findNoVisitSchedule() {
		List<Customer> l = repository
				.findByDeactivatedOnNullAndTypeAndChannelVisitedTrueAndVisitFrequencyNull(CUSTOMER);
		if (l == null)
			l = repository.findByDeactivatedOnNullAndTypeAndChannelVisitedTrueAndVisitScheduleNull(CUSTOMER);
		Customer c = filterBySeller(l, username());
		return new ResponseEntity<>(c, OK);
	}

	@RequestMapping(path = "/vendor", method = GET)
	public ResponseEntity<?> findVendor() {
		Customer c = repository.findOne(Long.valueOf(vendorId));
		return new ResponseEntity<>(c, OK);
	}

	@RequestMapping(method = GET)
	public ResponseEntity<?> list() {
		List<Customer> l = (List<Customer>) repository.findAll();
		if (l != null)
			l = l.stream().map(c -> basicInfoOnly(c)).collect(toList());
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/search", method = GET)
	public ResponseEntity<?> searchByName(@RequestParam("name") String s) {
		List<Customer> l = repository.findByNameContaining(s);
		if (l != null)
			l = l.stream().map(c -> basicInfoOnly(c)).collect(toList());
		return new ResponseEntity<>(l, OK);
	}

	private Customer basicInfoOnly(Customer c) {
		Customer n = idAndNameOnly(c);
		n.setRouteHistory(c.getRouteHistory());
		n.setStreet(c.getStreet());
		n.setBarangay(c.getBarangay());
		n.setCity(c.getCity());
		return n;
	}

	private int count(List<WeeklyVisit> l) {
		int i = 0;
		for (WeeklyVisit v : l) {
			if (v.getSun() != null && v.getSun())
				++i;
			if (v.getMon() != null && v.getMon())
				++i;
			if (v.getTue() != null && v.getTue())
				++i;
			if (v.getWed() != null && v.getWed())
				++i;
			if (v.getThu() != null && v.getThu())
				++i;
			if (v.getFri() != null && v.getFri())
				++i;
			if (v.getSat() != null && v.getSat())
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

	private Customer filterBySeller(List<Customer> l, String seller) {
		Optional<Customer> o = l.stream().filter(c -> seller.equals(c.getSeller())).findFirst();
		return o.isPresent() ? idAndNameOnly(o.get()) : null;
	}

	private Customer idAndNameOnly(Customer c) {
		Customer n = new Customer();
		n.setId(c.getId());
		n.setName(c.getName());
		return n;
	}
}