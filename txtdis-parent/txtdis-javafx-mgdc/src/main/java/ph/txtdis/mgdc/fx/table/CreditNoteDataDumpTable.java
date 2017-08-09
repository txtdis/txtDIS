package ph.txtdis.mgdc.fx.table;

import javafx.scene.control.TableColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.CreditNoteDump;
import ph.txtdis.fx.table.AbstractTable;
import ph.txtdis.fx.table.Column;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.*;

@Scope("prototype")
@Component("creditNoteDumpTable")
public class CreditNoteDataDumpTable
	extends AbstractTable<CreditNoteDump> {

	@Autowired
	private Column<CreditNoteDump, Long> id;

	@Autowired
	private Column<CreditNoteDump, LocalDate> creditDate, paymentDate;

	@Autowired
	private Column<CreditNoteDump, String> description, paymentRemarks, reference, remarks;

	@Autowired
	private Column<CreditNoteDump, BigDecimal> balance, payment, total;

	@Override
	protected List<TableColumn<CreditNoteDump, ?>> addColumns() {
		return asList( //
			id.ofType(ID).build("ID No.", "id"), //
			creditDate.ofType(DATE).build("Date", "creditDate"), //
			description.ofType(TEXT).build("Description", "description"), //
			total.ofType(CURRENCY).build("Total", "totalValue"), //
			balance.ofType(CURRENCY).build("Balance", "balanceValue"), //
			remarks.ofType(TEXT).width(480).build("Remarks", "remarks"), //
			paymentDate.ofType(DATE).build("Date", "paymentDate"), //
			reference.ofType(TEXT).width(120).build("Reference", "reference"), //
			payment.ofType(CURRENCY).build("Payment", "paymentValue"), //
			paymentRemarks.ofType(TEXT).width(480).build("Remarks", "paymentRemarks"));
	}
}
