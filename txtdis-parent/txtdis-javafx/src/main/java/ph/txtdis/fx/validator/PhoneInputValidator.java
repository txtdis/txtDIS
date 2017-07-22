package ph.txtdis.fx.validator;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import ph.txtdis.fx.control.AppFieldImpl;

public class PhoneInputValidator //
		implements ChangeListener<String> {

	private boolean ignore;
	private AppFieldImpl<?> input;
	private String restrict;

	public PhoneInputValidator(AppFieldImpl<?> input) {
		this.input = input;
		this.restrict = "[0-9 ]";
	}

	@Override
	public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		if (ignore || newValue == null)
			return;
		if (!newValue.matches(restrict + "*") || (oldValue.endsWith(" ") && newValue.endsWith(" "))) {
			ignore = true;
			input.setText(oldValue);
			ignore = false;
		} else if (newValue.length() > 13) {
			ignore = true;
			input.setText(newValue.substring(0, 13));
			ignore = false;
		}
	}
}
