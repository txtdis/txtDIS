package ph.txtdis.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.fx.tab.CustomerVolumeDiscountTab;
import ph.txtdis.fx.tab.CustomerVolumePromoTab;
import ph.txtdis.fx.tab.InputTab;

@Scope("prototype")
@Component("customerApp")
public class CustomerAppImpl extends AbstractTabbedCustomerApp {

	@Autowired
	private CustomerVolumeDiscountTab volumeDiscountTab;

	@Autowired
	private CustomerVolumePromoTab volumePromoTab;

	@Override
	protected List<InputTab> inputTabs() {
		inputTabs = new ArrayList<>(super.inputTabs());
		inputTabs.addAll(Arrays.asList(volumeDiscountTab.build(), volumePromoTab.build()));
		return inputTabs;
	}

	@Override
	protected void setBindings() {
		super.setBindings();
		volumeDiscountTab.disableIf(creditTab.disabledProperty());
		volumePromoTab.disableIf(creditTab.disabledProperty());
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		volumeDiscountTab.setOnSelectionChanged(e -> checkForChangesNeedingApproval(volumeDiscountTab));
		volumePromoTab.setOnSelectionChanged(e -> checkForChangesNeedingApproval(volumePromoTab));
	}
}
