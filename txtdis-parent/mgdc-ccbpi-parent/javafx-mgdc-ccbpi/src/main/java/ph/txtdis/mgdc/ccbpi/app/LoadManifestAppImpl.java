package ph.txtdis.mgdc.ccbpi.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.mgdc.ccbpi.fx.table.LoadManifestTable;
import ph.txtdis.mgdc.ccbpi.service.LoadManifestService;

@Scope("prototype")
@Component("loadManifestApp")
public class LoadManifestAppImpl //
	extends AbstractShippedBillableApp<LoadManifestService, LoadManifestTable> //
	implements LoadManifestApp {

	@Override
	protected void setListeners() {
		super.setListeners();
		referenceIdInput.onAction(e -> setShipmentDateAndId());
	}

	private void setShipmentDateAndId() {
		service.setShipmentDateAndId( //
			orderDatePicker.getValue(), //
			referenceIdInput.getValue());
	}

	@Override
	protected void setTableBindings() {
		table.disableIf(referenceIdInput.isEmpty());
	}
}
