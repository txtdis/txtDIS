package ph.txtdis.mgdc.fx.table;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.RemittanceDetail;
import ph.txtdis.fx.table.AbstractPaymentTable;
import ph.txtdis.fx.table.AppendContextMenuImpl;
import ph.txtdis.fx.table.PaymentTable;
import ph.txtdis.mgdc.fx.dialog.PaymentDialog;
import ph.txtdis.mgdc.service.AdjustableInputtedPaymentDetailedRemittanceService;

@Scope("prototype")
@Component("paymentTable")
public class PaymentTableImpl //
	extends AbstractPaymentTable //
	implements PaymentTable {

	@Autowired
	private AppendContextMenuImpl<RemittanceDetail> append;

	@Autowired
	private PaymentDialog dialog;

	@Autowired
	private AdjustableInputtedPaymentDetailedRemittanceService service;

	@Override
	protected void addProperties() {
		append.addMenu(this, dialog, service);
	}
}
