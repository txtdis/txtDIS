package ph.txtdis.fx.table;

import java.math.BigDecimal;
import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableBooleanValue;
import ph.txtdis.fx.control.FocusRequested;

public interface AppTable<S> extends FocusRequested {

	AbstractTableView<S> build();

	ObservableBooleanValue disabledProperty();

	void disableIf(BooleanBinding b);

	void disableIf(BooleanProperty b);

	int getColumnCount();

	int getColumnIndexOfFirstTotal();

	List<?> getColumns();

	List<BigDecimal> getColumnTotals();

	String getId();

	S getItem();

	List<S> getItems();

	int getLastRowIndex();

	BooleanBinding isEmpty();

	void items(List<S> items);

	void refresh();

	void setId(String id);

	void setItem(S item);

	void setOnEmpty(String message);

	void setOnItemChange(InvalidationListener listener);
}
