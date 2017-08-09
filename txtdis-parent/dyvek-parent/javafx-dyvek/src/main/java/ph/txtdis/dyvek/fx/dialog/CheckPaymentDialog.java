package ph.txtdis.dyvek.fx.dialog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.Remittance;
import ph.txtdis.dyvek.app.CashAdvanceListApp;
import ph.txtdis.dyvek.service.VendorAndClientCheckPaymentDetailedRemittanceService;
import ph.txtdis.fx.control.LabeledCombo;

import static ph.txtdis.dyvek.service.CashAdvanceService.CASH_ADVANCE;
import static ph.txtdis.type.PartnerType.VENDOR;

@Scope("prototype")
@Component("checkPaymentDialog")
public class CheckPaymentDialog //
	extends AbstractDatedCheckDialog<Remittance, VendorAndClientCheckPaymentDetailedRemittanceService> {

	private static final String PAY = "Pay";

	@Autowired
	private CashAdvanceListApp app;

	@Override
	protected String addButtonLabelName() {
		return PAY;
	}

	@Override
	protected void addItem() {
		try {
			entity = service.save(createEntity());
			refresh();
			close();
		} catch (Exception e) {
			resetNodesOnError(e);
		}
	}

	@Override
	protected Remittance createEntity() {
		service.setDraweeBank(bankCombo.getValue());
		service.setCheckId(checkIdInput.getValue());
		service.setPaymentDate(datePicker.getValue());
		return service.createEntity();
	}

	@Override
	protected LabeledCombo<String> bankCombo() {
		super.bankCombo();
		bankCombo.onAction(e -> verifyCashAdvanceIfSelected(bankCombo.getValue()));
		return bankCombo;
	}

	private void verifyCashAdvanceIfSelected(String selection) {
		if (selection.equals(CASH_ADVANCE))
			try {
				service.verifyCashAdvance(VENDOR);
				setCashAdvanceId();
			} catch (Exception e) {
				resetNodesOnError(e);
			}
	}

	private void setCashAdvanceId() {
		app.addParent(this).start();
		checkIdInput.setValue(app.getSelectedKey());
		checkIdInput.requestFocus();
	}

	@Override
	protected String headerText() {
		return PAY + " a Bill";
	}
}
