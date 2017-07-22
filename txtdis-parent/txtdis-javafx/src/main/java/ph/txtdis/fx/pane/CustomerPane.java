package ph.txtdis.fx.pane;

import java.util.List;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableBooleanValue;
import javafx.scene.Node;

public interface CustomerPane {

	void clear();

	void disableNameFieldIf(ObservableBooleanValue b);

	BooleanBinding hasIncompleteData();

	List<Node> mainVerticalPaneNodes();

	void refresh();

	void save();

	void select();

	void setBindings();

	void setFocus();

	void setListeners();

	BooleanBinding showsPartnerAsACustomer();
}