package ph.txtdis.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import ph.txtdis.fx.control.AppCombo;
import ph.txtdis.service.PickListService;
import ph.txtdis.service.PreSellPickListService;

@Lazy
@Component("pickListApp")
public class PickListAppImpl extends AbstractPickListApp<PickListService> {

	@Autowired
	private AppCombo<String> asstHelperCombo;

	@Autowired
	private PreSellPickListService preSellService;

	@Override
	protected void addPersonCombosToGridpane() {
		gridPane.add(label.field("Lead Helper"), 7, 0);
		gridPane.add(helperCombo, 8, 0);
		gridPane.add(label.field("Asst Helper"), 9, 0);
		gridPane.add(asstHelperCombo, 10, 0);
	}

	@Override
	protected void refreshSucceedingControls() {
		super.refreshSucceedingControls();
		asstHelperCombo.items(preSellService.listAsstHelpers());
	}

	@Override
	protected void reset() {
		super.reset();
		asstHelperCombo.empty();
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		asstHelperCombo.setOnAction(e -> validateAsstHelper());
	}

	private void validateAsstHelper() {
		if (asstHelperCombo.getItems() != null && asstHelperCombo.getItems().size() > 1)
			try {
				preSellService.setAsstHelperUponValidation(asstHelperCombo.getValue());
			} catch (Exception e) {
				clearControlAfterShowingErrorDialog(e, asstHelperCombo);
			}
	}

	@Override
	protected void setBindings() {
		super.setBindings();
		asstHelperCombo.disableIf(driverCombo.isEmpty());
	}
}
