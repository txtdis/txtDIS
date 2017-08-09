package ph.txtdis.fx.control;

import javafx.scene.control.Tooltip;

public class ToolTip
	extends Tooltip {

	public ToolTip(String text) {
		super(text);
		setStyle("-fx-font-size: 10pt;");
	}
}
