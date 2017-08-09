package ph.txtdis.service;

import ph.txtdis.util.DateTimeUtils;

import java.time.LocalDate;
import java.time.ZonedDateTime;

public interface EdmsService {

	default ZonedDateTime goLiveTimestamp() {
		return DateTimeUtils.startOfDay(goLiveDate());
	}

	LocalDate goLiveDate();
}
