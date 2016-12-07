package ph.txtdis.fx.table;

import static javafx.collections.FXCollections.emptyObservableList;
import static javafx.collections.FXCollections.observableArrayList;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
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
public abstract class AbstractTableView<S> extends TableView<S> implements AppTable<S> {

	private S item;

	public AbstractTableView() {
		setStyle("-fx-opacity: 1; ");
	}

	@Override
	public AbstractTableView<S> build() {
		addColumns();
		setMinWidth(width());
		addProperties();
		return this;
	}

	@Override
	public void disableIf(BooleanBinding b) {
		disableProperty().bind(b);
	}

	@Override
	public void disableIf(BooleanProperty b) {
		disableProperty().bind(b);
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
	public BooleanBinding isEmpty() {
		return itemsProperty().isEqualTo(emptyObservableList());
	}

	@Override
	public void items(List<S> list) {
		setItems(list == null ? emptyObservableList() : (ObservableList<S>) observableArrayList(list));
	}

	@Override
	public void setOnEmpty(String text) {
		setPlaceholder(message(text));
	}

	@Override
	public void setOnItemChange(InvalidationListener listener) {
		itemsProperty().addListener(listener);
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

	protected abstract void addColumns();

	protected void addProperties() {
	}

	protected double width() {
		double width = 20;
		for (TableColumn<S, ?> column : getColumns())
			if (column.isVisible())
				width = width + column.getMinWidth();
		return width;
	}
}
