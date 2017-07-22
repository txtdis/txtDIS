package ph.txtdis.dyvek.fx.dialog;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.dto.Keyed;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.fx.control.LabeledDatePicker;
import ph.txtdis.fx.control.LabeledField;
import ph.txtdis.fx.dialog.AbstractCheckDialog;
import ph.txtdis.service.BankDrawnCheckService;

public abstract class AbstractDatedCheckDialog<T extends Keyed<Long>, S extends BankDrawnCheckService<T>> //
		extends AbstractCheckDialog<T, S> {

	@Autowired
	protected LabeledDatePicker datePicker;

	@Override
	protected List<InputNode<?>> addNodes() {
		List<InputNode<?>> l = new ArrayList<>(super.addNodes());
		l.add(1, datePicker.name("Date"));
		return l;
	}

	@Override
	protected LabeledField<Long> checkIdInput() {
		checkIdInput = super.checkIdInput();
		checkIdInput.onAction(e -> verifyCheck(bankCombo.getValue(), checkIdInput.getValue()));
		return checkIdInput;
	}

	private void verifyCheck(String bank, Long checkId) {
		if (bank != null && checkId != 0)
			try {
				service.verifyCheck(bank, checkId);
			} catch (Exception e) {
				resetNodesOnError(e);
			}
	}
}
