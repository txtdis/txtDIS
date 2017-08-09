package ph.txtdis.mgdc.fx.table;

import javafx.scene.control.TableColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.Holiday;
import ph.txtdis.fx.table.AbstractNameListTable;
import ph.txtdis.fx.table.Column;
import ph.txtdis.mgdc.fx.dialog.HolidayDialog;

import java.time.LocalDate;
import java.util.List;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.*;

@Scope("prototype")
@Component("holidayTable")
public class HolidayTable
	extends AbstractNameListTable<Holiday, HolidayDialog> {

	@Autowired
	private Column<Holiday, LocalDate> date;

	@Override
	protected List<TableColumn<Holiday, ?>> addColumns() {
		return asList( //
			date.ofType(DATE).build("Date", "declaredDate"), //
			name.ofType(TEXT).width(180).build("Name", "name"), //
			createdBy.ofType(TEXT).width(100).build("Created by", "createdBy"), //
			createdOn.ofType(TIMESTAMP).build("Created on", "createdOn"));
	}
}
