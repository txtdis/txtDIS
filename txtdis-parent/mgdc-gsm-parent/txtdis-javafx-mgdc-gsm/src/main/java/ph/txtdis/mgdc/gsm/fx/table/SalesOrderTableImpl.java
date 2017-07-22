package ph.txtdis.mgdc.gsm.fx.table;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.control.TableColumn;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.mgdc.gsm.fx.dialog.SalesOrderDialog;
import ph.txtdis.mgdc.gsm.service.GsmBookingService;

@Scope("prototype")
@Component("salesOrderTable")
public class SalesOrderTableImpl //
		extends AbstractBeverageBillableTable<GsmBookingService, SalesOrderDialog> implements SalesOrderTable {

	@Override
	protected String bookedQtyColumnName() {
		if (service.isLoadOrder())
			return "Loaded";
		return super.bookedQtyColumnName();
	}

	@Override
	protected List<TableColumn<BillableDetail, ?>> addColumns() {
		List<TableColumn<BillableDetail, ?>> l = new ArrayList<>(super.addColumns());
		if (service.isLoadOrder())
			l.addAll(asList(bookedQtyInFractions, soldQtyInFractions, returnedQtyInFractions, qtyColumn()));
		else
			l.addAll(asList(price, bookedQtyInFractions, subtotal));
		return l;
	}

	@Override
	protected String netQtyColumnName() {
		if (service.isLoadOrder())
			return "Variance";
		return super.netQtyColumnName();
	}

	@Override
	protected String returnedQtyColumnName() {
		if (service.isLoadOrder())
			return "Returned";
		return netQtyColumnName();
	}

	@Override
	protected String subtotal() {
		return "initialSubtotalValue";
	}
}
