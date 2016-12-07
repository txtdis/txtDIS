package ph.txtdis.app;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.Node;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.fx.control.AppCombo;
import ph.txtdis.fx.table.DeliveryListTable;
import ph.txtdis.service.DeliveryListService;

@Scope("prototype")
@Component("deliveryListApp")
public class DeliveryListAppImpl //
		extends AbstractBillableApp<DeliveryListService, DeliveryListTable, Long> //
		implements DeliveryListApp {

	private static final String FOR = "\nfor ";

	private static final String FORMAT_IS = "Format is: ";

	private static final String ROUTE = "E3CX";

	private static final String SHIPMENT_ID = "12345678";

	private static final String SHIPMENT_NO = "Shipment No. ";

	@Autowired
	private AppCombo<String> routeCombo;

	@Autowired
	private TotaledTableApp<BillableDetail> totaledTableApp;

	@Override
	protected void customerGridLine() {
	}

	@Override
	protected void addressAndOrRemarksGridLines() {
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		return Arrays.asList( //
				gridPane(), //
				totaledTableApp.addNoSubHeadTablePane(table), //
				trackedPane());
	}

	@Override
	protected void topGridLine() {
		dateGridNodes();
		referenceGridNodes(service.getDeliveryListPrompt(), 5);
		routeGridNodes();
	}

	@Override
	protected Node orderDateNode() {
		return orderDateDisplayOnly();
	}

	private void routeGridNodes() {
		gridPane.add(label.field(service.getRoutePrompt()), 5, 0);
		gridPane.add(routeCombo, 6, 0);
	}

	@Override
	protected String getDialogInput() {
		openByIdDialog //
				.idPrompt(SHIPMENT_NO + "+ Route") //
				.header(openByIdDialogHeader()) //
				.prompt(openByIdDialogPrompt()) //
				.addParent(this).start();
		return openByIdDialog.getId();
	}

	@Override
	protected String openByIdDialogPrompt() {
		return FORMAT_IS + SHIPMENT_ID + ROUTE + FOR + SHIPMENT_NO + SHIPMENT_ID + " & Route " + ROUTE;
	}

	@Override
	public void refresh() {
		super.refresh();
		routeCombo.items(service.listRoutes());
		referenceIdInput.setValue(service.getDeliveryListId());
		totaledTableApp.refresh(service);
	}

	@Override
	protected void reset() {
		super.reset();
		referenceIdInput.requestFocus();
	}

	@Override
	protected void setButtonBindings() {
		saveButton.disableProperty().unbind();
		saveButton.disableIf(isPosted().or(table.isEmpty()));
	}

	@Override
	public void setFocus() {
		newButton.requestFocus();
	}

	@Override
	protected void setInputFieldBindings() {
		super.setInputFieldBindings();
		routeCombo.disableIf(referenceIdInput.isEmpty());
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		referenceIdInput.setOnAction(e -> updateUponReferenceIdValidation());
		routeCombo.setOnAction(e -> service.setRoute(routeCombo.getValue()));
		table.setOnItemChange(e -> updateSummaries());
	}

	private void updateUponReferenceIdValidation() {
		Long id = referenceIdInput.getValue();
		if (service.isNew() && id != null && id != 0)
			try {
				service.updateUponReferenceIdValidation(id);
			} catch (Exception e) {
				handleError(e, referenceIdInput);
			} finally {
				refresh();
			}
	}

	private void updateSummaries() {
		totaledTableApp.refresh(service);
	}

	@Override
	protected void setTableBindings() {
		table.disableIf(routeCombo.isEmpty());
	}

	@Override
	public void start() {
		totaledTableApp.addTotalDisplays(1);
		super.start();
	}
}
