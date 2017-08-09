package ph.txtdis.mgdc.ccbpi.app;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.control.TableColumn;
import ph.txtdis.app.AbstractTotaledReportApp;
import ph.txtdis.dto.SalesItemVariance;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.mgdc.ccbpi.fx.dialog.FilterByRouteDialog;
import ph.txtdis.mgdc.ccbpi.fx.table.BookingVarianceTable;
import ph.txtdis.mgdc.ccbpi.service.BookingVarianceService;
import ph.txtdis.type.ModuleType;

@Scope("prototype")
@Component("bookingVarianceApp")
public class BookingVarianceAppImpl //
	extends AbstractTotaledReportApp<BookingVarianceTable, BookingVarianceService, SalesItemVariance> //
	implements BookingVarianceApp {

	@Autowired
	private AppButton routeButton;

	@Autowired
	private FilterByRouteDialog routeDialog;

	@Override
	protected List<AppButton> addButtons() {
		List<AppButton> buttons = new ArrayList<>(asList(routeButton));
		buttons.addAll(super.addButtons());
		return buttons;
	}

	@Override
	protected void createButtons() {
		routeButton.icon("route").tooltip("Filter route").build();
		super.createButtons();
	}

	@Override
	protected int noOfTotalDisplays() {
		return 5;
	}

	@Override
	protected void setOnButtonClick() {
		super.setOnButtonClick();
		routeButton.onAction(e -> filterByRoute());
	}

	private void filterByRoute() {
		routeDialog.addParent(this).start();
		service.setRoute(routeDialog.getRoute());
		refresh();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void refresh() {
		super.refresh();
		table.getColumns().forEach(c -> ((TableColumn<SalesItemVariance, ?>) c).setUserData(moduleAndRouteAndDates()));
	}

	private String moduleAndRouteAndDates() {
		return ModuleType.SALES_ORDER + "$" + service.getRoute() + "|" + service.getStartDate() + "|" +
			service.getEndDate();
	}
}
