package ph.txtdis.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.binding.BooleanBinding;
import ph.txtdis.fx.control.AppCombo;
import ph.txtdis.service.ExTruckPickListService;
import ph.txtdis.service.PickListService;

@Scope("prototype")
@Component("pickListApp")
public class PickListAppImpl extends AbstractPickListApp<PickListService> {

	@Autowired
	private AppCombo<String> collectorCombo;

	@Autowired
	private ExTruckPickListService exTruckService;

	@Override
	protected void addPersonCombosToGridpane() {
		gridPane.add(label.field("Collector"), 7, 0);
		gridPane.add(collectorCombo, 8, 0);
		gridPane.add(label.field("Helper"), 9, 0);
		gridPane.add(helperCombo.noAutoSelectSingleItem(), 10, 0);
	}

	@Override
	protected BooleanBinding noSecondPerson() {
		return super.noSecondPerson().and(driverCombo.isEmpty()).and(collectorCombo.isEmpty());
	}

	@Override
	protected void refreshSucceedingControls() {
		super.refreshSucceedingControls();
		collectorCombo.items(exTruckService.listCollectors());
	}

	@Override
	protected void setBindings() {
		super.setBindings();
		collectorCombo.disableIf(noSecondPerson());
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		collectorCombo.setOnAction(e -> validateCollector());
	}

	private void validateCollector() {
		if (service.isNew() && !collectorCombo.isEmpty().get())
			try {
				exTruckService.setCollectorUponValidation(collectorCombo.getValue());
			} catch (Exception e) {
				clearControlAfterShowingErrorDialog(e, collectorCombo);
			}
	}
}
