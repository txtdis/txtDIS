package ph.txtdis.fx.table;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.CURRENCY;
import static ph.txtdis.type.Type.DATE;
import static ph.txtdis.type.Type.ID;
import static ph.txtdis.type.Type.TEXT;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.control.TableColumn;
import ph.txtdis.app.DialogClosingOnlyApp;
import ph.txtdis.dto.Remittance;

@Scope("prototype")
@Component("remittanceListTable")
public class RemittanceListTableImpl //
	extends AbstractTable<Remittance> //
	implements RemittanceListTable {

	@Autowired
	private Column<Remittance, Long> id;

	@Autowired
	private Column<Remittance, LocalDate> orderDate;

	@Autowired
	private Column<Remittance, String> bank;

	@Autowired
	private Column<Remittance, Long> check;

	@Autowired
	private Column<Remittance, BigDecimal> value;

	@Autowired
	private DialogClosingOnlyApp app;

	@Override
	protected List<TableColumn<Remittance, ?>> addColumns() {
		return asList( //
			id.ofType(ID).launches(app).build("Remit No.", "id"), //
			orderDate.ofType(DATE).launches(app).build("Date", "paymentDate"), //
			bank.ofType(TEXT).launches(app).build("Bank", "draweeBank"), //
			check.ofType(ID).launches(app).width(90).build("Check No.", "checkId"), //
			value.ofType(CURRENCY).launches(app).build("Amount", "value"));
	}
}
