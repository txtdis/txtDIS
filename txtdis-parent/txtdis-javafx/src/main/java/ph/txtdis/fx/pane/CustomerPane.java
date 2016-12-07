package ph.txtdis.fx.pane;

import java.util.List;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableBooleanValue;
import javafx.scene.Node;

public interface CustomerPane {

	void disableNameFieldIf(ObservableBooleanValue b);

	BooleanBinding hasIncompleteData();

	void refresh();

	void save();

	void select();

	BooleanBinding showsPartnerAsACustomer();

	List<Node> mainVerticalPaneNodes();

	void setBindings();

	void setListeners();
}