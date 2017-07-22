package ph.txtdis.fx.validator;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import ph.txtdis.fx.control.AppFieldImpl;

public class DecimalInputValidator //
		implements ChangeListener<String> {

	private boolean ignore;
	private AppFieldImpl<?> input;
	private String restrict;

	public DecimalInputValidator(AppFieldImpl<?> input) {
		this.input = input;
		this.restrict = "[0-9-.,()₱%]";
	}

	@Override
	public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		if (ignore || newValue == null)
			return;
		if (!newValue.matches(restrict + "*") || (oldValue.contains(".") && newValue.endsWith("."))
				|| (newValue.length() > 1 && newValue.endsWith("-"))) {
			ignore = true;
			input.setText(oldValue);
			ignore = false;
		}
	}
}
