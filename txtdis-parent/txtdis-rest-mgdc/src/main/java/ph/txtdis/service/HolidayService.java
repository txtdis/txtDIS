package ph.txtdis.service;

import java.sql.Date;
import java.util.List;

import ph.txtdis.dto.Holiday;

public interface HolidayService extends IdService<Holiday, Long> {

	Holiday findByDate(Date d);

	List<Holiday> list();
}