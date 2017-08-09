package ph.txtdis.mgdc.fx.tab;

import javafx.beans.binding.BooleanBinding;
import ph.txtdis.fx.tab.InputTab;

public interface ItemTab
	extends InputTab {

	BooleanBinding hasIncompleteData();

	BooleanBinding needsPrice();

	void setFocus();
}