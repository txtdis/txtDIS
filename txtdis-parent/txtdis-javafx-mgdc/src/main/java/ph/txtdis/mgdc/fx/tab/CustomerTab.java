package ph.txtdis.mgdc.fx.tab;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableBooleanValue;
import ph.txtdis.fx.tab.InputTab;

public interface CustomerTab
	extends InputTab {

	InputTab build();

	void disableNameFieldIf(ObservableBooleanValue b);

	BooleanBinding hasIncompleteData();

	void setFocus();

	BooleanBinding showsPartnerAsACustomer();
}