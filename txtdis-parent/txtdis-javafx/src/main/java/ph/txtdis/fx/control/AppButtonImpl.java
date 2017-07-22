package ph.txtdis.fx.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import ph.txtdis.util.ClientTypeMap;

@Scope("prototype")
@Component("appButton")
public class AppButtonImpl //
		extends Button //
		implements AppButton {

	@Autowired
	private ClientTypeMap typeMap;

	private String text, color, tooltip;

	private int size;

	private boolean isUnicode;

	public AppButtonImpl() {
		setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ENTER)
				fire();
		});
	}

	@Override
	public AppButtonImpl build() {
		if (tooltip != null)
			setTooltip(new ToolTip(tooltip));
		if (isUnicode)
			text = typeMap.icon(text);
		setText(text);
		String style = textButtonStyle();
		if (text == null || isUnicode)
			style = iconButtonStyle();
		setStyle(style);
		return this;
	}

	@Override
	public AppButton color(String color) {
		this.color = color;
		return this;
	}

	@Override
	public void disable() {
		disableProperty().unbind();
		disableProperty().set(true);
	}

	@Override
	public void disableIf(ObservableValue<Boolean> b) {
		disableProperty().unbind();
		disableProperty().bind(b);
	}

	@Override
	public void enable() {
		disableProperty().unbind();
		disableProperty().set(false);
	}

	@Override
	public AppButton fontSize(int size) {
		this.size = size;
		return this;
	}

	@Override
	public AppButton icon(String unicode) {
		isUnicode = true;
		return text(unicode);
	}

	@Override
	public AppButton large(String text) {
		this.text = text;
		this.size = 14;
		HBox.setMargin(this, new Insets(0, 0, 0, 10));
		return this;
	}

	@Override
	public void onAction(EventHandler<ActionEvent> e) {
		setOnAction(e);
	}

	@Override
	public AppButton text(String text) {
		this.text = text;
		return this;
	}

	@Override
	public AppButton tooltip(String tooltip) {
		this.tooltip = tooltip;
		return this;
	}

	private String buttonSize() {
		int s = iconSize() * 2 - 2;
		return " -fx-min-width: " + s + "; -fx-max-width: " + s + "; -fx-min-height: " + s + "; -fx-max-height: " + s + ";";
	}

	private String color() {
		return color == null ? "" : "-fx-text-fill: " + color + "; ";
	}

	private String iconButtonStyle() {
		return " -fx-font: " + iconSize() + " 'txtdis'; " + color() + "-fx-padding: 0; " + buttonSize();
	}

	private int iconSize() {
		return size == 0 ? 24 : size;
	}

	private int textSize() {
		return size == 0 ? 11 : size;
	}

	protected String textButtonStyle() {
		return " -fx-font-size: " + textSize() + "pt;";
	}
}
