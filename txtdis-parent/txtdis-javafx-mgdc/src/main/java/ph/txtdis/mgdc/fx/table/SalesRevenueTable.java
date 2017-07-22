package ph.txtdis.mgdc.fx.table;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.CURRENCY;
import static ph.txtdis.type.Type.TEXT;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.control.TableColumn;
import ph.txtdis.dto.SalesRevenue;
import ph.txtdis.fx.table.AbstractTable;
import ph.txtdis.fx.table.Column;
import ph.txtdis.mgdc.service.SalesRevenueService;

@Scope("prototype")
@Component("salesRevenueTable")
public class SalesRevenueTable //
		extends AbstractTable<SalesRevenue> {

	@Autowired
	private Column<SalesRevenue, String> seller;

	@Autowired
	private Column<SalesRevenue, String> customer;

	@Autowired
	private Column<SalesRevenue, BigDecimal> value;

	@Autowired
	private SalesRevenueService service;

	@Autowired
	private SellerFilterContextMenu<SalesRevenue> menu;

	@Override
	protected List<TableColumn<SalesRevenue, ?>> addColumns() {
		return asList( //
				seller.ofType(TEXT).width(120).build("Seller", "seller"), customer.ofType(TEXT).width(360).build("Customer", "customer"), //
				value.ofType(CURRENCY).build("Revenue", "value") //
		);
	}

	@Override
	protected void addProperties() {
		menu.setMenu(service, this);
	}
}
