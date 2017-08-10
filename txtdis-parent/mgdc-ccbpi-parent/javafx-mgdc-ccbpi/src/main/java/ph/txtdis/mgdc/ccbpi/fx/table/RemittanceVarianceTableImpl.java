package ph.txtdis.mgdc.ccbpi.fx.table;

import javafx.scene.control.TableColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.SalesItemVariance;
import ph.txtdis.mgdc.ccbpi.app.OrderConfirmationListApp;
import ph.txtdis.mgdc.ccbpi.service.RemittanceVarianceService;

import java.util.List;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.CURRENCY;
import static ph.txtdis.type.Type.FRACTION;

@Scope("prototype")
@Component("remittanceVarianceTable")
public class RemittanceVarianceTableImpl //
	extends AbstractVarianceTable<RemittanceVarianceService> //
	implements RemittanceVarianceTable {

	@Autowired
	private OrderConfirmationListApp app;

	@Override
	protected void buildLaunchableColumns() {
		other.ofType(FRACTION).launches(app).build(otherColumnName(), otherQtyMethodName());
		expected.ofType(FRACTION).launches(app).build(expectedHeader(), expectedQtyMethodName());
		returned.ofType(FRACTION).launches(app).build(returnedHeader(), returnedQtyMethodName());
		actual.ofType(CURRENCY).build("Amount", "returnedValue");
	}

	@Override
	protected List<TableColumn<SalesItemVariance, ?>> columns() {
		return asList(id, item, other, expected, returned, variance, actual);
	}

	@Override
	protected String valueColumnName() {
		return "Price";
	}

	@Override
	protected String valueMethodName() {
		return "priceValue";
	}
}