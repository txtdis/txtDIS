package ph.txtdis.mgdc.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.app.AbstractTableApp;
import ph.txtdis.dto.Holiday;
import ph.txtdis.mgdc.fx.table.HolidayTable;
import ph.txtdis.mgdc.service.HolidayService;

@Scope("prototype")
@Component("holidayApp")
public class HolidayApp extends AbstractTableApp<HolidayTable, HolidayService, Holiday> {
}
