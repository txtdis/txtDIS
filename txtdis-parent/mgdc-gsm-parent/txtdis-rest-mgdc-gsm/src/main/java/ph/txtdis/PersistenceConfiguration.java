package ph.txtdis;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import ph.txtdis.domain.HolidayEntity;
import ph.txtdis.domain.PricingTypeEntity;
import ph.txtdis.domain.SyncEntity;
import ph.txtdis.dto.ItemFamily;
import ph.txtdis.dto.PickList;
import ph.txtdis.dto.Warehouse;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;
import ph.txtdis.repository.HolidayRepository;
import ph.txtdis.repository.PricingTypeRepository;
import ph.txtdis.repository.SyncRepository;
import ph.txtdis.service.ImportedBillingService;
import ph.txtdis.service.ImportedChannelService;
import ph.txtdis.service.ImportedCustomerService;
import ph.txtdis.service.ImportedItemFamilyService;
import ph.txtdis.service.ImportedItemService;
import ph.txtdis.service.ImportedLocationService;
import ph.txtdis.service.ImportedRemittanceService;
import ph.txtdis.service.ImportedRouteService;
import ph.txtdis.service.ImportedTruckService;
import ph.txtdis.service.ImportedUserService;
import ph.txtdis.service.PickListService;
import ph.txtdis.service.WarehouseService;
import ph.txtdis.type.SyncType;
import ph.txtdis.util.Code;
import ph.txtdis.util.DateTimeUtils;

@Configuration("persistenceConfiguration")
public class PersistenceConfiguration {

	private static final String EDMS = "eDMS";

	private static final String WAREHOUSE = "HAVANA";

	@Autowired
	private SyncRepository syncRepository;

	@Autowired
	private HolidayRepository holidayRepository;

	@Autowired
	private PricingTypeRepository pricingRepository;

	@Autowired
	private ImportedBillingService billableService;

	@Autowired
	private ImportedChannelService channelService;

	@Autowired
	private ImportedCustomerService customerService;

	@Autowired
	private ImportedItemService itemService;

	@Autowired
	private ImportedItemFamilyService familyService;

	@Autowired
	private ImportedLocationService locationService;

	@Autowired
	private ImportedRouteService routeService;

	@Autowired
	private PickListService pickListService;

	@Autowired
	private ImportedRemittanceService remittanceService;

	@Autowired
	private ImportedTruckService truckService;

	@Autowired
	private ImportedUserService userService;

	@Autowired
	private WarehouseService warehouseService;

	@Value("${vendor.dis.go.live}")
	private String goLive;

	@PostConstruct
	private void start() {
		if (syncRepository.count() == 0)
			try {
				truckService.importAll();
				locationService.importAll();
				userService.importAll();
				routeService.importAll();
				channelService.importAll();
				holidayRepository.save(philippineHolidays2016());
				familyService.importAll();
				pricingRepository.save(pricings());
				warehouseService.save(warehouse());
				itemService.importAll();
				customerService.importAll();
				pickListService.save(dummyPickList());
				billableService.importAll();
				remittanceService.importAll();
				syncRepository.save(sync());
			} catch (NoServerConnectionException | StoppedServerException | FailedAuthenticationException | RestException
					| InvalidException e) {
				e.printStackTrace();
			}
	}

	private List<HolidayEntity> philippineHolidays2016() {
		return Arrays.asList(//
				newHoliday("AQUINO", 2016, 8, 21), //
				newHoliday("HALLOWEEN", 2016, 10, 31), //
				newHoliday("ALL SAINTS", 2016, 11, 1), //
				newHoliday("BONIFACIO", 2016, 10, 30), //
				newHoliday("CHRISTMAS EVE", 2016, 12, 24), //
				newHoliday("CHRISTMAS", 2016, 12, 25), //
				newHoliday("RIZAL", 2016, 12, 30), //
				newHoliday("NEW YEAR'S EVE", 2016, 12, 31));
	}

	private HolidayEntity newHoliday(String name, int year, int month, int day) {
		return new HolidayEntity(name, LocalDate.of(year, month, day));
	}

	private List<PricingTypeEntity> pricings() {
		return Arrays.asList(//
				newPricing("PURCHASE"), //
				newPricing(Code.DEALER), //
				newPricing(Code.RETAIL));
	}

	private PricingTypeEntity newPricing(String name) {
		return new PricingTypeEntity(name);
	}

	private Warehouse warehouse() {
		Warehouse w = new Warehouse();
		w.setName(WAREHOUSE);
		w.setFamily(liquorFamily());
		return w;
	}

	private ItemFamily liquorFamily() {
		return familyService.findByName("LIQUOR");
	}

	private PickList dummyPickList() {
		PickList p = new PickList();
		p.setPickDate(goLiveDate());
		p.setPrintedBy(EDMS);
		p.setPrintedOn(goLiveTimestamp());
		return p;
	}

	private LocalDate goLiveDate() {
		return DateTimeUtils.toDate(goLive);
	}

	private ZonedDateTime goLiveTimestamp() {
		return DateTimeUtils.toZonedDateTime(goLiveDate());
	}

	private SyncEntity sync() {
		SyncEntity s = new SyncEntity();
		s.setLastSync(DateTimeUtils.toUtilDate("2009-03-05"));
		s.setType(SyncType.VERSION);
		return s;
	}
}
