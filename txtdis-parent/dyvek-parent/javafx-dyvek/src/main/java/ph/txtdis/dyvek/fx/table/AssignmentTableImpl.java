package ph.txtdis.dyvek.fx.table;

import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.TableColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dyvek.model.BillableDetail;
import ph.txtdis.fx.table.Column;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static ph.txtdis.type.Type.CURRENCY;
import static ph.txtdis.type.Type.QUANTITY;

@Scope("prototype")
@Component("assignmentTable")
public class AssignmentTableImpl 
	extends AbstractBillableDetailTable 
	implements AssignmentTable {

	@Autowired
	private Column<BillableDetail, BigDecimal> price, qtyAssigned;

	@Autowired
	private AssignmentContextMenu menu;

	private ChangeListener<? super Boolean> changeListener;

	private BooleanProperty editableProperty;

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
		menu.addMenu(this);
	}

	private void setQtyColumnVisibility() {
		editableProperty = editableProperty();
		editableProperty.addListener(changeListener = (e, old, now) -> setQtyColumnVisibility(now));
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
		editableProperty.removeListener(changeListener);
	}
}
