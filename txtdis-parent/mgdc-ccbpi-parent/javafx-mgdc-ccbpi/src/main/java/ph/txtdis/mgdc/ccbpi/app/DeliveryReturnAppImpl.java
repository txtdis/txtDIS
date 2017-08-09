package ph.txtdis.mgdc.ccbpi.app;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.DATE;
import static ph.txtdis.type.Type.ID;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.Node;
import ph.txtdis.app.AbstractKeyedApp;
import ph.txtdis.dto.PickList;
import ph.txtdis.fx.control.AppFieldImpl;
import ph.txtdis.fx.pane.AppGridPane;
import ph.txtdis.mgdc.ccbpi.fx.table.LoadReturnTable;
import ph.txtdis.mgdc.ccbpi.service.DeliveryReturnService;

@Scope("prototype")
@Component("deliveryReturnApp")
public class DeliveryReturnAppImpl //
	extends AbstractKeyedApp<DeliveryReturnService, PickList, Long, Long> //
	implements DeliveryReturnApp {

	@Autowired
	private AppFieldImpl<Long> pickListIdInput;

	@Autowired
	private LoadReturnTable table;

	@Override
	protected void clear() {
		super.clear();
		table.removeListener();
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		return asList(gridPane(), pane.centeredHorizontal(table.build()), trackedPane());
	}

	private AppGridPane gridPane() {
		gridPane.getChildren().clear();
		gridPane.add(label.field("Date"), 0, 0);
		gridPane.add(orderDateDisplay.build(DATE), 1, 0);
		gridPane.add(label.field("Pick List No."), 2, 0);
		gridPane.add(pickListIdInput.build(ID), 3, 0);
		return gridPane;
	}

	@Override
	protected void setBindings() {
		saveButton.disableIf(table.isEmpty()//
			.or(table.disabled())//
			.or(isPosted()));
		table.disableIf(pickListIdInput.isEmpty());
	}

	@Override
	public void goToDefaultFocus() {
		pickListIdInput.requestFocus();
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		pickListIdInput.onAction(e -> updateUponPickListIdValidation());
	}

	private void updateUponPickListIdValidation() {
		if (service.isNew())
			try {
				service.updateUponPickListIdValidation(pickListIdInput.getValue());
				refresh();
				table.requestFocus();
			} catch (Exception e) {
				service.reset();
				refresh();
				clearControlAfterShowingErrorDialog(e, pickListIdInput);
			}
	}

	@Override
	public void refresh() {
		super.refresh();
		orderDateDisplay.setValue(service.getPickDate());
		table.items(service.getDetails());
	}
}