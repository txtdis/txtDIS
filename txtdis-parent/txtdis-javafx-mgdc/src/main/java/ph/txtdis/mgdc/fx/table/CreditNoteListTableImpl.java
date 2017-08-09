package ph.txtdis.mgdc.fx.table;

import javafx.scene.control.TableColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.CreditNote;
import ph.txtdis.fx.table.AbstractTable;
import ph.txtdis.fx.table.Column;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.*;

@Scope("prototype")
@Component("creditNoteListTable")
public class CreditNoteListTableImpl //
	extends AbstractTable<CreditNote> //
	implements CreditNoteListTable {

	@Autowired
	private Column<CreditNote, Long> id;

	@Autowired
	private Column<CreditNote, LocalDate> creditDate;

	@Autowired
	private Column<CreditNote, String> description, reference;

	@Autowired
	private Column<CreditNote, BigDecimal> balanceValue, totalValue;

	@Override
	protected List<TableColumn<CreditNote, ?>> addColumns() {
		return asList( //
			id.ofType(ID).build("C/N No.", "id"), //
			creditDate.ofType(DATE).build("Date", "creditDate"), //
			reference.ofType(TEXT).build("Reference", "reference"), //
			description.ofType(TEXT).build("Description", "description"), //
			totalValue.ofType(CURRENCY).build("Total", "totalValue"), //
			balanceValue.ofType(CURRENCY).build("Balance", "balanceValue"));
	}
}
