package ph.txtdis.mgdc.ccbpi.fx.table;

import javafx.scene.control.TableColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.SalesItemVariance;
import ph.txtdis.mgdc.ccbpi.app.DeliveryListListApp;
import ph.txtdis.mgdc.ccbpi.app.OrderConfirmationListApp;
import ph.txtdis.mgdc.ccbpi.app.OrderReturnListApp;
import ph.txtdis.mgdc.ccbpi.service.BookingVarianceService;

import java.util.List;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.FRACTION;

@Scope("prototype")
@Component("bookingVarianceTable")
public class BookingVarianceTableImpl
	extends AbstractVarianceTable<BookingVarianceService>
	implements BookingVarianceTable {

	@Autowired
	private DeliveryListListApp ddlApp;

	@Autowired
	private OrderConfirmationListApp ocsApp;

	@Autowired
	private OrderReturnListApp rrApp;

	@Override
	protected void addProperties() {
		menu.setMenu(service, this);
	}

	@Override
	protected void buildLaunchableColumns() {
		expected.ofType(FRACTION).launches(ocsApp).build(expectedHeader(), expectedQtyMethodName());
		actual.ofType(FRACTION).launches(ddlApp).build(actualHeader(), actualQtyMethodName());
		returned.ofType(FRACTION).launches(rrApp).build(returnedHeader(), returnedQtyMethodName());
	}

	@Override
	protected List<TableColumn<SalesItemVariance, ?>> columns() {
		return asList(id, item, expected, actual, returned, variance, value);
	}
}
