package ph.txtdis.dyvek.fx.table;

import static ph.txtdis.type.Type.CURRENCY;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.scene.control.TableColumn;
import ph.txtdis.dyvek.model.BillableDetail;
import ph.txtdis.fx.table.Column;

@Scope("prototype")
@Component("billingTable")
public class BillingTableImpl //
		extends AbstractBillableDetailTable //
		implements BillingTable {

	@Autowired
	private Column<BillableDetail, BigDecimal> price, value;

	@Autowired
	private Column<BillableDetail, Boolean> selection;

	private ChangeListener<Boolean> selectionColumnVisibilityListener;

	private BooleanProperty selectionColumnVisibility;

	@Override
	protected List<TableColumn<BillableDetail, ?>> addColumns() {
		List<TableColumn<BillableDetail, ?>> l = new ArrayList<>(super.addColumns());
		l.add(price.ofType(CURRENCY).build("Unit Price", "priceValue"));
		l.add(value.ofType(CURRENCY).build("Amount", "value"));
		return l;
	}

	@Override
	protected String orderNoColumnName() {
		return "D/R Number";
	}

	@Override
	protected String qtyColumnName() {
		return "Destination Weight";
	}

	@Override
	public void removeListener() {
		super.removeListener();
		if (selectionColumnVisibilityListener != null)
			selectionColumnVisibility.removeListener(selectionColumnVisibilityListener);
	}

	@Override
	public BillingTable showSelectionColumnIf(ObservableBooleanValue b) {
		selectionColumnVisibility = selection.visibleProperty();
		selectionColumnVisibility.bind(b);
		selectionColumnVisibility.addListener(selectionColumnVisibilityListener = (isVisible, isVisibleThen, isVisibleNow) -> resize(isVisibleNow));
		return this;
	}

	private void resize(boolean isVisibleNow) {
		if (!isVisibleNow)
			setMinWidth();
		autosize();
	}

	private void setMinWidth() {
		if (minWidthProperty().isBound())
			minWidthProperty().unbind();
		setMinWidth(width());
	}
}
