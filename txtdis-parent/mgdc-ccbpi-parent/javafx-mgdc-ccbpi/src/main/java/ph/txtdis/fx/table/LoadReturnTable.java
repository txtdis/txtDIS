package ph.txtdis.fx.table;

import static ph.txtdis.type.Type.CODE;
import static ph.txtdis.type.Type.ENUM;
import static ph.txtdis.type.Type.FRACTION;
import static ph.txtdis.type.Type.TEXT;

import java.util.Arrays;

import org.apache.commons.lang3.math.Fraction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.PickListDetail;
import ph.txtdis.fx.dialog.LoadReturnDialog;
import ph.txtdis.service.CokePickListService;
import ph.txtdis.type.QualityType;
import ph.txtdis.type.UomType;

@Scope("prototype")
@Component("loadReturnTable")
public class LoadReturnTable extends AbstractTableView<PickListDetail> {

	@Autowired
	private AppendContextMenu<PickListDetail> append;

	@Autowired
	private DeleteContextMenu<PickListDetail> subtract;

	@Autowired
	private Column<PickListDetail, Fraction> returned;

	@Autowired
	private Column<PickListDetail, QualityType> quality;

	@Autowired
	private Column<PickListDetail, String> itemVendorId, name;

	@Autowired
	private Column<PickListDetail, UomType> uom;

	@Autowired
	private LoadReturnDialog dialog;

	@Autowired
	private CokePickListService service;

	@Override
	protected void addColumns() {
		itemVendorId.ofType(CODE).build("ID No.", "itemVendorNo");
		name.ofType(TEXT).width(180).build("Name", "itemName");
		uom.ofType(ENUM).build("UOM", "uom");
		quality.ofType(ENUM).build("Quality", "quality");
		returned.ofType(FRACTION).build("Quantity", "returnedQtyInFractions");
		getColumns().setAll(Arrays.asList(itemVendorId, name, uom, quality, returned));
	}

	@Override
	protected void addProperties() {
		append.addMenu(this, dialog, service);
		subtract.addMenu(this);
	}
}
