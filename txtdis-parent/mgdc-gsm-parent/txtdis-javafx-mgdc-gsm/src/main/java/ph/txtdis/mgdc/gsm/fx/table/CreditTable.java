package ph.txtdis.mgdc.gsm.fx.table;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.CURRENCY;
import static ph.txtdis.type.Type.INTEGER;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.control.TableColumn;
import ph.txtdis.dto.CreditDetail;
import ph.txtdis.fx.table.AbstractTable;
import ph.txtdis.fx.table.Column;
import ph.txtdis.fx.table.DecisionNeededTableControls;
import ph.txtdis.mgdc.gsm.fx.dialog.CreditDialog;

@Scope("prototype")
@Component("creditTable")
public class CreditTable extends AbstractTable<CreditDetail> {

	@Autowired
	private Column<CreditDetail, Integer> termInDays;

	@Autowired
	private Column<CreditDetail, Integer> gracePeriodInDays;

	@Autowired
	private Column<CreditDetail, BigDecimal> creditLimit;

	@Autowired
	private DecisionNeededTableControls<CreditDetail> decisionNeeded;

	@Autowired
	private CreditDialog dialog;

	@Override
	protected List<TableColumn<CreditDetail, ?>> addColumns() {
		List<TableColumn<CreditDetail, ?>> columns = new ArrayList<>(asList( //
				termInDays.ofType(INTEGER).width(60).build("Term", "termInDays"), //
				gracePeriodInDays.ofType(INTEGER).width(60).build("Grace\nPeriod", "gracePeriodInDays"), //
				creditLimit.ofType(CURRENCY).build("Credit\nLimit", "creditLimit")));
		columns.addAll(decisionNeeded.addColumns());
		return columns;
	}

	@Override
	protected void addProperties() {
		setOnEmpty("Complete contact details\nto enable data entry");
		decisionNeeded.addContextMenu(this, dialog);
	}
}
