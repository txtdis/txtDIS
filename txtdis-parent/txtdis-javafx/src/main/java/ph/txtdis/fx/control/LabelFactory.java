package ph.txtdis.fx.control;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

import static java.lang.Double.MAX_VALUE;
import static javafx.geometry.HPos.RIGHT;
import static javafx.scene.layout.Region.USE_PREF_SIZE;
import static javafx.scene.text.TextAlignment.CENTER;

@Component("labelFactory")
public class LabelFactory {

	public AppLabel dialog(String text) {
		AppLabel label = header(text);
		label.setPadding(new Insets(20, 0, 0, 0));
		return label;
	}

	public AppLabel header(String text) {
		AppLabel label = name(text);
		label.setStyle(" -fx-font-size: 26pt;");
		return label;
	}

	public AppLabel name(String name) {
		AppLabel label = create();
		label.setText(name);
		return label;
	}

	@Lookup
	protected AppLabel create() {
		return null;
	}

	public AppLabel field(String text) {
		AppLabel label = name(text);
		label.setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
		GridPane.setHalignment(label, RIGHT);
		return label;
	}

	public AppLabel group(String text) {
		AppLabel label = name(text);
		label.setStyle(" -fx-font-size: 16pt;");
		return label;
	}

	public AppLabel icon(String unicode, String color) {
		AppLabel label = name(unicode);
		label.setStyle(" -fx-font: 90pt 'txtdis'; -fx-text-fill: " + color + ";");
		label.setPadding(new Insets(20, 0, 20, 0));
		return label;
	}

	public AppLabel menu(String name) {
		AppLabel label = name(name);
		GridPane.setHalignment(label, HPos.CENTER);
		return label;
	}

	public AppLabel message(String text) {
		AppLabel label = help(text);
		label.setPrefHeight(75);
		return label;
	}

	public AppLabel help(String text) {
		AppLabel label = name(text);
		label.setWrapText(true);
		label.setAlignment(Pos.CENTER);
		label.setTextAlignment(CENTER);
		label.setMaxSize(MAX_VALUE, MAX_VALUE);
		return label;
	}

	public AppLabel subheader(String text) {
		AppLabel label = name(text);
		label.setStyle("-fx-font-size: 18pt;");
		label.setAlignment(Pos.CENTER);
		return label;
	}
}
