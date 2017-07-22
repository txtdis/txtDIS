package ph.txtdis.mgdc.ccbpi.fx.table;

import static ph.txtdis.type.Type.CODE;
import static ph.txtdis.type.Type.ENUM;
import static ph.txtdis.type.Type.FRACTION;
import static ph.txtdis.type.Type.TEXT;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.math.Fraction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.control.TableColumn;
import ph.txtdis.dto.PickListDetail;
import ph.txtdis.fx.table.AbstractTable;
import ph.txtdis.fx.table.AppendContextMenu;
import ph.txtdis.fx.table.Column;
import ph.txtdis.fx.table.DeleteContextMenu;
import ph.txtdis.mgdc.ccbpi.fx.dialog.LoadReturnDialog;
import ph.txtdis.mgdc.ccbpi.service.LoadReturnService;
import ph.txtdis.type.UomType;

@Scope("prototype")
@Component("loadReturnTable")
public class LoadReturnTableImpl //
		extends AbstractTable<PickListDetail> //
		implements LoadReturnTable {

	@Autowired
	private AppendContextMenu<PickListDetail> append;

	@Autowired
	private DeleteContextMenu<PickListDetail> subtract;

	@Autowired
	private Column<PickListDetail, Fraction> returned;

	@Autowired
	private Column<PickListDetail, String> itemVendorId, name;

	@Autowired
	private Column<PickListDetail, UomType> uom;

	@Autowired
	private LoadReturnDialog dialog;

	@Autowired
	private LoadReturnService service;

	@Override
	protected List<TableColumn<PickListDetail, ?>> addColumns() {
		return Arrays.asList(//
				itemVendorId.ofType(CODE).build("ID No.", "itemVendorNo"), //
				name.ofType(TEXT).width(180).build("Name", "itemName"), //
				uom.ofType(ENUM).build("UOM", "uom"), //
				returned.ofType(FRACTION).build("Quantity", "returnedQtyInFractions"));
	}

	@Override
	protected void addProperties() {
		append.addMenu(this, dialog, service);
		subtract.addMenu(this);
	}
}
