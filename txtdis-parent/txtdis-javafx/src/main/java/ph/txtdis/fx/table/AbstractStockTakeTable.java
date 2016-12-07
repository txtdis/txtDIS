package ph.txtdis.fx.table;

import static ph.txtdis.type.Type.ENUM;
import static ph.txtdis.type.Type.ID;
import static ph.txtdis.type.Type.TEXT;

import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.dto.StockTakeDetail;
import ph.txtdis.fx.dialog.StockTakeDialog;
import ph.txtdis.type.QualityType;
import ph.txtdis.type.UomType;

public abstract class AbstractStockTakeTable extends AbstractTableView<StockTakeDetail> {

	@Autowired
	private AppendContextMenu<StockTakeDetail> append;

	@Autowired
	private DeleteContextMenu<StockTakeDetail> subtract;

	@Autowired
	private Column<StockTakeDetail, Long> id;

	@Autowired
	private Column<StockTakeDetail, String> name;

	@Autowired
	private Column<StockTakeDetail, UomType> uom;

	@Autowired
	private Column<StockTakeDetail, QualityType> quality;

	@Autowired
	private StockTakeDialog dialog;

	@Override
	@SuppressWarnings("unchecked")
	protected void addColumns() {
		getColumns().setAll(//
				id.ofType(ID).build("ID No.", "id"), //
				name.ofType(TEXT).width(180).build("Name", "name"), //
				uom.ofType(ENUM).build("UOM", "uom"), //
				quality.ofType(ENUM).build("Quality", "quality"), //
				quantityColumn()); //
	}

	protected abstract Column<StockTakeDetail, ?> quantityColumn();

	@Override
	protected void addProperties() {
		append.addMenu(this, dialog);
		subtract.addMenu(this);
	}
}
