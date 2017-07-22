package ph.txtdis.mgdc.fx.table;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.CURRENCY;
import static ph.txtdis.type.Type.DATE;
import static ph.txtdis.type.Type.TEXT;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.control.TableColumn;
import ph.txtdis.dto.CreditNotePayment;
import ph.txtdis.fx.table.AbstractTable;
import ph.txtdis.fx.table.AppendContextMenu;
import ph.txtdis.fx.table.Column;
import ph.txtdis.mgdc.fx.dialog.CreditNotePaymentDialog;

@Scope("prototype")
@Component("creditNotePaymentTable")
public class CreditNotePaymentTable //
		extends AbstractTable<CreditNotePayment> {

	@Autowired
	private AppendContextMenu<CreditNotePayment> append;

	@Autowired
	private Column<CreditNotePayment, LocalDate> paymentDate;

	@Autowired
	private Column<CreditNotePayment, String> referenceId, remarks;

	@Autowired
	private Column<CreditNotePayment, BigDecimal> payment;

	@Autowired
	private CreditNotePaymentDialog dialog;

	@Override
	protected List<TableColumn<CreditNotePayment, ?>> addColumns() {
		return asList( //
				paymentDate.ofType(DATE).build("Date", "paymentDate"), //
				referenceId.ofType(TEXT).width(120).build("Reference", "reference"), //
				payment.ofType(CURRENCY).build("Payment", "paymentValue"), //
				remarks.ofType(TEXT).width(360).build("Remarks", "paymentRemarks"));
	}

	@Override
	protected void addProperties() {
		append.addMenu(this, dialog);
	}
}
