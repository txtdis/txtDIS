package ph.txtdis.service;

import java.sql.Date;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import ph.txtdis.dto.Billable;

public interface StartEndDate {

	Billable findByDate(Date d);

	default ZonedDateTime start(Date d) {
		return d.toLocalDate().atStartOfDay(ZoneId.systemDefault());
	}

	default ZonedDateTime end(Date d) {
		return d.toLocalDate().atStartOfDay(ZoneId.systemDefault()).minusSeconds(1L);
	}
}
