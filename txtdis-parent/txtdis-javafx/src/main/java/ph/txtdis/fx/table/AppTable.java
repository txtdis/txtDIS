package ph.txtdis.fx.table;

import java.math.BigDecimal;
import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.KeyEvent;
import ph.txtdis.fx.control.FocusRequested;
import ph.txtdis.fx.control.InputControl;

public interface AppTable<S> //
		extends FocusRequested, InputControl<S> {

	AbstractTable<S> build();

	ObservableBooleanValue disabled();

	void disableIf(ObservableValue<? extends Boolean> b);

	void editableIf(ObservableValue<? extends Boolean> b);

	BooleanProperty editableProperty();

	int getColumnCount();

	int getColumnIndexOfFirstTotal();

	List<?> getColumns();

	List<BigDecimal> getColumnTotals();

	String getId();

	S getItem();

	List<S> getItems();

	int getLastRowIndex();

	Scene getScene();

	BooleanBinding isEmpty();

	void items(List<S> items);

	void refresh();

	void removeListener();

	void scrollTo(int i);

	void setContextMenu(ContextMenu createTableMenu);

	void setId(String id);

	void setItem(S item);

	void setItems(ObservableList<S> l);

	void setOnEmpty(String message);

	void setOnItemChange(InvalidationListener listener);

	void setOnItemCheckBoxSelectionChange(ChangeListener<? super ObservableList<S>> listener);

	void setOnKeyPressed(EventHandler<? super KeyEvent> e);
}
