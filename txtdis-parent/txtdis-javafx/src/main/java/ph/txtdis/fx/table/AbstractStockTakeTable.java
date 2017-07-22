package ph.txtdis.fx.table;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.ENUM;
import static ph.txtdis.type.Type.ID;
import static ph.txtdis.type.Type.TEXT;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.scene.control.TableColumn;
import ph.txtdis.dto.StockTakeDetail;
import ph.txtdis.fx.dialog.StockTakeDialog;
import ph.txtdis.type.QualityType;
import ph.txtdis.type.UomType;

public abstract class AbstractStockTakeTable extends AbstractTable<StockTakeDetail> {

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
	protected List<TableColumn<StockTakeDetail, ?>> addColumns() {
		return asList( //
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
