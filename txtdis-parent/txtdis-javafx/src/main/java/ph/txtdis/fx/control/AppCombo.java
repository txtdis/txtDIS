package ph.txtdis.fx.control;

import static java.util.Arrays.asList;
import static javafx.collections.FXCollections.emptyObservableList;
import static javafx.collections.FXCollections.observableArrayList;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyEvent.KEY_PRESSED;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sun.javafx.scene.control.behavior.ComboBoxBaseBehavior;
import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.scene.control.ComboBox;

@Component
@Scope("prototype")
@SuppressWarnings("restriction")
public class AppCombo<T> extends ComboBox<T> implements InputControl<T> {

	private boolean singleItemIsNotAutoSelected;

	public AppCombo() {
		setEditable(false);
		traversePressedEnterKey();
		setMinWidth(120);
	}

	@SuppressWarnings("unchecked")
	private void traversePressedEnterKey() {
		addEventFilter(KEY_PRESSED, event -> {
			if (event.getCode() == ENTER) {
				ComboBoxListViewSkin<T> skin = (ComboBoxListViewSkin<T>) getSkin();
				ComboBoxBaseBehavior<T> behavior = skin.getBehavior();
				behavior.traverseNext();
			}
		});
	}

	public BooleanBinding are(List<T> items) {
		BooleanBinding b = Bindings.not(new SimpleBooleanProperty(true));
		if (items == null || items.isEmpty())
			return b;
		b = is(items.get(0));
		for (int i = 1; i < items.size(); i++)
			b = b.or(is(items.get(i)));
		return b;
	}

	@SuppressWarnings("unchecked")
	public BooleanBinding are(T... items) {
		return are(asList(items));
	}

	public BooleanBinding areOfText(List<String> texts) {
		BooleanBinding b = isOfText(texts.get(0));
		for (int i = 1; i < texts.size(); i++)
			b = b.or(isOfText(texts.get(i)));
		return b;
	}

	@Override
	public void clear() {
		setValue(null);
	}

	public void disableIf(BooleanBinding b) {
		disableProperty().bind(b);
	}

	public void disableIf(ReadOnlyBooleanProperty b) {
		disableProperty().bind(b);
	}

	public void empty() {
		getItems().clear();
	}

	public boolean hasItems() {
		return getItems() != null && getItems().size() > 1;
	}

	public BooleanBinding is(T item) {
		return getSelectionModel().selectedItemProperty().isEqualTo(item);
	}

	public ObservableBooleanValue isDisabledNow() {
		return disabledProperty();
	}

	public BooleanBinding isEmpty() {
		return getSelectionModel().selectedItemProperty().isNull();
	}

	public BooleanBinding isNot(T item) {
		return is(item).not();
	}

	public BooleanBinding isNotEmpty() {
		return isEmpty().not();
	}

	public BooleanBinding isNotOfText(String text) {
		return isOfText(text).not();
	}

	public BooleanBinding isOfText(String text) {
		return Bindings.convert(getSelectionModel().selectedItemProperty()).isEqualTo(text);
	}

	public AppCombo<T> items(List<T> items) {
		setItems(items == null ? emptyObservableList() : observableArrayList(items));
		selectSingleItemIfAutoSelectedAndDisableFocusTranversing();
		return this;
	}

	private void selectSingleItemIfAutoSelectedAndDisableFocusTranversing() {
		if (size() == 1 && !singleItemIsNotAutoSelected) {
			select(0);
			focusTraversableProperty().set(false);
		} else {
			select(null);
			focusTraversableProperty().set(true);
		}
	}

	public AppCombo<T> items(T[] items) {
		return items(asList(items));
	}

	public AppCombo<T> itemsSelectingFirst(List<T> items) {
		items(items);
		select(0);
		return this;
	}

	public AppCombo<T> itemsSelectingFirst(T[] items) {
		return itemsSelectingFirst(asList(items));
	}

	public AppCombo<T> noAutoSelectSingleItem() {
		singleItemIsNotAutoSelected = true;
		return this;
	}

	public AppCombo<T> readOnlyOfWidth(int width) {
		focusTraversableProperty().set(false);
		width(width);
		return this;
	}

	public void select(int index) {
		getSelectionModel().select(index);
	}

	public void select(T selection) {
		getSelectionModel().select(selection);
	}

	public int size() {
		return getItems().size();
	}

	public AppCombo<T> width(int width) {
		setMinWidth(width);
		return this;
	}
}
