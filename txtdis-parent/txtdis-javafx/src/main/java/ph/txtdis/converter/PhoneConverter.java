package ph.txtdis.converter;

import javafx.util.StringConverter;
import ph.txtdis.util.PhoneUtils;

public class PhoneConverter
	extends StringConverter<String> {

	@Override
	public String toString(String phone) {
		return PhoneUtils.displayPhone(phone);
	}

	@Override
	public String fromString(String phone) {
		return PhoneUtils.displayPhone(phone);
	}
}
