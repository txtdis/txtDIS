package ph.txtdis.mgdc.ccbpi.app;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.Node;
import ph.txtdis.app.TotaledTableApp;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.fx.control.AppCombo;
import ph.txtdis.fx.control.AppFieldImpl;
import ph.txtdis.mgdc.ccbpi.fx.table.OrderReturnTable;
import ph.txtdis.mgdc.ccbpi.service.OrderReturnService;
import ph.txtdis.type.OrderReturnType;
import ph.txtdis.type.Type;

@Scope("prototype")
@Component("orderReturnApp")
public class OrderReturnAppImpl //
	extends AbstractBillableApp<OrderReturnService, OrderReturnTable, Long> //
	implements OrderReturnApp {

	@Autowired
	private AppCombo<OrderReturnType> reasonCombo;

	@Autowired
	private AppFieldImpl<String> orderInput, collectorDisplay, customerDisplay;

	@Autowired
	private TotaledTableApp<BillableDetail> totaledTableApp;

	@Override
	protected void addressAndOrRemarksGridLine() {
		remarksGridLineAtRowSpanning(2, 3);
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
	protected void buildFields() {
		super.buildFields();
		orderInput.build(Type.TEXT);
		customerDisplay.readOnly().build(Type.TEXT);
		collectorDisplay.readOnly().build(Type.TEXT);
	}

	@Override
	public void refresh() {
		super.refresh();
		orderInput.setValue(service.getOrderNo());
		setCustomer();
		setCollector();
		setReason();
		setRemarks();
		updateSummaries();
	}

	private void setCustomer() {
		customerDisplay.setValue(service.getCustomerName());
	}

	private void setCollector() {
		collectorDisplay.setValue(service.getCollectorName());
	}

	private void setReason() {
		reasonCombo.items(service.listReasons());
	}

	private void setRemarks() {
		remarksDisplay.setValue(service.getRemarks());
	}

	@Override
	protected void updateSummaries() {
		super.updateSummaries();
		totaledTableApp.refresh(service);
	}

	@Override
	protected void renew() {
		super.renew();
		orderInput.requestFocus();
	}

	@Override
	public void goToDefaultFocus() {
		newButton.requestFocus();
	}

	@Override
	protected void setInputFieldBindings() {
		super.setInputFieldBindings();

	}

	@Override
	protected void setListeners() {
		super.setListeners();
		orderInput.onAction(e -> setOrderUponValidation());
		reasonCombo.onAction(e -> updateUponReasonValidation());
	}

	private void setOrderUponValidation() {
		try {
			service.setOrderUponValidation(orderInput.getValue());
			setCustomer();
			setCollector();
			setRemarks();
		} catch (Exception e) {
			handleError(e, orderInput);
		}
	}

	private void updateUponReasonValidation() {
		try {
			service.updateUponReasonValidation(reasonCombo.getValue());
			refreshTable();
		} catch (Exception e) {
			handleError(e, orderInput, table);
		}
	}

	@Override
	protected void setTableBindings() {
		table.disableIf(reasonCombo.isEmpty());
	}

	@Override
	protected void secondGridLine() {
		gridPane.add(label.field("Collector"), 0, 1);
		gridPane.add(collectorDisplay, 1, 1);
		gridPane.add(label.field("Reason"), 2, 1);
		gridPane.add(reasonCombo, 3, 1);
	}

	;

	@Override
	public void start() {
		totaledTableApp.addTotalDisplays(2);
		super.start();
	}

	@Override
	protected void topGridLine() {
		gridPane.add(label.field("OCS No."), 0, 0);
		gridPane.add(orderInput, 1, 0);
		gridPane.add(label.field("Outlet"), 2, 0);
		gridPane.add(customerDisplay, 3, 0);
	}
}
