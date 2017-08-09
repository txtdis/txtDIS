package ph.txtdis.fx.table;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.CODE;
import static ph.txtdis.type.Type.CURRENCY;
import static ph.txtdis.type.Type.DATE;
import static ph.txtdis.type.Type.INTEGER;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.control.TableColumn;
import ph.txtdis.app.BillingApp;
import ph.txtdis.dto.CustomerReceivable;

@Scope("prototype")
@Component("customerReceivableTable")
public class CustomerReceivableTable //
	extends AbstractTable<CustomerReceivable> {

	@Autowired
	private BillingApp app;

	@Autowired
	private Column<CustomerReceivable, String> id;

	@Autowired
	private Column<CustomerReceivable, LocalDate> orderDate;

	@Autowired
	private Column<CustomerReceivable, LocalDate> dueDate;

	@Autowired
	private Column<CustomerReceivable, Integer> daysOver;

	@Autowired
	private Column<CustomerReceivable, BigDecimal> totalValue;

	@Autowired
	private Column<CustomerReceivable, BigDecimal> unpaidValue;

	@Override
	protected List<TableColumn<CustomerReceivable, ?>> addColumns() {
		return asList( //
			id.launches(app).ofType(CODE).width(120).build("S/I(D/R) No.", "orderNo"),
			orderDate.launches(app).ofType(DATE).build("Date", "orderDate"), //
			dueDate.launches(app).ofType(DATE).build("Due", "dueDate"),
			daysOver.launches(app).ofType(INTEGER).build("Days Over", "daysOverCount"),
			totalValue.launches(app).ofType(CURRENCY).build("Total Amount", "totalValue"),
			unpaidValue.launches(app).ofType(CURRENCY).build("Unpaid Balance", "unpaidValue"));
	}
}
