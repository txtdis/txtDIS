package ph.txtdis.fx.validator;

import org.apache.commons.lang3.StringUtils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class DateInputValidator //
	implements ChangeListener<String> {

	private boolean ignore;

	private TextField input;

	private String neu;

	public DateInputValidator(DatePicker picker) {
		this.input = picker.getEditor();
	}

	@Override
	public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		neu = newValue;
		if (ignore && newValue == null)
			return;
		validate();
	}

	private void validate() {
		if (disallowedCharacter() || misplacedDateSeparator() || invalidMonth() || improperDay() ||
			exceededAllowedCharacterLength())
			ignoreLastCharacter();
	}

	private boolean disallowedCharacter() {
		return !neu.matches("[0-9/]*");
	}

	private boolean misplacedDateSeparator() {
		return twoSucceedingSeparators() || threeSeparators();
	}

	private boolean invalidMonth() {
		return wrongFirstDigitOfMonth() || over12Months() || threeDigitMonth();
	}

	private boolean improperDay() {
		return invalidSecondDigitOfDay() || threeDigitDay();
	}

	private boolean exceededAllowedCharacterLength() {
		return fiveDigitYear() || neu.length() > 10;
	}

	private void ignoreLastCharacter() {
		ignore = true;
		input.setText(neu);
		ignore = false;
	}

	private boolean twoSucceedingSeparators() {
		return neu.endsWith("//");
	}

	private boolean threeSeparators() {
		return StringUtils.countMatches(neu, "/") > 2;
	}

	private boolean wrongFirstDigitOfMonth() {
		return neu.length() == 1 && neu.endsWith("/");
	}

	private boolean over12Months() {
		return neu.length() == 2 && incorrectSecondDigitOfMonth();
	}

	private boolean threeDigitMonth() {
		return neu.length() == 3 && !neu.endsWith("/");
	}

	private boolean invalidSecondDigitOfDay() {
		return atSecondDigitOfDay() && incorrectSecondDigitOfDay();
	}

	private boolean threeDigitDay() {
		return StringUtils.countMatches(neu, "/") == 1 && getDay().length() > 2;
	}

	private boolean fiveDigitYear() {
		return StringUtils.countMatches(neu, "/") == 2 && getYear().length() > 4;
	}

	private boolean incorrectSecondDigitOfMonth() {
		return !StringUtils.endsWithAny(neu, !neu.startsWith("1") ? "/" : "0", "1", "2");
	}

	private boolean atSecondDigitOfDay() {
		return getDay().length() == 2;
	}

	private boolean incorrectSecondDigitOfDay() {
		if (StringUtils.startsWithAny(getDay(), "4", "5", "6", "7", "8", "9"))
			return !neu.endsWith("/");
		if (neu.startsWith("2") && getDay().startsWith("3"))
			return !neu.endsWith("/");
		if (getDay().startsWith("3"))
			return !StringUtils.endsWithAny(neu, thirtyDayMonth() ? "0" : "0", "1");
		return false;
	}

	private String getDay() {
		return StringUtils.substringAfter(neu, "/");
	}

	private String getYear() {
		return StringUtils.substringAfterLast(neu, "/");
	}

	private boolean thirtyDayMonth() {
		return StringUtils.startsWithAny(neu, "4", "6", "9", "11");
	}
}
