package ph.txtdis.mgdc.gsm.fx.table;

import javafx.scene.control.TableColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.ItemFamily;
import ph.txtdis.dto.ItemTree;
import ph.txtdis.fx.table.AbstractTable;
import ph.txtdis.fx.table.AppendContextMenuImpl;
import ph.txtdis.fx.table.Column;
import ph.txtdis.mgdc.gsm.fx.dialog.ItemTreeDialog;

import java.time.ZonedDateTime;
import java.util.List;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.TEXT;
import static ph.txtdis.type.Type.TIMESTAMP;

@Scope("prototype")
@Component("itemTreeTable")
public class ItemTreeTable
	extends AbstractTable<ItemTree> {

	@Autowired
	private AppendContextMenuImpl<ItemTree> append;

	@Autowired
	private Column<ItemTree, ItemFamily> family;

	@Autowired
	private Column<ItemTree, ItemFamily> parent;

	@Autowired
	private Column<ItemTree, String> createdBy;

	@Autowired
	private Column<ItemTree, ZonedDateTime> createdOn;

	@Autowired
	private ItemTreeDialog dialog;

	@Override
	protected List<TableColumn<ItemTree, ?>> addColumns() {
		return asList( //
			family.ofType(TEXT).width(180).build("Family", "family"), //
			parent.ofType(TEXT).width(180).build("Parent", "parent"), //
			createdBy.ofType(TEXT).width(120).build("Issued by", "createdBy"), //
			createdOn.ofType(TIMESTAMP).build("Issued on", "createdOn"));
	}

	@Override
	protected void addProperties() {
		append.addMenu(this, dialog);
	}
}
