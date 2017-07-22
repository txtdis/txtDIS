package ph.txtdis.mgdc.ccbpi.app;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.DATE;
import static ph.txtdis.type.Type.ID;
import static ph.txtdis.type.Type.TEXT;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.Node;
import ph.txtdis.app.AbstractKeyedApp;
import ph.txtdis.dto.PickList;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppButtonImpl;
import ph.txtdis.fx.control.AppFieldImpl;
import ph.txtdis.fx.pane.AppGridPane;
import ph.txtdis.mgdc.ccbpi.fx.table.LoadReturnTable;
import ph.txtdis.mgdc.ccbpi.service.LoadReturnService;

@Scope("prototype")
@Component("loadReturnApp")
public class LoadReturnAppImpl //
		extends AbstractKeyedApp<LoadReturnService, PickList, Long, Long> //
		implements LoadReturnApp {

	@Autowired
	private AppButton returnButton;

	@Autowired
	private AppFieldImpl<Long> pickListIdInput;

	@Autowired
	private AppFieldImpl<String> truckDisplay;

	@Autowired
	private LoadReturnTable table;

	@Override
	protected void clear() {
		super.clear();
		table.removeListener();
	}

	@Override
	protected List<AppButtonImpl> addButtons() {
		List<AppButtonImpl> buttons = new ArrayList<>(super.addButtons());
		buttons.add(4, returnButton.icon("return").tooltip("Return all").build());
		return buttons;
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		return asList(gridPane(), box.forHorizontalPane(table.build()), trackedPane());
	}

	private AppGridPane gridPane() {
		gridPane.getChildren().clear();
		gridPane.add(label.field("Pick List No."), 0, 0);
		gridPane.add(pickListIdInput.build(ID), 1, 0);
		gridPane.add(label.field("Date"), 2, 0);
		gridPane.add(orderDateDisplay.build(DATE), 3, 0);
		gridPane.add(label.field("Truck"), 4, 0);
		gridPane.add(truckDisplay.width(120).build(TEXT), 5, 0);
		return gridPane;
	}

	@Override
	public void refresh() {
		super.refresh();
		orderDateDisplay.setValue(service.getPickDate());
		truckDisplay.setValue(service.getTruck());
		pickListIdInput.setValue(service.getId());
		table.items(service.getDetails());
	}

	@Override
	protected void renew() {
		super.renew();
		pickListIdInput.requestFocus();
	}

	@Override
	protected void setBindings() {
		returnButton.disableIf(pickListIdInput.isEmpty() //
				.or(isPosted()));
		saveButton.disableIf(table.isEmpty() //
				.or(table.disabled()) //
				.or(isPosted()));
		pickListIdInput.disableIf(isPosted());
		table.disableIf(pickListIdInput.isEmpty());
	}

	@Override
	public void goToDefaultFocus() {
		pickListIdInput.requestFocus();
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		returnButton.onAction(e -> returnAllIfNoPickedOCSHasAnRR());
		pickListIdInput.onAction(e -> updateUponPickListIdValidation());
		table.setOnItemChange(e -> service.setDetails(table.getItems()));
	}

	private void returnAllIfNoPickedOCSHasAnRR() {
		if (service.isNew())
			try {
				service.returnAllPickedItemsIfNoneOfItsOCSHasAnRR();
				refresh();
				saveButton.requestFocus();
			} catch (Exception e) {
				service.reset();
				refresh();
				showErrorDialog(e);
			}
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
}