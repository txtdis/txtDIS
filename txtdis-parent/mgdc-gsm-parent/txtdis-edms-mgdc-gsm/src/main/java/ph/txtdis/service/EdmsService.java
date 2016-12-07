package ph.txtdis.service;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import ph.txtdis.util.DateTimeUtils;

public interface EdmsService {

	LocalDate goLiveDate();

	default ZonedDateTime goLiveTimestamp() {
		return DateTimeUtils.startOfDay(goLiveDate());
	}
}
