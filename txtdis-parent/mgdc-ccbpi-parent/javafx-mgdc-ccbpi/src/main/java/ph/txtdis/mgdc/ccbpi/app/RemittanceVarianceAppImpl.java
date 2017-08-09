package ph.txtdis.mgdc.ccbpi.app;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.CURRENCY;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import ph.txtdis.app.AbstractTotaledReportApp;
import ph.txtdis.dto.SalesItemVariance;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppFieldImpl;
import ph.txtdis.fx.control.LabelFactory;
import ph.txtdis.mgdc.ccbpi.fx.dialog.FilterByCollectorDialog;
import ph.txtdis.mgdc.ccbpi.fx.table.RemittanceVarianceTable;
import ph.txtdis.mgdc.ccbpi.service.RemittanceVarianceService;
import ph.txtdis.type.ModuleType;

@Scope("prototype")
@Component("remittanceVarianceApp")
public class RemittanceVarianceAppImpl //
	extends AbstractTotaledReportApp<RemittanceVarianceTable, RemittanceVarianceService, SalesItemVariance> //
	implements RemittanceVarianceApp {

	@Autowired
	private AppButton collectorButton;

	@Autowired
	private AppFieldImpl<BigDecimal> actualRemittanceDisplay, loadOutValueDisplay, returnedValueDisplay,
		remittanceVarianceDisplay;

	@Autowired
	private FilterByCollectorDialog collectorDialog;

	@Autowired
	private LabelFactory label;

	@Override
	protected List<AppButton> addButtons() {
		List<AppButton> buttons = new ArrayList<>(asList(collectorButton));
		buttons.addAll(super.addButtons());
		return buttons;
	}

	@Override
	protected void createButtons() {
		collectorButton.icon("user").tooltip("Filter receivedFrom").build();
		super.createButtons();
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		return asList(totaledTableApp.addTablePane(table), summaryPane());
	}

	private Node summaryPane() {
		List<Node> l = new ArrayList<>();
		l.addAll(deliveredValueNodes());
		l.addAll(loadVarianceNodes());
		l.addAll(actualRemittanceNodes());
		l.addAll(remittanceVarianceNodes());
		return pane.centeredHorizontal(l);
	}

	private List<Node> deliveredValueNodes() {
		return asList(label.name("Expected"), loadOutValueDisplay.readOnly().build(CURRENCY));
	}

	private List<Node> loadVarianceNodes() {
		return asList(label.name("Loaded-in"), returnedValueDisplay.readOnly().build(CURRENCY));
	}

	private List<Node> actualRemittanceNodes() {
		return asList(label.name("Remitted"), actualRemittanceDisplay.readOnly().build(CURRENCY));
	}

	private List<Node> remittanceVarianceNodes() {
		return asList(label.name("Over/(Short)"), remittanceVarianceDisplay.readOnly().build(CURRENCY));
	}

	@Override
	protected int noOfTotalDisplays() {
		return 5;
	}

	@Override
	protected void setOnButtonClick() {
		super.setOnButtonClick();
		collectorButton.onAction(e -> filterByCollector());
	}

	private void filterByCollector() {
		collectorDialog.addParent(this).start();
		service.setCollector(collectorDialog.getCollector());
		refresh();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void refresh() {
		super.refresh();
		loadOutValueDisplay.setValue(service.getLoadOutValue());
		returnedValueDisplay.setValue(service.getReturnedValue());
		actualRemittanceDisplay.setValue(service.getRemittedValue());
		remittanceVarianceDisplay.setValue(service.getRemittanceVarianceValue());
		table.getColumns()
			.forEach(c -> ((TableColumn<SalesItemVariance, ?>) c).setUserData(moduleAndCollectorAndDates()));
	}

	private String moduleAndCollectorAndDates() {
		return ModuleType.REMITTANCE + "$" + service.getCollector() + "|" + service.getStartDate() + "|" +
			service.getEndDate();
	}
}
