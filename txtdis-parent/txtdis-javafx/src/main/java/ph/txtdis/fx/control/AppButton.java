package ph.txtdis.fx.control;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.util.ClientTypeMap;

@Scope("prototype")
@Component("appButton")
public class AppButton //
	extends Button {

	@Autowired
	private ClientTypeMap typeMap;

	private String text, color, tooltip;

	private int size;

	private boolean isUnicode;

	public AppButton build() {
		setOnEnterKeyPressed();
		setTooltip();
		setText();
		setStyle();
		return this;
	}

	private void setOnEnterKeyPressed() {
		setOnKeyPressed(e -> fire(e));
	}

	private void setTooltip() {
		if (tooltip != null)
			setTooltip(new ToolTip(tooltip));
	}

	private void setText() {
		if (isUnicode)
			text = typeMap.icon(text);
		setText(text);
	}

	private void setStyle() {
		String style = textButtonStyle();
		if (text == null || isUnicode)
			style = iconButtonStyle();
		setStyle(style);
	}

	private void fire(KeyEvent e) {
		if (e.getCode() == KeyCode.ENTER)
			fire();
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

	public AppButton color(String color) {
		this.color = color;
		return this;
	}

	public void disable() {
		disableProperty().unbind();
		disableProperty().set(true);
	}

	public void disableIf(ObservableValue<Boolean> b) {
		disableProperty().unbind();
		disableProperty().bind(b);
	}

	public void enable() {
		disableProperty().unbind();
		disableProperty().set(false);
	}

	public AppButton fontSize(int size) {
		this.size = size;
		return this;
	}

	public AppButton icon(String unicode) {
		isUnicode = true;
		return text(unicode);
	}

	public AppButton text(String text) {
		this.text = text;
		return this;
	}

	public AppButton large(String text) {
		this.text = text;
		this.size = 14;
		HBox.setMargin(this, new Insets(0, 0, 0, 10));
		return this;
	}

	public void onAction(EventHandler<ActionEvent> e) {
		setOnAction(e);
	}

	public AppButton tooltip(String tooltip) {
		this.tooltip = tooltip;
		return this;
	}
}
