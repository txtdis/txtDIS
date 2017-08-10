package ph.txtdis.mgdc.ccbpi.app;

import javafx.scene.Node;
import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.app.TotaledTableApp;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.mgdc.ccbpi.service.ShippedBillableService;
import ph.txtdis.mgdc.fx.table.BillableTable;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractShippedBillableApp<AS extends ShippedBillableService, AT extends BillableTable> //
	extends AbstractBillableApp<AS, AT, Long> {

	@Autowired
	private TotaledTableApp<BillableDetail> totaledTableApp;

	@Override
	protected void addressAndOrRemarksGridLine() {
	}

	@Override
	protected void secondGridLine() {
	}

	@Override
	protected String getDialogInput() {
		openByIdDialog //
			.idPrompt(service.getOpenDialogKeyPrompt()) //
			.header(service.getOpenDialogHeader()) //
			.prompt(service.getOpenDialogPrompt()) //
			.addParent(this).start();
		return openByIdDialog.getKey();
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		buildFields();
		return Arrays.asList( //
			gridPane(), //
			totaledTableApp.addNoSubHeadTablePane(table), //
			trackedPane());
	}

	@Override
	protected Node orderDateNode() {
		return stackPane(orderDateDisplay, orderDatePicker);
	}

	@Override
	public void refresh() {
		super.refresh();
		totaledTableApp.refresh(service);
	}

	@Override
	protected void renew() {
		super.renew();
		orderDatePicker.setValue(service.today());
		orderDatePicker.requestFocus();
	}

	@Override
	public void goToDefaultFocus() {
		newButton.requestFocus();
	}

	@Override
	protected void setInputFieldBindings() {
		super.setInputFieldBindings();
		orderDateBinding();
		referenceIdInput.disableIf(isPosted().or(orderDatePicker.isEmpty()));
	}

	@Override
	protected void updateSummaries() {
		super.updateSummaries();
		totaledTableApp.refresh(service);
	}

	@Override
	public void start() {
		totaledTableApp.addTotalDisplays(2);
		super.start();
	}

	@Override
	protected void topGridLine() {
		dateGridNodes();
		referenceGridNodes(service.getShipmentPrompt(), 3);
	}
}
