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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import ph.txtdis.domain.AccountEntity;
import ph.txtdis.domain.AuthorityEntity;
import ph.txtdis.domain.CustomerEntity;
import ph.txtdis.domain.HolidayEntity;
import ph.txtdis.domain.ItemEntity;
import ph.txtdis.domain.PriceEntity;
import ph.txtdis.domain.PricingTypeEntity;
import ph.txtdis.domain.QtyPerUomEntity;
import ph.txtdis.domain.RouteEntity;
import ph.txtdis.domain.RoutingEntity;
import ph.txtdis.domain.SyncEntity;
import ph.txtdis.domain.TruckEntity;
import ph.txtdis.domain.UserEntity;
import ph.txtdis.domain.WarehouseEntity;
import ph.txtdis.dto.PartnerType;
import ph.txtdis.repository.CustomerRepository;
import ph.txtdis.repository.HolidayRepository;
import ph.txtdis.repository.ItemRepository;
import ph.txtdis.repository.PricingTypeRepository;
import ph.txtdis.repository.RouteRepository;
import ph.txtdis.repository.SyncRepository;
import ph.txtdis.repository.TruckRepository;
import ph.txtdis.repository.UserRepository;
import ph.txtdis.repository.WarehouseRepository;
import ph.txtdis.service.CredentialService;
import ph.txtdis.type.ItemType;
import ph.txtdis.type.SyncType;
import ph.txtdis.type.UomType;
import ph.txtdis.type.UserType;
import ph.txtdis.util.DateTimeUtils;

@Configuration("persistenceConfiguration")
public class PersistenceConfiguration {

	private static final String SYSGEN = "SYSGEN";

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private HolidayRepository holidayRepository;

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private PricingTypeRepository pricingTypeRepository;

	@Autowired
	private RouteRepository routeRepository;

	@Autowired
	private SyncRepository syncRepository;

	@Autowired
	private TruckRepository truckRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private WarehouseRepository warehouseRepository;

	@Autowired
	private CredentialService credentialService;

	@Value("${go.live}")
	private String goLive;

	private AuthorityEntity manager, //
			driver, helper, seller, salesEncoder, //
			collector, cashier, headCashier, //
			storekeeper, stockChecker, stockTaker;

	private PricingTypeEntity purchase, dealer;

	private RouteEntity e3cg, e3ch, e3ci, e3cj, e3ck;

	@PostConstruct
	private void start() {
		if (syncRepository.count() == 0)
			try {
				setRoles();
				userRepository.save(users());
				truckRepository.save(trucks());
				holidayRepository.save(philippineHolidays2017());
				warehouseRepository.save(warehouse());
				setPricingTypes();
				itemRepository.save(empties());
				itemRepository.save(items());
				setRoutes();
				customerRepository.save(customers());
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
				newHoliday("AFTER NEW YEAR", 2017, 1, 2), //
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

	private WarehouseEntity warehouse() {
		WarehouseEntity w = new WarehouseEntity();
		w.setName("HAVANA");
		return w;
	}

	private void setPricingTypes() {
		purchase = newPricingType("PURCHASE");
		dealer = newPricingType("DEALER");
	}

	private PricingTypeEntity newPricingType(String name) {
		PricingTypeEntity p = new PricingTypeEntity();
		p.setName(name);
		return pricingTypeRepository.save(p);
	}

	private List<ItemEntity> empties() {
		return Arrays.asList( //
				newItem(147, "CASE237", "237 CASE FULL", 24, 90.00, 90.00, null),
				newItem(156, "CASE355", "355 CASE FULL", 24, 90.00, 90.00, null),
				newItem(290, "CASE237RTOUG", "237 CASE FULL ULTRAGLS RTO", 24, 90.00, 90.00, null),
				newItem(291, "CASE237SPRUG", "237 CASE FULL ULTRAGLS SPRITE", 24, 90.00, 90.00, null),
				newItem(299, "CASE800POPUG", "800 CASE FULL UTRAGLASS POP", 12, 100.00, 100.00, null),
				newItem(321, "CASE355RTOUG", "355 CASE FULL ULTRAGLS RTO", 24, 90.00, 90.00, null),
				newItem(322, "CASE355SPRUG", "355 CASE FULL ULTRAGLS SPRITE", 24, 90.00, 90.00, null),
				newItem(333, "CASE240SPAUG", "240 CASE FULL SPARKLE ULTRAGLASS", 24, 90.00, 90.00, null),
				newItem(343, "CASE355COKUG", "355 CASE FULL ULTRA GLASS COKE", 24, 90.00, 90.00, null),
				newItem(677, "CASE750RAINB", "750 CASE FULL RAINBOW", 12, 124.00, 124.00, null),
				newItem(725, "CASE237COKTG", "237 CASE FULL TALL GLASS COKE", 24, 90.00, 90.00, null),
				newItem(657, "CASE750RTO", "750 CASE FULL RTO", 12, 124.00, 124.00, null),
				newItem(658, "CASE750SPRT", "750 CASE FULL SPRITE", 12, 124.00, 124.00, null),
				newItem(304, "CASE750COKE", "750 CASE FULL COKE", 12, 124.00, 124.00, null),
				newItem(92, "PJG5WIL", "5GL PJG EMPTY WILKINS", 1, 250.00, 250.00, null),
				newItem(180, "CASE240POPUG", "240 CASE FULL ULTRAGLS POP", 1, 90.00, 90.00, null),
				newItem(82, "CASESHELL1.0", "1.0 SHELL PLASTIC", 1, 52.00, 52.00, null),
				newItem(353, "CASE240SARUG", "240 CASE FULL SARSI ULTRAGLASS", 1, 90.00, 90.00, null),
				newItem(78, "CASESHELL355", "355 SHELL PLASTIC", 1, 42.00, 42.00, null) //
		);
	}

	private List<ItemEntity> items() {
		return Arrays.asList( //
				newItem(101620, "COK330X24", "330MLALCNN1X24 COKE", 24, 492.00, 480.00, null),
				newItem(101621, "RTO330X24", "330MLALCNN1X24 ROYAL TRU ORG", 24, 492.00, 480.00, null),
				newItem(101622, "SPR330X24", "330MLALCNN1X24 SPRITE", 24, 492.00, 480.00, null),
				newItem(101625, "RTOGRP330X24", "330MLALCNN1X24 ROYAL TRU GRP", 24, 492.00, 480.00, null),
				newItem(101711, "COKLTE330X24", "330MLALCNN1X24 COKE LT", 24, 510.00, 498.00, null),
				newItem(101703, "COKZER330X24", "330MLALCNN1X24 COKE ZERO", 24, 492.00, 480.00, null),
				newItem(101635, "RTO2X8", "2LPLBTN1X8 ROYAL TRU ORG", 8, 396.00, 384.00, null),
				newItem(101636, "SPR2X8", "2LPLBTN1X8 SPRITE", 8, 396.00, 384.00, null),
				newItem(101637, "COKZER2X8", "2LPLBTN1X8 COKE ZERO", 8, 396.00, 384.00, null),
				newItem(101640, "COK2X8", "2LPLBTN1X8 COKE", 8, 396.00, 384.00, null),
				newItem(101651, "RTO1.5X12", "1.5LPLBTN1X12 ROYAL TRU ORG CNTR", 12, 522.00, 510.00, null),
				newItem(101653, "SPR1.5X12", "1.5LPLBTN1X12 SPRITE", 12, 522.00, 510.00, null),
				newItem(101655, "COKZER1.5X12", "1.5LPLBTN1X12 COKE ZERO", 12, 522.00, 510.00, null),
				newItem(103652, "COK1.75X12", "1.75LPLBTN1X12 COKE", 12, 522.00, 510.00, null),
				newItem(101659, "COKLTE1.5X24", "1.5LPLBTN1X12 COKE LT", 12, 522.00, 510.00, null),
				newItem(101650, "COK1.5X12", "1.5LPLBTN1X12 COKE", 12, 522.00, 510.00, null),
				newItem(105428, "COK1.75X6", "1.75LPLBTN1X6 COKE", 6, 261.00, 255.00, null),
				newItem(104630, "SPRPRO1.5X6", "1.5LPLBTN1X6 SPRITE PROM", 6, 261.00, 255.00, null),
				newItem(104632, "RTOPRO1.5X6", "1.5LPLBTN1X6 ROYAL TRU ORG PROM", 6, 261.00, 255.00, null),
				newItem(105429, "COKPRO1.75X2", "1.75LPLBTN1X2 COKE PROM", 2, 87.00, 86.00, null),
				newItem(101667, "COK500X24", "500MLPLBTN1X24 COKE", 24, 525.00, 513.00, null),
				newItem(101668, "SPR500X24", "500MLPLBTN1X24 SPRITE", 24, 525.00, 513.00, null),
				newItem(101669, "RTO500X24", "500MLPLBTNX24 ROYAL TRU ORG", 24, 525.00, 513.00, null),
				newItem(101677, "COKZER500X24", "500MLPLBTNX24 COKE ZERO", 24, 525.00, 513.00, null),
				newItem(101606, "MMPORG1X12", "1LPLBTN1X12 MM PULPY ORG", 12, 598.00, 586.00, null),
				newItem(101698, "MMPORG330X24", "330MLPLBTN1X24 MM PULPY ORG", 24, 518.00, 506.00, null),
				newItem(105703, "MMPM/O330X12", "330MLPLBTN1X12 MM PLPY MANG ORG (NC)", 12, 259.00, 254.00, null),
				newItem(102002, "WILDIS500X24", "500MLPLBTN1X24 WILKINS DISTILLED WT PROM", 24, 365.00, 353.00, null),
				newItem(102007, "WILDIS6X3", "6LPLBTN1X3 WILKINS DISTILLED WTR PROM", 3, 230.00, 218.00, null),
				newItem(102008, "WILDIS4X4", "4LPLBTN1X4 WILKINS DISTILLED WTR PROM", 4, 233.00, 221.00, null),
				newItem(102237, "POPULT800X12", "800MLGLDTR1X12 POP COLA ULTR", 12, 138.00, 126.00, 299),
				newItem(102407, "RTOULT237X24", "237MLGLBTR1X24 ROYAL TRU ORG", 24, 144.00, 132.00, 147),
				newItem(102408, "SPRULT237X24", "237MLGLBTR1X24 SPRITE ULTR", 24, 144.00, 132.00, 147),
				newItem(102491, "RTOULT355X24", "355MLGLBTR1X24 ROYAL TRU ORG ULTR", 24, 192.00, 180.00, 156),
				newItem(102492, "SPRULT355X24", "355MLGLBTR1X24 SPRITE ULTR", 24, 192.00, 180.00, 156),
				newItem(102576, "SPALMN240X24", "240MLGLBTR1X24 SPARKLE LMN ULTR", 24, 144.00, 132.00, 333),
				newItem(102634, "COKULT355X24", "355MLGLBTR1X24 COKE ULTR", 24, 192.00, 180.00, 156),
				newItem(103913, "MMPFOR330X24", "330MLPLBTN1X24 MM PLPY FOURSEASONS", 24, 518.00, 506.00, null),
				newItem(103914, "MMPFOR1X12", "1LPLBTN1X12 MM PLPY FOURSEASONS", 12, 598.00, 586.00, null),
				newItem(104200, "WILPUR500X24", "500MLPLBTN1X24 WILKINS PURE", 24, 250.00, 238.00, null),
				newItem(106117, "WILLWT500X24", "500MLPLBTN1X24 WILKINSPURE LTWT", 24, 250.00, 238.00, null),
				newItem(104201, "WILPUR1X12", "1LPLBTN1X12 WILKINS PURE", 12, 240.00, 228.00, null),
				newItem(104474, "RGBS/R237X24", "237ML RGB X SPRITE12 RT012", 24, 144.00, 132.00, 147),
				newItem(104475, "RGBCRS750X12", "750ML RGB X COKE6 RT03 SPRITE3", 12, 168.00, 156.00, 156),
				newItem(104600, "COKPRO355X24", "355MLGLBTR1X24 COKE PROM", 24, 192.00, 180.00, 156),
				newItem(104608, "RTOPRO355X24", "355MLGLBTR1X24 ROYAL TRU ORG PROM", 24, 192.00, 180.00, 156),
				newItem(104609, "SPRPRO355X24", "355MLGLBTR1X24 SPRITE PROM", 24, 192.00, 180.00, 156),
				newItem(104784, "WILPUR330X30", "330MLPLBTN1X30 WILKINS PURE", 30, 210.00, 200.00, null),
				newItem(104793, "COK250X12", "250MLPLBTN1X12 COKE", 12, 108.00, 103.00, null),
				newItem(104866, "WILPUR330X15", "330MLPLBTN1X15 WILKINS PURE", 15, 105.00, 100.00, null),
				newItem(104892, "SPR400X12", "400MLPLBTN1X12 SPRITE", 12, 140.00, 125.00, null),
				newItem(104896, "COK400X12", "400MLPLBTN1X12 COKE", 12, 140.00, 125.00, null),
				newItem(104955, "RTO250X12", "250MLPLBTN1X12 ROYAL TRU ORG", 12, 108.00, 103.00, null),
				newItem(104956, "SPR250X12", "250MLPLBTN1X12 SPRITE", 12, 108.00, 103.00, null),
				newItem(105147, "RGBS/R355X24", "355ML RGB X SPRITE12 ROYAL 12 ULTRA", 24, 192.00, 180.00, 156),
				newItem(105311, "SAR250X12", "250MLPLBTN1X12 SARSI ROOT BEER", 12, 108.00, 103.00, null),
				newItem(105329, "COKTPR237X24", "237MLGLBTR1X24 COKE TLPR", 24, 112.00, 100.00, 147),
				newItem(105331, "COKTAL237X24", "237MLGLBTR1X24 COKE TALL", 24, 112.00, 100.00, 147),
				newItem(105343, "MMFORG800X12", "800MLPLBTN1X12 MM FRSH ORG (NC) PRIM", 12, 252.00, 242.00, null),
				newItem(105346, "MMFORG250X12", "250MLPLBTN1X12 MM FRSH ORG (NC) PRIM", 12, 108.00, 103.00, null),
				newItem(105909, "SPRLBR250X12", "250MLPLBTN1X12 SPRITE LBRNS MX", 12, 108.00, 103.00, null),
				newItem(105953, "RLFSWT250X12", "250MLPLBTN1X12 REAL LEAF FRTCY SWT TEA", 12, 96.00, 91.00, null),
				newItem(105947, "POW250X12", "250MLPLBTN1X12 PADE MT BRY", 12, 96.00, 91.00, null),
				newItem(105330, "COKTPL237X24", "237MLGLBTR1X24 COKE TLPL", 24, 112.00, 100.00, 147),
				newItem(103958, "RLFH/L480X24", "480MLPLBTN1X24 REAL LEAF HONLYCH G PRIM", 24, 430.00, 418.00, null),
				newItem(104798, "RGBSRL237X24", "237 RET X 24 ROYAL RAINBOW CASE", 24, 144.00, 132.00, 147),
				newItem(105314, "RGBCRS355X24", "355 RGBX COKE12 SPRITE6 ROYAL6", 24, 192.00, 180.00, 156),
				newItem(105394, "MMFORG800X6", "800MLPLBTN1X6MM FRSH ORG (NC) PRIM", 6, 126.00, 121.00, null),
				newItem(104611, "SPRPLC355X24", "355MLGLBTR1X24 SPRITE PLCR", 24, 192.00, 180.00, 156),
				newItem(104603, "COKPLC355X24", "355MLGLBTR1X24 COKE PLCR", 24, 192.00, 180.00, 156),
				newItem(105892, "EOCMGO30X12", "30GMSACHN1X12 EIGHT OCLCKLMNACTMANG PROM", 12, 101.80, 100.60, null),
				newItem(105895, "EOCPIN30X12", "30GMSACHN1X12 EIGHT OCLCKLMNACTPINE PROM", 12, 101.80, 100.60, null),
				newItem(105672, "EOCORG30X12", "30GMSACHN1X12 EIGHT OCLCKIMMACTORG PROM", 12, 101.80, 100.60, null),
				newItem(105896, "EOCO/M30X12", "30GMSACHN1X12 EIGHT OCLCKLMACTORGMNG PROM", 12, 101.80, 100.60, null),
				newItem(105949, "EOCOMF30X12", "30GMSACHN1X12 EIGHT OCLCKIMMACTORG FREE", 12, 0.00, 0.00, null),
				newItem(105718, "EOCMGO35X12", "35GMSSACHN1X12 EIGHT OCLCKLMNACTMANG PROM", 12, 101.80, 100.60, null),
				newItem(105720, "EOCPON35X12", "35GMSSACHN1X12 EIGHT OCLCKLMNACTPON PROM", 12, 101.80, 100.60, null),
				newItem(101630, "POPULT240X24", "240MLGLBTR1X24 POP COLA ULTR", 24, 108.00, 96.00, 180),
				newItem(104599, "POPULP240X24", "240MLGLBTR1X24 POP COLA PROM", 24, 108.00, 96.00, 180),
				newItem(101319, "WILDIS18.9X1", "18.9LPLJGR1X1 WILKINS DISTILLED WTR", 1, 150.00, 138.00, null),
				newItem(104529, "COKPLC750X12", "750MLGLBTR1X12 COKE PLCR", 12, 168.00, 156.00, 304),
				newItem(104528, "SPRPLC750X12", "750MLGLBTR1X12 SPRITE PLCR", 12, 168.00, 156.00, 678),
				newItem(102003, "WILPRO1X12", "1LPLBTN1X12 WILKINS DISTILLED WTR PROM", 12, 280.00, 268.00, null),
				newItem(106125, "WILLTW1X12", "1LPLBTN1X12 WILKINS DISTILLED WTR LTWT", 12, 280.00, 268.00, null),
				newItem(102648, "SARULT240X24", "240MLGLBTR1X24 SARSI ROOT BEER ULTR", 24, 155.00, 143.00, 180),
				newItem(105506, "SPALMN300X12", "300MLPLBTN1X12 SPARKLE LMN", 12, 108.00, 103.00, null),
				newItem(106148, "COKFRU250X24", "250NRPX36 COKE24 FRUITCY12", 24, 312.00, 297.00, null),
				newItem(106149, "COKMTB250X24", "250NRPX36 COKE24 MTBERRY12", 24, 312.00, 297.00, null),
				newItem(104895, "COKPRO1X12", "1LGLBTR1X12 COKE PROM", 12, 222.00, 210.00, null),
				newItem(104897, "COKPLC1X12", "1LGLBTR1X12 COKE PLCR", 12, 222.00, 210.00, null),
				newItem(106272, "COKK/L175X24", "750 RET COKE12 1.0 RET COKE12 250 COKE12", 24, 390.00, 361.00, null),
				newItem(106268, "COKM/W250X48", "250 COKE48 330WLKNSPUREOLD330", 48, 432.00, 400.00, null));
	}

	private ItemEntity newItem(Integer id, String name, String description, int qtyPerCase, double sellingPrice,
			double purchasePrice, Integer emptiesId) {
		ItemEntity i = new ItemEntity();
		i.setVendorId(id.toString());
		i.setName(name);
		i.setDescription(description);
		i.setQtyPerUomList(newQtyPerUomList(i, qtyPerCase));
		i.setPriceList(newPriceList(sellingPrice, purchasePrice));
		i.setEmpties(getEmpties(emptiesId));
		i.setType(ItemType.PURCHASED);
		return i;
	}

	private List<QtyPerUomEntity> newQtyPerUomList(ItemEntity item, int qtyPerCase) {
		return Arrays.asList( //
				newQtyPerUom(item, 1, UomType.EA), //
				newQtyPerUom(item, qtyPerCase, UomType.CS) //
		);
	}

	private QtyPerUomEntity newQtyPerUom(ItemEntity item, int qty, UomType uom) {
		QtyPerUomEntity q = new QtyPerUomEntity();
		q.setItem(item);
		q.setUom(uom);
		q.setQty(new BigDecimal(qty));
		q.setPurchased(uom == UomType.CS);
		q.setSold(uom == UomType.CS);
		q.setReported(uom == UomType.CS);
		return q;
	}

	private List<PriceEntity> newPriceList(double sellingPrice, double purchasePrice) {
		return Arrays.asList( //
				newPrice(sellingPrice, dealer), //
				newPrice(purchasePrice, purchase) //
		);
	}

	private PriceEntity newPrice(double price, PricingTypeEntity type) {
		PriceEntity p = new PriceEntity();
		p.setType(type);
		p.setPriceValue(new BigDecimal(price));
		p.setStartDate(goLiveDate());
		p.setDecidedBy(SYSGEN);
		p.setDecidedOn(goLiveTimestamp());
		return p;
	}

	private ZonedDateTime goLiveTimestamp() {
		return DateTimeUtils.toZonedDateTime(goLiveDate());
	}

	private LocalDate goLiveDate() {
		return DateTimeUtils.toDate(goLive);
	}

	private ItemEntity getEmpties(Integer id) {
		return id == null ? null : itemRepository.findByVendorId(id.toString());
	}

	private void setRoutes() {
		e3cg = newRoute("E3CG", "PS1");
		e3ch = newRoute("E3CH", "PS2");
		e3ci = newRoute("E3CI", "PS3");
		e3cj = newRoute("E3CJ", "PS4");
		e3ck = newRoute("E3CK", "PS5");
	}

	private RouteEntity newRoute(String name, String seller) {
		RouteEntity r = new RouteEntity();
		r.setName(name);
		r.setSellerHistory(newAccount(seller));
		return routeRepository.save(r);
	}

	private List<AccountEntity> newAccount(String seller) {
		AccountEntity a = new AccountEntity();
		a.setSeller(seller);
		a.setStartDate(goLiveDate());
		return Arrays.asList(a);
	}

	private List<CustomerEntity> customers() {
		return Arrays.asList( //
				newOutlet(503746808, "2544 STORE", e3cg), //
				newOutlet(503746953, "3K STORE", e3ck), //
				newOutlet(503747162, "888 STORE", e3cj), //
				newOutlet(503747201, "A.M. EATERY", e3ch), //
				newOutlet(503747217, "A-24 MINI GROCERY", e3cg), //
				newOutlet(503747238, "ABA SARI SARI STORE", e3cg), //
				newOutlet(503747281, "ABES STORE", e3cj), //
				newOutlet(503747379, "ADAN STORE", e3cg), //
				newOutlet(503747698, "AIZEL KAYE STORE", e3cg), //
				newOutlet(503747623, "AIJO STORE", e3ck), //
				newOutlet(503747831, "ALCAPAL STORE", e3ck), //
				newOutlet(503747911, "ALEXINE STORE", e3ck), //
				newOutlet(503747912, "ALEXIS BAKERY", e3ch), //
				newOutlet(503747925, "ALFRED STORE", e3cg), //
				newOutlet(504606047, "ALFIE & ROWENA STORE", e3cg), //
				newOutlet(503748295, "ALLOY STORE", e3ck), //
				newOutlet(503748407, "ALVIN STORE", e3cg), //
				newOutlet(503748530, "AMMY STORE", e3ch), //
				newOutlet(503748713, "ANDDYS STORE", e3cj), //
				newOutlet(504432704, "ARIOLA NELGEN LUZARA", e3ck), //
				newOutlet(503749662, "ASHA STORE", e3ck), //
				newOutlet(503751264, "BLESS SARI SARI STORE", e3ch), //
				newOutlet(503750019, "BABY PANDA STORE", e3cg), //
				newOutlet(503750237, "BALBIN EATERY", e3cg), //
				newOutlet(503750579, "BELEN CARENDERIA", e3cg), //
				newOutlet(503750892, "BEST FRIEND", e3cg), //
				newOutlet(503750913, "BETH BAKERY", e3cj), //
				newOutlet(503750926, "BETH STORE(3CH)", e3ch), //
				newOutlet(503750934, "BETH STORE(3CK)", e3ck), //
				newOutlet(503751022, "BETTRY STORE", e3cg), //
				newOutlet(503751360, "BOMBERO STORE", e3cj), //
				newOutlet(503751484, "BOTIKANG BARANGAY AT SARI SARI STORE", e3cg), //
				newOutlet(503752200, "CAROLINA STORE", e3cg), //
				newOutlet(503752446, "CELY STORE", e3ch), //
				newOutlet(503752363, "CECILE STORE", e3ck), //
				newOutlet(504877694, "CHE'S STORE", e3ck), //
				newOutlet(503752543, "CHAREMEL SARI-SARI STORE", e3ch), //
				newOutlet(503753068, "C-JR TAPSI TRIP FOOD HAUZ", e3cg), //
				newOutlet(503753244, "COMIAS BAKERY", e3cg), //
				newOutlet(503753258, "CONCHING SARI SARI STORE", e3cg), //
				newOutlet(503753300, "CONNIE STORE", e3cj), //
				newOutlet(503933776, "CRISDAN STORE", e3ck), //
				newOutlet(503753608, "CRISTINA STORE", e3cg), //
				newOutlet(503752934, "CHRISMA STORE", e3cg), //
				newOutlet(503753723, "CUEVAS BAKERY(3CJ)", e3cj), //
				newOutlet(504479809, "CUEVAS BAKERY(3CK)", e3ck), //
				newOutlet(503753772, "CYNTHIA STORE", e3ci), //
				newOutlet(503753825, "D SISTERS", e3ci), //
				newOutlet(503754145, "DE ASIA STORE", e3ci), //
				newOutlet(503754670, "D-JELL STORE", e3ch), //
				newOutlet(503754508, "DIANHIRANG STORE", e3ck), //
				newOutlet(503754865, "DORA STORE", e3cg), //
				newOutlet(503755035, "E CUEBAS BAKERY", e3cj), //
				newOutlet(503755109, "EBIANG STORE", e3cj), //
				newOutlet(503755157, "EDDIE STORE", e3cj), //
				newOutlet(503755595, "EMILY EATERY", e3cg), //
				newOutlet(503755844, "ELVINS MIREI STORE", e3cg), //
				newOutlet(503755864, "ELY STORE", e3ci), //
				newOutlet(503755582, "ELIJAH STORE", e3ch), //
				newOutlet(503756018, "EMMA STORE(3CJ)", e3cj), //
				newOutlet(503756020, "EMMA STORE(3CG)", e3cg), //
				newOutlet(503756476, "ESTEVES STORE", e3cj), //
				newOutlet(503756477, "ESTHER EATERY", e3ci), //
				newOutlet(503755039, "E Q BAKERY", e3ch), //
				newOutlet(503756717, "F AND F", e3ch), //
				newOutlet(503757101, "FEL-CON ENTERPRISES", e3ch), //
				newOutlet(503757213, "FERGEM STORE", e3ch), //
				newOutlet(503757301, "FIRST NEPTUNE", e3cj), //
				newOutlet(503757526, "FRANKS'S STORE", e3ch), //
				newOutlet(503757537, "FRANZINE STORE", e3ci), //
				newOutlet(503757684, "GABRIEL STORE", e3cg), //
				newOutlet(503757929, "GEMS STORE", e3cj), //
				newOutlet(503757980, "GERALDINE BAKERY", e3cg), //
				newOutlet(503758116, "GIL STORE", e3cj), //
				newOutlet(503758473, "GOLDEN BREAD BAKERY", e3ck), //
				newOutlet(503758485, "GOLDEN SON HOUZ", e3cg), //
				newOutlet(503758699, "GUINTO STORE", e3ch), //
				newOutlet(503758813, "HAPINOY PHARMACY", e3ch), //
				newOutlet(503758814, "HAPON STORE", e3ck), //
				newOutlet(503758894, "HEAVEN STORE", e3cg), //
				newOutlet(503759376, "INA STORE/EATERY", e3cg), //
				newOutlet(503759306, "ILLUMINA DRUGSTORE", e3ch), //
				newOutlet(503759670, "J MAGS STORE", e3cg), //
				newOutlet(503759825, "JAGORING STORE", e3ck), //
				newOutlet(503759842, "JAIRA STORE", e3cg), //
				newOutlet(503901657, "JAIHO STORE", e3ck), //
				newOutlet(503760351, "JEFF STORE", e3cj), //
				newOutlet(503760622, "JESSICA STORE", e3cj), //
				newOutlet(503760766, "JIECELS STORE", e3cj), //
				newOutlet(503760771, "JILIET STORE", e3cj), //
				newOutlet(503760995, "JNK STORE", e3ci), //
				newOutlet(503761282, "JOJI'S GEN. STORE", e3cg), //
				newOutlet(503761290, "JOJO STORE", e3cg), //
				newOutlet(503761403, "JONA'S STORE", e3cj), //
				newOutlet(503761418, "JONES VARIETY STORE", e3cg), //
				newOutlet(503761785, "JOYNA'S STORE", e3cg), //
				newOutlet(503761561, "JOSHUA STORE", e3cg), //
				newOutlet(503761285, "JOJO AND MALOU STORE", e3cg), //
				newOutlet(503760720, "JHO-A STORE", e3ck), //
				newOutlet(503761824, "JR STORE", e3cj), //
				newOutlet(503762287, "KAI MICHAEL CARENDERIA", e3ck), //
				newOutlet(503762333, "KAINAN SA KANTO", e3ch), //
				newOutlet(503762589, "KEECE STORE", e3ch), //
				newOutlet(503762626, "KENIS SARI SARI STORE", e3ci), //
				newOutlet(503762755, "KIM AND KURT STORE", e3cj), //
				newOutlet(503763083, "KYLE'S STORE", e3cj), //
				newOutlet(503763537, "LEGASPI STORE", e3ci), //
				newOutlet(503763637, "LENNY STORE", e3ci), //
				newOutlet(503763801, "LETTY STORE", e3cg), //
				newOutlet(503764422, "LIZA STORE", e3cg), //
				newOutlet(503763930, "LIGAYA BAKERY", e3ck), //
				newOutlet(503764899, "LOURDES STORE", e3cg), //
				newOutlet(503839793, "LUCKY BEE", e3ck), //
				newOutlet(503765005, "LT STORE", e3ci), //
				newOutlet(503765520, "M3M EATERY", e3ck), //
				newOutlet(503765626, "MADEL STORE", e3ck), //
				newOutlet(503765915, "MAMAYS STORE", e3cj), //
				newOutlet(503766123, "MAPALAD BAKERY", e3cg), //
				newOutlet(503766146, "MAR-AN BAKE SHOP", e3cg), //
				newOutlet(503766357, "MARICEL STORE", e3ch), //
				newOutlet(503766418, "MARIE STORE", e3cg), //
				newOutlet(503766607, "MARIS STORE", e3ck), //
				newOutlet(503766930, "MARRY CHERISE", e3cj), //
				newOutlet(503766206, "MARGARETH STORE", e3cg), //
				newOutlet(503767719, "MERCY STORE", e3cg), //
				newOutlet(503767671, "MENGGIE STORE", e3ck), //
				newOutlet(503768215, "MILA EATERY", e3ci), //
				newOutlet(503768858, "MOPPHEN STORE", e3ch), //
				newOutlet(503768715, "MOMMY STORE", e3ck), //
				newOutlet(503769772, "NICO STORE", e3cg), //
				newOutlet(503769864, "NIDEN-J STORE", e3cj), //
				newOutlet(503769484, "NENE STORE", e3cg), //
				newOutlet(503769366, "NECKIE ESPESYAL PALABOK", e3cg), //
				newOutlet(503770480, "OCEAN PARK STORE", e3ch), //
				newOutlet(503842532, "OVI STORE", e3ck), //
				newOutlet(503770772, "PAGDONZALAN STORE", e3cg), //
				newOutlet(503770853, "PANADEIA DE MADREMARIA", e3cj), //
				newOutlet(503771123, "PAUREES STORE", e3cj), //
				newOutlet(503838180, "PENYO GEN. MDSE", e3ck), //
				newOutlet(503771628, "PREMIUM STORE", e3cg), //
				newOutlet(503771738, "PURE BEST STORE", e3cg), //
				newOutlet(503771671, "PRINCESS DIANE STORE", e3cg), //
				newOutlet(503772004, "RAMAS BAKERY", e3ck), //
				newOutlet(504762683, "RAINE'S STORE", e3ck), //
				newOutlet(503773439, "REIGN BLESS BAKERY", e3ch), //
				newOutlet(503774396, "RRJM STORE", e3ck), //
				newOutlet(504491845, "ROSE ANN EATERY & STORE", e3ck), //
				newOutlet(503747182, "A ANON DRUG STORE", e3ch), //
				newOutlet(503774419, "RSM STORE", e3cj), //
				newOutlet(503774438, "TINDAHAN NI JOSE", e3ch), //
				newOutlet(503774792, "SAMSON STORE", e3cg), //
				newOutlet(503775016, "SENY STOP HAMBURGER", e3cj), //
				newOutlet(503775277, "SIAK LABAT STORE", e3cg), //
				newOutlet(503775077, "SHAMMES STORE", e3cj), //
				newOutlet(503775342, "SINKO KATORSE MECHANDIZING", e3cj), //
				newOutlet(503775457, "SMILE GEN. MDSE.", e3ch), //
				newOutlet(503775509, "SOL STORE", e3cg), //
				newOutlet(503775536, "SOMERA STORE", e3ck), //
				newOutlet(503775697, "STARLIGAT STORE", e3ch), //
				newOutlet(503775715, "STELLA STORE", e3cg), //
				newOutlet(503775742, "STIANGELA STORE", e3ci), //
				newOutlet(503776010, "T.N.B. STORE", e3cg), //
				newOutlet(503776719, "TINA STORE", e3cg), //
				newOutlet(503776891, "TJR STORE", e3cg), //
				newOutlet(503776916, "TOLITS EATERY", e3cg), //
				newOutlet(503776949, "TONGLAN STORE", e3cj), //
				newOutlet(503776993, "TOP J SARI SARI STORE", e3ck), //
				newOutlet(503777529, "VENTURA STORE", e3ch), //
				newOutlet(503777815, "VINCE EATERY", e3cg), //
				newOutlet(503777774, "VILMA STORE", e3cg), //
				newOutlet(503778037, "VTC GEN.MDSE", e3cg), //
				newOutlet(503778077, "WANSON STORE", e3ci), //
				newOutlet(503778079, "WAREN STORE", e3ck), //
				newOutlet(503778412, "YANNAS STORE", e3cj), //
				newOutlet(504107365, "ZAMORA EATERY", e3ck), //
				newOutlet(503838181, "2M BAKERY AND SARISARI ", e3ch), //
				newOutlet(503880977, "AM STORE", e3cg), //
				newOutlet(503842528, "CHECHE STORE", e3cg), //
				newOutlet(503842493, "DANZOVEN STORE", e3cg), //
				newOutlet(503841872, "HOLY FAMILY PARISH MPC", e3ck), //
				newOutlet(503880974, "MERLYMAR EATERY", e3cg), //
				newOutlet(503842491, "NINA STORE", e3cg), //
				newOutlet(503880669, "PAPUS CORNER STORE", e3ch), //
				newOutlet(503880675, "VIDAL STORE", e3ch), //
				newOutlet(503931983, "148 STORE", e3cg), //
				newOutlet(503932976, "ARIEL JOY TAPSI B ", e3ci), //
				newOutlet(503933164, "ALING LUDING STORE", e3cg), //
				newOutlet(503931879, "ALING ANCHING STORE", e3cg), //
				newOutlet(503931997, "ADELAINE STORE", e3ci), //
				newOutlet(503900649, "CHIEYIENNE STORE", e3cg), //
				newOutlet(503931981, "CJMK TRADING", e3ch), //
				newOutlet(503907248, "DIDAY STORE", e3cg), //
				newOutlet(503931864, "FLORA STORE", e3ci), //
				newOutlet(503931945, "FAMILY STORE", e3ck), //
				newOutlet(503931972, "JCC GEN.MDSE", e3cg), //
				newOutlet(503932943, "IRENE STORE", e3ch), //
				newOutlet(503931420, "LABIAL PHARMACY", e3ci), //
				newOutlet(503931965, "MINDA EATERY(3CK)", e3ck), //
				newOutlet(503900659, "PERJAN FOOD HOUSE", e3cg), //
				newOutlet(503926144, "ROSAVIE STORE", e3ci), //
				newOutlet(503931979, "SALLY STORE", e3ci), //
				newOutlet(503900660, "SILVESTRE STORE", e3cg), //
				newOutlet(503931937, "TONIROSE", e3ck), //
				newOutlet(503900658, "UNO STORE", e3cg), //
				newOutlet(504107341, "FLORENCIO CANTEEN", e3ci), //
				newOutlet(504129589, "JAELOU EATERY", e3ch), //
				newOutlet(504101850, "DJH BAKESHOP", e3ch), //
				newOutlet(504107368, "HELEN & RONA EATERY", e3cg), //
				newOutlet(504107391, "MELINDA EATERY", e3cg), //
				newOutlet(504115738, "ARDABIE EATERY", e3cg), //
				newOutlet(504124334, "MICHAEL STORE", e3ch), //
				newOutlet(504235426, "ICL ", e3ci), //
				newOutlet(504259858, "YOU AND I EATERY", e3ch), //
				newOutlet(504448585, "A & V STORE AND TAPSILOGAN", e3ch), //
				newOutlet(504404283, "VILMAR STORE", e3ck), //
				newOutlet(504408512, "JOY'S STORE", e3cj), //
				newOutlet(504448611, "JEPRIL STORE", e3ck), //
				newOutlet(504408553, "SUSAN STORE", e3cg), //
				newOutlet(504408554, "ALLAN STORE", e3cg), //
				newOutlet(504408557, "RENE CARINDERIA", e3ci), //
				newOutlet(504408560, "LUGAWAN", e3cg), //
				newOutlet(504408563, "KEN'S BAKERY", e3cg), //
				newOutlet(504408565, "HUMBLE STORE", e3cg), //
				newOutlet(504410792, "YOUR NEIGHBORHOOD", e3ch), //
				newOutlet(504411756, "RO-NA DRUG AND GENERAL MDSE", e3cj), //
				newOutlet(504411759, "TTM STORE", e3cj), //
				newOutlet(504423619, "FLOUR BAKESHOP", e3cj), //
				newOutlet(504423621, "MAESTRA'S SARI SARI STORE", e3cj), //
				newOutlet(504423984, "CHENOK'S EATERY", e3ck), //
				newOutlet(504423986, "LUCKY CHOY", e3ck), //
				newOutlet(504426447, "BELLA EATERY", e3ck), //
				newOutlet(504428318, "TESS STORE", e3cj), //
				newOutlet(504428825, "MEKHAM STORE", e3ci), //
				newOutlet(504451112, "MV CUEVAS BAKERY AND EATERY", e3ch), //
				newOutlet(504490326, "JIM DRUG & GEN", e3ck), //
				newOutlet(504425768, "USMAND GENERAL MDSE", e3ci), //
				newOutlet(504549747, "KOKIKS FOOD STATION", e3ch), //
				newOutlet(504626966, "ALDRIN'S STORE", e3ck), //
				newOutlet(504627641, "MINDA EATERY(3CI)", e3ci), //
				newOutlet(504649368, "JAZEL STORE", e3cg), //
				newOutlet(504630858, "JOY STORE", e3ci), //
				newOutlet(504630870, "SONY'S STORE", e3ci), //
				newOutlet(504630874, "LAURA STORE", e3ci), //
				newOutlet(504649332, "3'S ATENGS", e3cg), //
				newOutlet(504650923, "EIGYERS STORE", e3ch), //
				newOutlet(504650979, "PALANAS STORE", e3ch), //
				newOutlet(504650980, "MEL STORE", e3cg), //
				newOutlet(504660554, "ALING MELING STORE", e3ci), //
				newOutlet(504660558, "DOROTEA STORE", e3cj), //
				newOutlet(504666957, "FLORENCIO BAKERY 2", e3ci), //
				newOutlet(504666967, "HABIBI STORE", e3ci), //
				newOutlet(504696211, "RICH GOAL", e3cg), //
				newOutlet(504697319, "PRIMA STORE", e3ck), //
				newOutlet(504650981, "NORMA STORE", e3cg), //
				newOutlet(504765291, "BLEZA STORE", e3cg), //
				newOutlet(504702510, "FLORENCIO BAKERY 1", e3ci), //
				newOutlet(504710384, "KARLKEN STORE", e3ch), //
				newOutlet(504768710, "RIVERA BOTIKA", e3ch), //
				newOutlet(504710910, "GOT GOLD EATERY", e3cg), //
				newOutlet(504717525, "RHIZA KITCHENETTE", e3ch), //
				newOutlet(504717526, "BUY N SAVE", e3ch), //
				newOutlet(504721051, "SHAKE STORE", e3ci), //
				newOutlet(504721054, "GTAM ENTERPRISE", e3ci), //
				newOutlet(504721060, "AG-EL BAKERY", e3ck), //
				newOutlet(504721133, "GARAJE EATERY", e3cg), //
				newOutlet(504725985, "ZULUETA STORE", e3ch), //
				newOutlet(504784877, "MARY'S STORE", e3ci), //
				newOutlet(504727831, "MELJI'S STORE", e3ch), //
				newOutlet(504747872, "ERIC MARANAN", e3cg), //
				newOutlet(504758674, "EMERALD'S STORE", e3ch), //
				newOutlet(504778817, "ADA STORE", e3ck), //
				newOutlet(504769733, "ANN STORE", e3cg), //
				newOutlet(504782127, "BELINA OCAMPO STORE", e3cg), //
				newOutlet(504767938, "CHAIRMAN EBONG", e3ch), //
				newOutlet(504758680, "GUIA BURGER", e3ch), //
				newOutlet(504759804, "JASRICK FOOD TRIP", e3cg), //
				newOutlet(504765879, "JHAY AR STORE", e3cg), //
				newOutlet(504759802, "MARICEL EATERY", e3cg), //
				newOutlet(504782124, "MD/RD", e3cg), //
				newOutlet(504762684, "KENNA'S STORE", e3ck), //
				newOutlet(504759877, "STA ANA SUPER STORE", e3cg), //
				newOutlet(504762392, "RIVERA STORE", e3ch), //
				newOutlet(504762829, "ROSE STORE", e3cj), //
				newOutlet(504765872, "IYA'S STORE", e3ck), //
				newOutlet(504788865, "MELCHOR DTORE", e3cg), //
				newOutlet(504736501, "TINDAHAN NI BATANG", e3ch), //
				newOutlet(504882613, "ZOE MAE STORE", e3ch), //
				newOutlet(504886279, "VHEK'S STORE", e3ch), //
				newOutlet(504428799, "VINCENT STORE", e3ck), //
				newOutlet(504889399, "BOBBY ANN'S STORE", e3cj), //
				newOutlet(504903060, "JM CARINDERIA ", e3ch) //
		);
	}

	private CustomerEntity newOutlet(long id, String name, RouteEntity route) {
		CustomerEntity c = new CustomerEntity();
		c.setVendorId(id);
		c.setName(name);
		c.setRouteHistory(newRouting(route));
		c.setPrimaryPricingType(dealer);
		c.setType(PartnerType.OUTLET);
		return c;
	}

	private List<RoutingEntity> newRouting(RouteEntity route) {
		RoutingEntity r = new RoutingEntity();
		r.setRoute(route);
		r.setStartDate(goLiveDate());
		return Arrays.asList(r);
	}

	private SyncEntity sync() {
		SyncEntity s = new SyncEntity();
		s.setLastSync(DateTimeUtils.toUtilDate("2009-04-09"));
		s.setType(SyncType.VERSION);
		return s;
	}
}
