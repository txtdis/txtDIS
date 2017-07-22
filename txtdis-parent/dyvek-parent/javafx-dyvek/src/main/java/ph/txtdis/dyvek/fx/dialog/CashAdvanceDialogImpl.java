package ph.txtdis.dyvek.fx.dialog;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.CURRENCY;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dyvek.model.CashAdvance;
import ph.txtdis.dyvek.service.CashAdvanceService;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.fx.control.LabeledCombo;
import ph.txtdis.fx.control.LabeledField;
import ph.txtdis.info.Information;

@Scope("prototype")
@Component("cashAdvanceDialog")
public class CashAdvanceDialogImpl //
		extends AbstractDatedCheckDialog<CashAdvance, CashAdvanceService> //
		implements CashAdvanceDialog {

	@Autowired
	private LabeledCombo<String> customerCombo;

	@Autowired
	private LabeledField<BigDecimal> valueInput;

	@Override
	protected String addButtonLabelName() {
		return "Add";
	}

	@Override
	protected List<InputNode<?>> addNodes() {
		return asList( //
				customerCombo.name("Partner").items(service.listCustomers()).build(), //
				datePicker.name("Check Date"), //
				bankCombo(), //
				checkIdInput(), //
				valueInput.name("Amount").build(CURRENCY));
	}

	@Override
	protected CashAdvance createEntity() {
		try {
			setCashAdvanceData();
			return service.save();
		} catch (Exception | Information e) {
			resetNodesOnError(e);
			return null;
		}
	}

	private void setCashAdvanceData() {
		service.setCustomer(customerCombo.getValue());
		service.setCheckDate(datePicker.getValue());
		service.setBank(bankCombo.getValue());
		service.setCheckId(checkIdInput.getValue());
		service.setTotalValue(valueInput.getValue());
	}

	@Override
	protected String headerText() {
		return "Add New Cash Advance";
	}
}