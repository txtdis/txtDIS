package ph.txtdis.mgdc.gsm.fx.table;

import javafx.scene.control.TableColumn;
import org.apache.commons.lang3.math.Fraction;
import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.fx.table.Column;
import ph.txtdis.mgdc.fx.dialog.BillableDialog;
import ph.txtdis.mgdc.gsm.service.BillableService;

import static ph.txtdis.type.Type.CODE;
import static ph.txtdis.type.Type.FRACTION;

public class AbstractBeverageBillableTable<AS extends BillableService, BD extends BillableDialog> //
	extends AbstractBillableTable<AS, BD> {

	@Autowired
	protected Column<BillableDetail, Fraction> bookedQtyInFractions, soldQtyInFractions, returnedQtyInFractions,
		netQtyInFractions;

	@Autowired
	protected Column<BillableDetail, String> itemVendorId;

	@Override
	public void initializeColumns() {
		super.initializeColumns();
		itemVendorId.ofType(CODE).build("ID No.", "itemVendorNo");
		bookedQtyInFractions.ofType(FRACTION).build(bookedQtyColumnName(), qtyInFractions("initial"));
		netQtyInFractions.ofType(FRACTION).build(netQtyColumnName(), netQty());
		returnedQtyInFractions.ofType(FRACTION).build(returnedQtyColumnName(), qtyInFractions("returned"));
		soldQtyInFractions.ofType(FRACTION).build("Sold", qtyInFractions("sold"));
	}

	protected String bookedQtyColumnName() {
		return netQtyColumnName();
	}

	protected String qtyInFractions(String type) {
		return inFractions(type + "Qty");
	}

	protected String netQtyColumnName() {
		return "Quantity";
	}

	@Override
	public String netQty() {
		return inFractions(super.netQty());
	}

	protected String returnedQtyColumnName() {
		return netQtyColumnName();
	}

	private String inFractions(String qtyType) {
		return qtyType + "InFractions";
	}

	@Override
	protected TableColumn<BillableDetail, ?> qtyColumn() {
		return netQtyInFractions;
	}
}
