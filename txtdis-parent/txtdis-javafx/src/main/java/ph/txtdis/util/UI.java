package ph.txtdis.util;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class UI {

	public static void loadFont(String fontName) {
		Font.loadFont(UI.class.getResourceAsStream("/font/" + fontName + ".ttf"), 24);
	}

	public static String toRGBA(Color color) {
		return String.format("rgba(%d, %d, %d, %f)", (int) Math.round(color.getRed() * 255),
				(int) Math.round(color.getGreen() * 255), (int) Math.round(color.getBlue() * 255), color.getOpacity());
	}
}
