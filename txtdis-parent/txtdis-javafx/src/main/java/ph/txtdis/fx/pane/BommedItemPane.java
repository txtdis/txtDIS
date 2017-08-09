package ph.txtdis.fx.pane;

import javafx.beans.binding.BooleanBinding;

public interface BommedItemPane //
	extends ItemPane {

	void clear();

	BooleanBinding needsPrice();
}