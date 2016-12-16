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
import ph.txtdis.domain.SyncEntity;
import ph.txtdis.dto.Warehouse;
import ph.txtdis.repository.HolidayRepository;
import ph.txtdis.repository.SyncRepository;
import ph.txtdis.service.WarehouseService;
import ph.txtdis.type.SyncType;
import ph.txtdis.util.DateTimeUtils;

@Configuration("persistenceConfiguration")
public class PersistenceConfiguration {

	private static final String WAREHOUSE = "HAVANA";

	@Autowired
	private SyncRepository syncRepository;

	@Autowired
	private HolidayRepository holidayRepository;

	@Autowired
	private WarehouseService warehouseService;

	@Value("${vendor.dis.go.live}")
	private String goLive;

	@PostConstruct
	private void start() {
		if (syncRepository.count() == 0)
			try {
				holidayRepository.save(philippineHolidays2017());
				warehouseService.save(warehouse());
				syncRepository.save(sync());
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	private List<HolidayEntity> philippineHolidays2017() {
		return Arrays.asList(//
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

	private LocalDate goLiveDate() {
		return DateTimeUtils.toDate(goLive);
	}

	private ZonedDateTime goLiveTimestamp() {
		return DateTimeUtils.toZonedDateTime(goLiveDate());
	}

	private SyncEntity sync() {
		SyncEntity s = new SyncEntity();
		s.setLastSync(DateTimeUtils.toUtilDate("2009-04-07"));
		s.setType(SyncType.VERSION);
		return s;
	}
}
