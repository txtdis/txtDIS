package ph.txtdis.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javafx.beans.binding.BooleanBinding;
import ph.txtdis.fx.control.AppCombo;
import ph.txtdis.service.SellerLessPickListService;

@Lazy
@Component("pickListApp")
public class PickListAppImpl extends AbstractPickListApp<SellerLessPickListService> {

	@Autowired
	private AppCombo<String> collectorCombo;

	@Override
	protected void addPersonCombosToGridpane() {
		gridPane.add(label.field("Collector"), 7, 0);
		gridPane.add(collectorCombo, 8, 0);
		gridPane.add(label.field("Helper"), 9, 0);
		gridPane.add(helperCombo.noAutoSelectSingleItem(), 10, 0);
	}

	@Override
	protected BooleanBinding noSecondPerson() {
		return collectorCombo.isEmpty();
	}

	@Override
	protected void refreshSucceedingControls() {
		super.refreshSucceedingControls();
		collectorCombo.items(service.listCollectors());
	}

	@Override
	protected void setBindings() {
		super.setBindings();
		collectorCombo.disableIf(driverCombo.isEmpty());
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		collectorCombo.setOnAction(e -> service.setCollector(collectorCombo.getValue()));
	}
}
