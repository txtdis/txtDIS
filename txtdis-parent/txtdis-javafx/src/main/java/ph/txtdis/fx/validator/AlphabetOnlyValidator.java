package ph.txtdis.fx.validator;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import ph.txtdis.fx.control.AppFieldImpl;

public class AlphabetOnlyValidator //
	implements ChangeListener<String> {

	private boolean ignore;

	private int maxLength;

	private String restrict;

	private AppFieldImpl<?> input;

	public AlphabetOnlyValidator(AppFieldImpl<?> input) {
		this.input = input;
		this.restrict = "[a-zA-ZX]";
		maxLength = (int) input.getMinWidth() / 30;
	}

	@Override
	public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		if (ignore || newValue == null)
			return;
		if (!newValue.matches(restrict + "*")) {
			ignore = true;
			input.setText(oldValue);
			ignore = false;
		}
		else if (newValue.length() > maxLength) {
			ignore = true;
			input.setText(newValue.substring(0, maxLength));
			ignore = false;
		}
		else {
			input.setText(newValue.toUpperCase());
		}
	}
}
