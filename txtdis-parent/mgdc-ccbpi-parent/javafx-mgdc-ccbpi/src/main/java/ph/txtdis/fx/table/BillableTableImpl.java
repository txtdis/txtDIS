package ph.txtdis.fx.table;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.control.TableColumn;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.fx.dialog.BillableDialog;
import ph.txtdis.service.CokeBillableService;

@Scope("prototype")
@Component("billableTable")
public class BillableTableImpl extends AbstractBeverageBillableTable<CokeBillableService, BillableDialog> {

	@Override
	protected List<TableColumn<BillableDetail, ?>> columns() {
		List<TableColumn<BillableDetail, ?>> l = new ArrayList<>(super.columns());
		l.addAll(asList(itemVendorId, name, quality, uom, price, qtyColumn(), subtotal));
		return l;
	}

	@Override
	protected String bookedQtyColumnName() {
		if (service.isAnOrderConfirmation())
			return netQtyColumnName();
		return "Loaded";
	}

	@Override
	protected String returnedQtyColumnName() {
		//TODO
		//if (service.isASalesReturn())
		//	return netQtyColumnName();
		return "Returned";
	}

	@Override
	protected String qtyInFractions(String type) {
		return inFractions(type + "Qty");
	}

	private String inFractions(String qtyType) {
		return qtyType + "InFractions";
	}

	@Override
	protected String netQtyColumnName() {
		return "Quantity";
	}

	@Override
	public String qty() {
		return inFractions(super.qty());
	}

	@Override
	protected TableColumn<BillableDetail, ?> qtyColumn() {
		if (service.isAnOrderConfirmation())
			return bookedQtyInFractions;
		//TODO
		//		if (service.isASalesReturn())
		//			return returnedQtyInFractions;
		return netQtyInFractions;
	}
}
