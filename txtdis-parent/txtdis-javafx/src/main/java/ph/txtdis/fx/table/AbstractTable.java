package ph.txtdis.fx.table;

import static javafx.collections.FXCollections.emptyObservableList;
import static javafx.collections.FXCollections.observableList;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.TextAlignment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractTable<S> extends TableView<S> //
		implements AppTable<S> {

	private ChangeListener<? super ObservableList<S>> changeListener;

	private InvalidationListener invalidationListener;

	private S item;

	public AbstractTable() {
		setStyle("-fx-opacity: 1; ");
		item = null;
	}

	@Override
	public AbstractTable<S> build() {
		getColumns().setAll(addColumns());
		setMinWidth(width());
		addProperties();
		return this;
	}

	protected abstract List<TableColumn<S, ?>> addColumns();

	protected double width() {
		double width = 20;
		for (TableColumn<S, ?> column : getColumns())
			if (column.isVisible())
				width = width + column.getMinWidth();
		return width;
	}

	protected void addProperties() {
	}

	@Override
	public void clear() {
		this.getChildren().clear();
	}

	@Override
	public ObservableBooleanValue disabled() {
		return disabledProperty();
	}

	@Override
	public void disableIf(ObservableValue<? extends Boolean> b) {
		disableProperty().unbind();
		disableProperty().bind(b);
	}

	@Override
	public void editableIf(ObservableValue<? extends Boolean> b) {
		editableProperty().unbind();
		editableProperty().bind(b);
	}

	@Override
	public int getColumnCount() {
		return getColumns().size();
	}

	@Override
	public int getColumnIndexOfFirstTotal() {
		return getColumnCount() - getColumnTotals().size();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<BigDecimal> getColumnTotals() {
		return getUserData() == null ? new ArrayList<>() : (List<BigDecimal>) getUserData();
	}

	@Override
	public int getLastRowIndex() {
		return getItems().size();
	}

	@Override
	public S getValue() {
		return getItem();
	}

	@Override
	public BooleanBinding isEmpty() {
		return itemsProperty().isEqualTo(emptyObservableList());
	}

	@Override
	public void items(List<S> list) {
		setItems(list == null ? emptyObservableList() : observableList(list));
	}

	@Override
	public void setOnEmpty(String text) {
		setPlaceholder(message(text));
	}

	private Label message(String text) {
		Label label = new Label(text);
		label.setWrapText(true);
		label.setAlignment(Pos.CENTER);
		label.setTextAlignment(TextAlignment.CENTER);
		label.setPrefHeight(75);
		label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		return label;
	}

	@Override
	public void removeListener() {
		if (invalidationListener != null)
			itemsProperty().removeListener(invalidationListener);
		if (changeListener != null)
			itemsProperty().removeListener(changeListener);
	}

	@Override
	public void setOnItemChange(InvalidationListener listener) {
		invalidationListener = listener;
		itemsProperty().addListener(listener);
	}

	@Override
	public void setOnItemCheckBoxSelectionChange(ChangeListener<? super ObservableList<S>> listener) {
		changeListener = listener;
		itemsProperty().addListener(listener);
	}

	@Override
	public void setValue(S value) {
		setItem(value);
	}
}
