package ph.txtdis.mgdc.gsm.fx.table;

import javafx.scene.control.TableColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.WeeklyVisit;
import ph.txtdis.fx.table.AbstractTable;
import ph.txtdis.fx.table.Column;

import java.util.List;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.CHECKBOX;
import static ph.txtdis.type.Type.INTEGER;

@Scope("prototype")
@Component("visitScheduleTable")
public class VisitScheduleTable //
	extends AbstractTable<WeeklyVisit> {

	@Autowired
	private Column<WeeklyVisit, Integer> weekNo;

	@Autowired
	private Column<WeeklyVisit, Boolean> sun, mon, tue, wed, thu, fri, sat;

	@Override
	protected List<TableColumn<WeeklyVisit, ?>> addColumns() {
		return asList( //
			weekNo.ofType(INTEGER).build("Week No.", "weekNo"), //
			sun.ofType(CHECKBOX).build("Sun", "sun"), //
			mon.ofType(CHECKBOX).build("Mon", "mon"), //
			tue.ofType(CHECKBOX).build("Tue", "tue"), //
			wed.ofType(CHECKBOX).build("Wed", "wed"), //
			thu.ofType(CHECKBOX).build("Thu", "thu"), //
			fri.ofType(CHECKBOX).build("Fri", "fri"), //
			sat.ofType(CHECKBOX).build("Sat", "sat"));
	}
}
