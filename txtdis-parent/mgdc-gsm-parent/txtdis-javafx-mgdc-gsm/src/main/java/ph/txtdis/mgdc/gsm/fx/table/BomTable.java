package ph.txtdis.mgdc.gsm.fx.table;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.FOURPLACE;
import static ph.txtdis.type.Type.OTHERS;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.control.TableColumn;
import ph.txtdis.dto.Bom;
import ph.txtdis.fx.table.AbstractTable;
import ph.txtdis.fx.table.AppendContextMenuImpl;
import ph.txtdis.fx.table.Column;
import ph.txtdis.mgdc.gsm.dto.Item;
import ph.txtdis.mgdc.gsm.fx.dialog.BomDialog;

@Scope("prototype")
@Component("bomTable")
public class BomTable extends AbstractTable<Bom> {

	@Autowired
	private AppendContextMenuImpl<Bom> append;

	@Autowired
	private BomDialog dialog;

	@Autowired
	private Column<Bom, Item> item;

	@Autowired
	private Column<Bom, BigDecimal> qty;

	@Override
	protected List<TableColumn<Bom, ?>> addColumns() {
		return asList( //
				item.ofType(OTHERS).width(180).build("Item", "part"), //
				qty.ofType(FOURPLACE).build("Quantity", "qty"));
	}

	@Override
	protected void addProperties() {
		append.addMenu(this, dialog);
		setOnEmpty("A promo item must have one purchased part,\n"//
				+ "and bundled requires at least two");
	}
}
