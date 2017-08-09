package ph.txtdis.fx.dialog;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.ID;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.scene.control.Button;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.fx.control.LabeledCombo;
import ph.txtdis.fx.control.LabeledField;
import ph.txtdis.service.FinancialService;

public abstract class AbstractCheckDialog<T, FS extends FinancialService> //
	extends AbstractFieldDialog<T> {

	@Autowired
	protected LabeledField<Long> checkIdInput;

	@Autowired
	protected LabeledCombo<String> bankCombo;

	@Autowired
	protected FS service;

	private Long checkId;

	private String bank;

	@Override
	protected AppButton addButton() {
		AppButton b = super.addButton();
		b.setText(addButtonLabelName());
		return b;
	}

	protected abstract String addButtonLabelName();

	@Override
	protected void addItem() {
		checkId = checkIdInput.getValue();
		bank = bankCombo.getValue();
		super.addItem();
	}

	@Override
	protected List<InputNode<?>> addNodes() {
		return asList(bankCombo(), checkIdInput());
	}

	protected LabeledCombo<String> bankCombo() {
		return bankCombo.name("Bank").items(service.listBanks()).build();
	}

	protected LabeledField<Long> checkIdInput() {
		return checkIdInput.name("Check No.").build(ID);
	}

	public String getBank() {
		return bank;
	}

	public Long getCheckId() {
		return checkId;
	}

	@Override
	protected void nullData() {
		super.nullData();
		bank = null;
		checkId = null;
	}
}
