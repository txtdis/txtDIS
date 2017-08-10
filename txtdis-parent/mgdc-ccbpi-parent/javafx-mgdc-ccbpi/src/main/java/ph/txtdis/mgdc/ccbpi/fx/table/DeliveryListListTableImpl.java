package ph.txtdis.mgdc.ccbpi.fx.table;

import javafx.scene.control.TableColumn;
import org.apache.commons.lang3.math.Fraction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.SalesItemVariance;
import ph.txtdis.fx.table.AbstractTable;
import ph.txtdis.fx.table.Column;
import ph.txtdis.mgdc.ccbpi.app.DeliveryListApp;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static ph.txtdis.type.Type.*;

@Scope("prototype")
@Component("deliveryListListTable")
public class DeliveryListListTableImpl
	extends AbstractTable<SalesItemVariance>
	implements DeliveryListListTable {

	@Autowired
	private Column<SalesItemVariance, String> route, shipment;

	@Autowired
	private Column<SalesItemVariance, Fraction> qty;

	@Autowired
	private Column<SalesItemVariance, BigDecimal> value;

	@Autowired
	private DeliveryListApp app;

	@Override
	protected List<TableColumn<SalesItemVariance, ?>> addColumns() {
		return Arrays.asList(//
			route.ofType(TEXT).launches(app).width(80).build("Route", "seller"), //
			shipment.ofType(TEXT).launches(app).width(120).build("Shipment No.", "orderNo"), //
			qty.ofType(FRACTION).launches(app).build("Quantity", "varianceQtyInFractions"), //
			value.ofType(CURRENCY).launches(app).build("Amount", "value"));
	}
}
