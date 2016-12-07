package ph.txtdis.app;

import static org.apache.log4j.Logger.getLogger;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.Node;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppCombo;
import ph.txtdis.fx.table.SalesOrderTable;
import ph.txtdis.service.SalesOrderService;
import ph.txtdis.type.BillableType;

@Scope("prototype")
@Component("salesOrderApp")
public class SalesOrderAppImpl //
		extends AbstractBookingApp<SalesOrderService, SalesOrderTable> //
		implements SalesOrderApp {

	private static Logger logger = getLogger(SalesOrderAppImpl.class);

	@Autowired
	private AppCombo<String> exTruckCombo;

	@Autowired
	private RouteItineraryApp routeItineraryApp;

	private BillableType type;

	@Override
	protected List<AppButton> addButtons() {
		List<AppButton> b = new ArrayList<>(super.addButtons());
		if (isExTruck())
			b.remove(overrideButton);
		b.add(routeItineraryApp.addButton(this, service));
		return b;
	}

	@Override
	protected void addressAndOrRemarksGridLines() {
		if (isExTruck())
			addRemarksAtLine(1, 8);
		else
			super.addressAndOrRemarksGridLines();
	}

	@Override
	protected void customerGridLine() {
		if (!isExTruck())
			super.customerGridLine();
	}

	private boolean isExTruck() {
		return type == BillableType.EX_TRUCK;
	}

	@Override
	protected Node idBox() {
		if (!isExTruck())
			return super.idBox();
		return exTruckCombo.readOnlyOfWidth(120);
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		List<Node> l = new ArrayList<>(super.mainVerticalPaneNodes());
		if (!isExTruck())
			l.add(vatPane());
		l.add(trackedPane());
		return l;
	}

	@Override
	public void refresh() {
		super.refresh();
		if (isExTruck())
			exTruckCombo.items(service.listUnbookedExTrucks());
	}

	@Override
	protected void reset() {
		super.reset();
		if (isExTruck())
			exTruckCombo.requestFocus();
	}

	@Override
	protected void setInputFieldBindings() {
		super.setInputFieldBindings();
		exTruckCombo.disableIf(orderDatePicker.isEmpty());
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		exTruckCombo.setOnAction(e -> setExTruckAsCustomerUponValidation());
	}

	private void setExTruckAsCustomerUponValidation() {
		logger.info("\n    ExTruckComboValue = " + exTruckCombo.getValue());
		if (!exTruckCombo.isEmpty().get())
			try {
				service.setExTruckAsCustomerIfExTruckPreviousTransactionsAreComplete(exTruckCombo.getValue());
			} catch (Exception e) {
				handleError(e, exTruckCombo);
			}
	}

	@Override
	protected void setTableBindings() {
		if (!isExTruck())
			super.setTableBindings();
		else
			table.disableIf(exTruckCombo.isEmpty());
	}

	@Override
	public void start() {
		service.setType(type);
		super.start();
	}

	@Override
	public Startable type(BillableType type) {
		this.type = type;
		return this;
	}

	@Override
	public BillableType type() {
		return type;
	}
}
