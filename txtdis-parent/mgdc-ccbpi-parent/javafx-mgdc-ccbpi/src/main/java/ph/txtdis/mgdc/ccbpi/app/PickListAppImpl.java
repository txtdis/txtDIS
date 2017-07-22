package ph.txtdis.mgdc.ccbpi.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.binding.BooleanBinding;
import ph.txtdis.fx.control.AppCombo;
import ph.txtdis.mgdc.app.AbstractPickListApp;
import ph.txtdis.mgdc.service.SellerLessPickListService;

@Scope("prototype")
@Component("pickListApp")
public class PickListAppImpl //
		extends AbstractPickListApp<SellerLessPickListService> {

	@Autowired
	private AppCombo<String> collectorCombo;

	@Override
	protected void addPersonCombosToGridpane() {
		gridPane.add(label.field("Collector"), 5, 0);
		gridPane.add(collectorCombo, 6, 0);
		gridPane.add(label.field("Driver"), 7, 0);
		gridPane.add(driverCombo, 8, 0);
		gridPane.add(label.field("Helper"), 9, 0);
		gridPane.add(assistantCombo, 10, 0);
	}

	@Override
	protected BooleanBinding noSecondPerson() {
		return collectorCombo.isEmpty();
	}

	@Override
	protected void refreshSucceedingControlsAfterTruckCombo() {
		super.refreshSucceedingControlsAfterTruckCombo();
		collectorCombo.items(service.listCollectors());
	}

	@Override
	protected void renew() {
		collectorCombo.empty();
		super.renew();
	}

	@Override
	protected void setBindings() {
		super.setBindings();
		collectorCombo.disableIf(truckCombo.isEmpty());
		driverCombo.disableIf(truckCombo.isEmpty() //
				.or(isPickup()));
		remarksDisplay.editableIf(collectorCombo.isNotEmpty());
		table.disableIf(collectorCombo.isEmpty());
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		collectorCombo.onAction(e -> setCollector());
	}

	private void setCollector() {
		if (service.isNew() && collectorCombo.hasItems())
			service.setCollector(collectorCombo.getValue());
	}
}
