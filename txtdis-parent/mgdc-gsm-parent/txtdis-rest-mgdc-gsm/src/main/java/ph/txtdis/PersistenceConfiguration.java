package ph.txtdis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import ph.txtdis.domain.SyncEntity;
import ph.txtdis.dto.PickList;
import ph.txtdis.dto.Warehouse;
import ph.txtdis.mgdc.domain.HolidayEntity;
import ph.txtdis.mgdc.domain.PricingTypeEntity;
import ph.txtdis.mgdc.gsm.service.server.*;
import ph.txtdis.mgdc.repository.HolidayRepository;
import ph.txtdis.mgdc.repository.PricingTypeRepository;
import ph.txtdis.mgdc.service.server.WarehouseService;
import ph.txtdis.repository.SyncRepository;
import ph.txtdis.type.SyncType;
import ph.txtdis.util.DateTimeUtils;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import static ph.txtdis.type.PriceType.*;

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
	private CreditedAndDiscountedCustomerService customerService;

	@Autowired
	private ImportedBillingService billableService;

	@Autowired
	private ImportedChannelService channelService;

	@Autowired
	private ImportedItemService itemService;

	@Autowired
	private ImportedLeveledItemFamilyService familyService;

	@Autowired
	private ImportedLocationService locationService;

	@Autowired
	private GsmRouteService routeService;

	@Autowired
	private PickListService pickListService;

	@Autowired
	private GsmRemittanceService remittanceService;

	@Autowired
	private ImportedTruckService truckService;

	@Autowired
	private UserService userService;

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
			} catch (Exception e) {
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

	private List<PricingTypeEntity> pricings() {
		return Arrays.asList(//
			newPricing(PURCHASE.toString()), //
			newPricing(DEALER.toString()), //
			newPricing(RETAIL.toString()));
	}

	private Warehouse warehouse() {
		Warehouse w = new Warehouse();
		w.setName(WAREHOUSE);
		return w;
	}

	private PickList dummyPickList() {
		PickList p = new PickList();
		p.setPickDate(goLiveDate());
		p.setPrintedBy(EDMS);
		p.setPrintedOn(goLiveTimestamp());
		return p;
	}

	private SyncEntity sync() {
		SyncEntity s = new SyncEntity();
		s.setLastSync(DateTimeUtils.toUtilDate("2009-03-05"));
		s.setType(SyncType.VERSION);
		return s;
	}

	private HolidayEntity newHoliday(String name, int year, int month, int day) {
		return new HolidayEntity(name, LocalDate.of(year, month, day));
	}

	private PricingTypeEntity newPricing(String name) {
		PricingTypeEntity e = new PricingTypeEntity();
		e.setName(name);
		return e;
	}

	private LocalDate goLiveDate() {
		return DateTimeUtils.toDate(goLive);
	}

	private ZonedDateTime goLiveTimestamp() {
		return DateTimeUtils.toZonedDateTime(goLiveDate());
	}
}
