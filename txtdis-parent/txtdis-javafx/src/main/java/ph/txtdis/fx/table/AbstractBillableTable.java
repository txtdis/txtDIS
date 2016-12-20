package ph.txtdis.fx.table;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.CURRENCY;
import static ph.txtdis.type.Type.ENUM;
import static ph.txtdis.type.Type.ID;
import static ph.txtdis.type.Type.TEXT;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.scene.control.TableColumn;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.fx.dialog.BillableDialog;
import ph.txtdis.service.BillableService;
import ph.txtdis.type.QualityType;
import ph.txtdis.type.UomType;

public abstract class AbstractBillableTable<AS extends BillableService, BD extends BillableDialog> // 
		extends AbstractTableView<BillableDetail> //
		implements BillableTable {

	@Autowired
	private AppendContextMenu<BillableDetail> append;

	@Autowired
	private DeleteContextMenu<BillableDetail> subtract;

	@Autowired
	private Column<BillableDetail, Long> id;

	@Autowired
	protected Column<BillableDetail, BigDecimal> price, subtotal;

	@Autowired
	protected Column<BillableDetail, QualityType> quality;

	@Autowired
	protected Column<BillableDetail, String> name;

	@Autowired
	protected Column<BillableDetail, UomType> uom;

	@Autowired
	protected AS service;

	@Autowired
	protected BD dialog;

	@Override
	@SuppressWarnings("unchecked")
	public AbstractBillableTable<AS, BD> build() {
		return (AbstractBillableTable<AS, BD>) super.build();
	}

	protected void initializeColumns() {
		id.ofType(ID).build("ID No.", "id");
		name.ofType(TEXT).width(180).build("Name", "itemName");
		uom.ofType(ENUM).build("UOM", "uom");
		quality.ofType(ENUM).build("Quality", "quality");
		price.ofType(CURRENCY).build("Price", "priceValue");
		subtotal.ofType(CURRENCY).build("Subtotal", subtotal());
	}

	protected String subtotal() {
		return "finalSubtotalValue";
	}

	protected String qty() {
		return "qty";
	}

	protected List<TableColumn<BillableDetail, ?>> columns() {
		return asList(id, name, uom, quality);
	}

	protected abstract TableColumn<BillableDetail, ?> qtyColumn();

	@Override
	protected void addColumns() {
		initializeColumns();
		getColumns().setAll(columns());
	}

	@Override
	protected void addProperties() {
		append.addMenu(this, dialog, service);
		subtract.addMenu(this);
	}
}
