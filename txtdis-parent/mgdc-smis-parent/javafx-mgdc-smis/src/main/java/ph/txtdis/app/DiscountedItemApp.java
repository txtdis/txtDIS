package ph.txtdis.app;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.fx.tab.InputTab;
import ph.txtdis.fx.tab.VolumeDiscountTab;

@Scope("prototype")
@Component("discountedItemApp")
public class DiscountedItemApp extends AbstractTabbedItemApp {

	@Autowired
	private VolumeDiscountTab discountTab;

	@Override
	protected List<InputTab> inputTabs() {
		inputTabs = new ArrayList<>(super.inputTabs());
		inputTabs.add(discountTab.build());
		return inputTabs;
	}

	@Override
	protected void setBindings() {
		super.setBindings();
		discountTab.disableIf(pricingTab.disabledProperty());
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		discountTab.setOnSelectionChanged(e -> checkForChangesNeedingApproval(discountTab));
	}
}
