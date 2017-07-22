package ph.txtdis.mgdc.fx.table;

import static ph.txtdis.type.Type.DATE;
import static ph.txtdis.type.Type.TEXT;
import static ph.txtdis.type.Type.TIMESTAMP;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.control.TableColumn;
import ph.txtdis.dto.Account;
import ph.txtdis.fx.table.AbstractTable;
import ph.txtdis.fx.table.AppendContextMenu;
import ph.txtdis.fx.table.Column;
import ph.txtdis.mgdc.fx.dialog.AccountDialog;

@Scope("prototype")
@Component("accountTable")
public class AccountTable extends AbstractTable<Account> {

	@Autowired
	private AppendContextMenu<Account> append;

	@Autowired
	private Column<Account, String> assignedSeller;

	@Autowired
	private Column<Account, LocalDate> startDate;

	@Autowired
	private Column<Account, String> assignedBy;

	@Autowired
	private Column<Account, ZonedDateTime> assignedOn;

	@Autowired
	private AccountDialog dialog;

	@Override
	protected List<TableColumn<Account, ?>> addColumns() {
		return Arrays.asList(//
				assignedSeller.ofType(TEXT).width(100).build("Assigned\nSeller", "seller"), startDate.ofType(DATE).build("Start\nDate", "startDate"),
				assignedBy.ofType(TEXT).width(100).build("Assigned\nby", "createdBy"), assignedOn.ofType(TIMESTAMP).build("Assigned\non", "createdOn"));
	}

	@Override
	protected void addProperties() {
		append.addMenu(this, dialog);
	}
}
