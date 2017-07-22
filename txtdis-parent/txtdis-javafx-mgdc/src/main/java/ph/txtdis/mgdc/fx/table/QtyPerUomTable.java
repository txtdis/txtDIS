package ph.txtdis.mgdc.fx.table;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.BOOLEAN;
import static ph.txtdis.type.Type.ENUM;
import static ph.txtdis.type.Type.QUANTITY;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.control.TableColumn;
import ph.txtdis.dto.QtyPerUom;
import ph.txtdis.fx.table.AbstractTable;
import ph.txtdis.fx.table.AppendContextMenuImpl;
import ph.txtdis.fx.table.Column;
import ph.txtdis.mgdc.fx.dialog.QtyPerUomDialogImpl;
import ph.txtdis.type.UomType;

@Scope("prototype")
@Component("qtyPerUomTable")
public class QtyPerUomTable //
		extends AbstractTable<QtyPerUom> {

	@Autowired
	private AppendContextMenuImpl<QtyPerUom> append;

	@Autowired
	private Column<QtyPerUom, UomType> uom;

	@Autowired
	private Column<QtyPerUom, BigDecimal> qty;

	@Autowired
	private Column<QtyPerUom, Boolean> isPurchased;

	@Autowired
	private Column<QtyPerUom, Boolean> isSold;

	@Autowired
	private Column<QtyPerUom, Boolean> isReported;

	@Autowired
	private QtyPerUomDialogImpl dialog;

	@Override
	protected List<TableColumn<QtyPerUom, ?>> addColumns() {
		return asList( //
				uom.ofType(ENUM).build("UOM", "uom"), //
				qty.ofType(QUANTITY).build("Quantity", "qty"), //
				isPurchased.ofType(BOOLEAN).width(120).build("Purchased", "purchased"), //
				isSold.ofType(BOOLEAN).width(120).build("Sold", "sold"), //
				isReported.ofType(BOOLEAN).width(120).build("Reported", "reported"));
	}

	@Override
	protected void addProperties() {
		append.addMenu(this, dialog);
		setOnEmpty("'EA' is the UOM referenced by the unit price,\n"//
				+ "all requires one report UOM, and\n"//
				+ "one purchased UOM is needed, if bought,\n" //
				+ "however, free items cannot be sold");
	}
}
