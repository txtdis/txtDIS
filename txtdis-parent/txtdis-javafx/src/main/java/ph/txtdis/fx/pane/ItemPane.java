package ph.txtdis.fx.pane;

import javafx.beans.binding.BooleanBinding;
import javafx.scene.layout.Pane;

public interface ItemPane {

	Pane get();

	BooleanBinding hasIncompleteData();

	void refresh();

	void save();

	void select();

	void setBindings();

	void setFocus();

	void setListeners();
}