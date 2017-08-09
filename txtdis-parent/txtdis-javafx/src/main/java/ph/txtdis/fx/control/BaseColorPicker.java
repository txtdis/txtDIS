package ph.txtdis.fx.control;

import org.springframework.stereotype.Component;

import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;

@Component("baseColorPicker")
public class BaseColorPicker
	extends ColorPicker {

	public BaseColorPicker() {
		super(defaultColor());
	}

	private static Color defaultColor() {
		return Color.SLATEBLUE;
	}

	public void reset() {
		setValue(defaultColor());
	}
}
