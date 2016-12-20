package ph.txtdis;

import static java.util.Arrays.asList;
import static ph.txtdis.type.UserType.CASHIER;
import static ph.txtdis.type.UserType.COLLECTOR;
import static ph.txtdis.type.UserType.DRIVER;
import static ph.txtdis.type.UserType.HEAD_CASHIER;
import static ph.txtdis.type.UserType.HELPER;
import static ph.txtdis.type.UserType.MANAGER;
import static ph.txtdis.type.UserType.SALES_ENCODER;
import static ph.txtdis.type.UserType.SELLER;
import static ph.txtdis.type.UserType.STOCK_CHECKER;
import static ph.txtdis.type.UserType.STOCK_TAKER;
import static ph.txtdis.type.UserType.STORE_KEEPER;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import ph.txtdis.domain.AuthorityEntity;
import ph.txtdis.domain.HolidayEntity;
import ph.txtdis.domain.SyncEntity;
import ph.txtdis.domain.TruckEntity;
import ph.txtdis.domain.UserEntity;
import ph.txtdis.dto.Warehouse;
import ph.txtdis.repository.HolidayRepository;
import ph.txtdis.repository.SyncRepository;
import ph.txtdis.repository.TruckRepository;
import ph.txtdis.repository.UserRepository;
import ph.txtdis.service.CredentialService;
import ph.txtdis.service.WarehouseService;
import ph.txtdis.type.SyncType;
import ph.txtdis.type.UserType;
import ph.txtdis.util.DateTimeUtils;

@Configuration("persistenceConfiguration")
public class PersistenceConfiguration {

	private static final String WAREHOUSE = "HAVANA";

	@Autowired
	private HolidayRepository holidayRepository;

	@Autowired
	private SyncRepository syncRepository;

	@Autowired
	private TruckRepository truckRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CredentialService credentialService;

	@Autowired
	private WarehouseService warehouseService;

	private AuthorityEntity manager, //
			driver, helper, seller, salesEncoder, //		
			collector, cashier, headCashier, //
			storekeeper, stockChecker, stockTaker;

	@PostConstruct
	private void start() {
		if (syncRepository.count() == 0)
			try {
				setRoles();
				userRepository.save(users());
				truckRepository.save(trucks());
				holidayRepository.save(philippineHolidays2017());
				warehouseService.save(warehouse());
				syncRepository.save(sync());
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	private void setRoles() {
		manager = newRole(MANAGER);
		driver = newRole(DRIVER);
		helper = newRole(HELPER);
		seller = newRole(SELLER);
		salesEncoder = newRole(SALES_ENCODER);
		collector = newRole(COLLECTOR);
		cashier = newRole(CASHIER);
		headCashier = newRole(HEAD_CASHIER);
		storekeeper = newRole(STORE_KEEPER);
		stockChecker = newRole(STOCK_CHECKER);
		stockTaker = newRole(STOCK_TAKER);
	}

	private AuthorityEntity newRole(UserType role) {
		AuthorityEntity r = new AuthorityEntity();
		r.setAuthority(role);
		return r;
	}

	private List<UserEntity> users() {
		return Arrays.asList(//
				newUser("SO", "JACKIE", "robbie", "917 568 2168", "manila12@gmail.com", manager), //
				newUser("SO", "RONALD", "alphacowboy", "917 895 8268", "ronaldallanso@yahoo.com", manager), //
				newUser(null, "SYSGEN", "Vierski@1", "949 859 2927", "txtdis.erp@gmail.com", manager) //
		);
	}

	private UserEntity newUser(String surname, String username, String password, String phone, String email,
			AuthorityEntity... roles) {
		UserEntity u = new UserEntity();
		u.setUsername(username);
		u.setSurname(surname);
		u.setEnabled(true);
		u.setPassword(encodePassword(password));
		u.setMobile(formatPhoneNumber(phone));
		u.setEmail(email);
		u.setRoles(asList(roles));
		return u;
	}

	private String encodePassword(String password) {
		if (password == null)
			password = "password";
		return credentialService.encode(password);
	}

	private String formatPhoneNumber(String phone) {
		return phone == null ? null : "+63" + phone.replace(" ", "");
	}

	private List<TruckEntity> trucks() {
		return Arrays.asList( //
				newTruck("") //
		);
	}

	private TruckEntity newTruck(String plateNo) {
		TruckEntity t = new TruckEntity();
		t.setName(plateNo);
		return t;
	}

	private List<HolidayEntity> philippineHolidays2017() {
		return Arrays.asList( //
				newHoliday("NEW YEAR", 2017, 1, 1), //
				newHoliday("AQUINO", 2017, 8, 21), //
				newHoliday("BONIFACIO", 2017, 10, 30), //
				newHoliday("HALLOWEEN", 2017, 10, 31), //
				newHoliday("ALL SAINTS", 2017, 11, 1), //
				newHoliday("CHRISTMAS EVE", 2017, 12, 24), //
				newHoliday("CHRISTMAS", 2017, 12, 25), //
				newHoliday("RIZAL", 2017, 12, 30), //
				newHoliday("NEW YEAR'S EVE", 2017, 12, 31));
	}

	private HolidayEntity newHoliday(String name, int year, int month, int day) {
		return new HolidayEntity(name, LocalDate.of(year, month, day));
	}

	private Warehouse warehouse() {
		Warehouse w = new Warehouse();
		w.setName(WAREHOUSE);
		return w;
	}

	private SyncEntity sync() {
		SyncEntity s = new SyncEntity();
		s.setLastSync(DateTimeUtils.toUtilDate("2009-04-09"));
		s.setType(SyncType.VERSION);
		return s;
	}
}
