package ph.txtdis.mgdc.gsm.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.binding.BooleanBinding;
import ph.txtdis.fx.control.AppCombo;
import ph.txtdis.mgdc.app.AbstractPickListApp;
import ph.txtdis.mgdc.gsm.service.ExTruckInclusivePickListService;
import ph.txtdis.mgdc.service.PickListService;

@Scope("prototype")
@Component("pickListApp")
public class PickListAppImpl //
		extends AbstractPickListApp<PickListService> {

	@Autowired
	private AppCombo<String> collectorCombo;

	@Autowired
	private ExTruckInclusivePickListService exTruckInclusivePickListService;

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
		return super.noSecondPerson().and(driverCombo.isEmpty()).and(collectorCombo.isEmpty());
	}

	@Override
	protected void refreshSucceedingControlsAfterTruckCombo() {
		super.refreshSucceedingControlsAfterTruckCombo();
		collectorCombo.items(exTruckInclusivePickListService.listCollectors());
	}

	@Override
	protected void setBindings() {
		super.setBindings();
		collectorCombo.disableIf(truckCombo.isEmpty());
		driverCombo.disableIf(collectorCombo.isEmpty() //
				.or(isPickup()));
		table.disableIf(collectorCombo.isEmpty());
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		collectorCombo.onAction(e -> validateCollector());
	}

	private void validateCollector() {
		if (service.isNew() && collectorCombo.hasSelectedItem())
			try {
				exTruckInclusivePickListService.setCollectorUponValidation(collectorCombo.getValue());
			} catch (Exception e) {
				clearControlAfterShowingErrorDialog(e, collectorCombo);
			}
	}
}
