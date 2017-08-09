package ph.txtdis.fx.table;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.ID;
import static ph.txtdis.type.Type.TEXT;
import static ph.txtdis.type.Type.TIMESTAMP;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.scene.control.TableColumn;
import ph.txtdis.dto.Keyed;
import ph.txtdis.fx.dialog.InputtedDialog;

public abstract class AbstractNameListTable<S extends Keyed<?>, D extends InputtedDialog<S>> //
	extends AbstractTable<S> {

	@Autowired
	protected Column<S, Long> id;

	@Autowired
	protected Column<S, String> name;

	@Autowired
	protected Column<S, String> createdBy;

	@Autowired
	protected Column<S, ZonedDateTime> createdOn;

	@Autowired
	private AppendContextMenu<S> append;

	@Autowired
	private D dialog;

	@Override
	protected List<TableColumn<S, ?>> addColumns() {
		return asList( //
			id.ofType(ID).build("ID No.", "id"), //
			name.ofType(TEXT).width(240).build("Name", "name"), //
			createdBy.ofType(TEXT).width(100).build("Created by", "createdBy"), //
			createdOn.ofType(TIMESTAMP).build("Created on", "createdOn"));
	}

	@Override
	protected void addProperties() {
		append.addMenu(this, dialog);
	}
}
