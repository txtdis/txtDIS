package ph.txtdis.fx.control;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.input.KeyCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;
import ph.txtdis.util.ClientTypeMap;

import static javafx.scene.layout.HBox.setMargin;

@Component("buttonFactory")
public class ButtonFactory {

	private final ClientTypeMap typeMap;

	private EventHandler<ActionEvent> eventHandler;

	private String text, color, tooltip;

	private boolean isLarge, isUnicode;

	private int size;

	@Autowired
	public ButtonFactory(ClientTypeMap typeMap) {
		this.typeMap = typeMap;
	}

	public AppButton build() {
		AppButton button = create();
		setOnAction(button);
		setOnEnterKeyPressed(button);
		setTooltip(button);
		setText(button);
		setStyle(button);
		resetFields();
		return button;
	}

	@Lookup
	AppButton create() {
		return null;
	}

	private void setOnAction(AppButton button) {
		if (eventHandler != null)
			button.setOnAction(eventHandler);
	}

	private void setOnEnterKeyPressed(AppButton button) {
		button.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ENTER)
				button.fire();
		});
	}

	private void setTooltip(AppButton button) {
		if (tooltip != null)
			button.setTooltip(new ToolTip(tooltip));
	}

	private void setText(AppButton button) {
		if (isUnicode)
			text = typeMap.icon(text);
		button.setText(text);
	}

	private void setStyle(AppButton button) {
		if (isLarge)
			setMargin(button, new Insets(0, 0, 0, 10));
		String style = textButtonStyle();
		if (text == null || isUnicode)
			style = iconButtonStyle();
		button.setStyle(style);
	}

	private void resetFields() {
		size = 0;
		text = null;
		color = null;
		tooltip = null;
		isLarge = false;
		isUnicode = false;
		eventHandler = null;
	}

	private String textButtonStyle() {
		return " -fx-font-size: " + textSize() + "pt;";
	}

	private String iconButtonStyle() {
		return " -fx-font: " + iconSize() + " 'txtdis'; " + color() + "-fx-padding: 0; " + buttonSize();
	}

	private int textSize() {
		return size == 0 ? 11 : size;
	}

	private int iconSize() {
		return size == 0 ? 24 : size;
	}

	private String color() {
		return color == null ? "" : "-fx-text-fill: " + color + "; ";
	}

	private String buttonSize() {
		int s = iconSize() * 2 - 2;
		return " -fx-min-width: " + s + "; -fx-max-width: " + s + "; -fx-min-height: " + s + "; -fx-max-height: " + s +
			";";
	}

	public ButtonFactory color(String color) {
		this.color = color;
		return this;
	}

	public ButtonFactory fontSize(int size) {
		this.size = size;
		return this;
	}

	public ButtonFactory icon(String unicode) {
		isUnicode = true;
		return text(unicode);
	}

	public ButtonFactory text(String text) {
		this.text = text;
		return this;
	}

	public ButtonFactory large(String text) {
		this.text = text;
		this.size = 14;
		return this;
	}

	public ButtonFactory onAction(EventHandler<ActionEvent> e) {
		eventHandler = e;
		return this;
	}

	public ButtonFactory tooltip(String tooltip) {
		this.tooltip = tooltip;
		return this;
	}
}
