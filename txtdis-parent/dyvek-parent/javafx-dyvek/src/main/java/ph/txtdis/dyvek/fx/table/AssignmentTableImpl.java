package ph.txtdis.dyvek.fx.table;

import static ph.txtdis.type.Type.CURRENCY;
import static ph.txtdis.type.Type.QUANTITY;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.TableColumn;
import ph.txtdis.dyvek.fx.dialog.AssignmentDialog;
import ph.txtdis.dyvek.model.BillableDetail;
import ph.txtdis.fx.table.Column;

@Scope("prototype")
@Component("assignmentTable")
public class AssignmentTableImpl //
		extends AbstractBillableDetailTable //
		implements AssignmentTable {

	@Autowired
	private Column<BillableDetail, BigDecimal> price, qtyAssigned;

	@Autowired
	private AssignmentContextMenu menu;

	@Autowired
	private AssignmentDialog dialog;

	private ChangeListener<? super Boolean> editabilityListener;

	private BooleanProperty editability;

	@Override
	protected List<TableColumn<BillableDetail, ?>> addColumns() {
		List<TableColumn<BillableDetail, ?>> l = new ArrayList<>(super.addColumns());
		l.add(2, price.ofType(CURRENCY).build("Price", "priceValue"));
		l.add(qtyAssigned.ofType(QUANTITY).build("Assigned", "assignedQty"));
		return l;
	}

	@Override
	protected void addProperties() {
		setQtyColumnVisibility();
		menu.addMenu(this, dialog);
	}

	private void setQtyColumnVisibility() {
		editability = editableProperty();
		editability.addListener(editabilityListener = (e, old, now) -> setQtyColumnVisibility(now));
	}

	private void setQtyColumnVisibility(Boolean isEditable) {
		qty.visibleProperty().setValue(isEditable);
		setMinWidth(width());
		refresh();
	}

	@Override
	protected String orderNoColumnName() {
		return "P/O No.";
	}

	@Override
	protected String qtyColumnName() {
		return "Balance";
	}

	@Override
	public void removeListener() {
		super.removeListener();
		editability.removeListener(editabilityListener);
	}
}
