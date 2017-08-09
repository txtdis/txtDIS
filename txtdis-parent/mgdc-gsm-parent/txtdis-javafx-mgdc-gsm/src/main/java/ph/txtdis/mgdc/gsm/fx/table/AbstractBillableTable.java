package ph.txtdis.mgdc.gsm.fx.table;

import javafx.scene.control.TableColumn;
import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.fx.table.AbstractTable;
import ph.txtdis.fx.table.AppendContextMenu;
import ph.txtdis.fx.table.Column;
import ph.txtdis.fx.table.DeleteContextMenu;
import ph.txtdis.mgdc.fx.dialog.BillableDialog;
import ph.txtdis.mgdc.fx.table.BillableTable;
import ph.txtdis.mgdc.gsm.service.BillableService;
import ph.txtdis.type.QualityType;
import ph.txtdis.type.UomType;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.*;

public abstract class AbstractBillableTable<AS extends BillableService, BD extends BillableDialog> // 
	extends AbstractTable<BillableDetail> //
	implements BillableTable {

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

	@Autowired
	private AppendContextMenu<BillableDetail> append;

	@Autowired
	private DeleteContextMenu<BillableDetail> subtract;

	@Autowired
	private Column<BillableDetail, Long> id;

	@Override
	@SuppressWarnings("unchecked")
	public AbstractBillableTable<AS, BD> build() {
		return (AbstractBillableTable<AS, BD>) super.build();
	}

	protected String netQty() {
		return "finalQty";
	}

	protected abstract TableColumn<BillableDetail, ?> qtyColumn();

	@Override
	protected List<TableColumn<BillableDetail, ?>> addColumns() {
		initializeColumns();
		return asList(id, name, uom, quality);
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

	@Override
	protected void addProperties() {
		append.addMenu(this, dialog, service);
		subtract.addMenu(this);
	}
}
