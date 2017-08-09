package ph.txtdis.fx.control;

import static java.util.Arrays.asList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableBooleanValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import ph.txtdis.type.Type;

@Component
@Scope("prototype")
public class LabeledField<T>
	implements InputControl<T>,
	InputNode<T> {

	@Autowired
	private AppButton searchButton;

	@Autowired
	private AppFieldImpl<T> textField;

	@Autowired
	private LabelFactory label;

	private boolean isSearchable;

	private List<Node> nodes;

	private String name;

	public LabeledField<T> build(Type type) {
		nodes = asList(label.field(name), (isSearchable ? searchableField(type) : textField(type)));
		return this;
	}

	private Node searchableField(Type type) {
		return new HBox(textField(type), searchButton());
	}

	private AppFieldImpl<T> textField(Type type) {
		return textField.build(type);
	}

	private AppButton searchButton() {
		searchButton.fontSize(16).icon("search").build();
		searchButton.focusTraversableProperty().set(false);
		return searchButton;
	}

	public void disableIf(ObservableBooleanValue b) {
		textField.disableIf(b);
	}

	@Override
	public List<Node> getNodes() {
		return nodes;
	}

	@Override
	public T getValue() {
		return textField.getValue();
	}

	@Override
	public void setValue(T value) {
		textField.setValue(value);
	}

	@Override
	public BooleanBinding isEmpty() {
		return textField.textProperty().isEmpty();
	}

	public LabeledField<T> isSearchable() {
		isSearchable = true;
		return this;
	}

	public LabeledField<T> name(String name) {
		this.name = name;
		return this;
	}

	public LabeledField<T> readOnly() {
		textField.readOnly();
		return this;
	}

	@Override
	public void requestFocus() {
		textField.requestFocus();
	}

	@Override
	public void reset() {
		clear();
	}

	@Override
	public void clear() {
		textField.clear();
	}

	public void onAction(EventHandler<ActionEvent> e) {
		textField.setOnAction(e);
	}

	public void setOnSearch(EventHandler<ActionEvent> e) {
		searchButton.onAction(e);
	}

	public LabeledField<T> width(int width) {
		textField.width(width);
		return this;
	}
}
