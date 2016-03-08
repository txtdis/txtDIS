package ph.txtdis.fx.table;

import static ph.txtdis.type.Type.CURRENCY;
import static ph.txtdis.type.Type.TEXT;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.SalesRevenue;

@Lazy
@Component("salesRevenueTable")
public class SalesRevenueTable extends AppTable<SalesRevenue> {

	@Autowired
	private Column<SalesRevenue, String> customer;

	@Autowired
	private Column<SalesRevenue, BigDecimal> value;

	@Override
	@SuppressWarnings("unchecked")
	protected void addColumns() {
		getColumns().setAll(//
				customer.ofType(TEXT).width(360).build("Customer", "customer"), //
				value.ofType(CURRENCY).build("Revenue", "value") //
		);
	}
}
