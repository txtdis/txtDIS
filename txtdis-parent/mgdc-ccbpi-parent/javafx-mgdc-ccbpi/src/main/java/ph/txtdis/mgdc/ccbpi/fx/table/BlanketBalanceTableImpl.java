package ph.txtdis.mgdc.ccbpi.fx.table;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.TEXT;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.control.TableColumn;
import ph.txtdis.dto.SalesItemVariance;
import ph.txtdis.fx.table.Column;
import ph.txtdis.mgdc.ccbpi.service.BlanketBalanceService;

@Scope("prototype")
@Component("blanketBalanceTable")
public class BlanketBalanceTableImpl extends AbstractVarianceTable<BlanketBalanceService> implements BlanketBalanceTable {

	@Autowired
	private Column<SalesItemVariance, String> orderNo, customer;

	@Override
	protected void addProperties() {
		menu.setMenu(service, this);
	}

	@Override
	protected void buildColumns() {
		super.buildColumns();
		orderNo.ofType(TEXT).build("OCS No.", "orderNo");
		customer.ofType(TEXT).build("Outlet", "customer");
	}

	@Override
	protected List<TableColumn<SalesItemVariance, ?>> columns() {
		return asList(seller, orderNo, customer, id, item, expected, actual, returned, variance, value);
	}
}
