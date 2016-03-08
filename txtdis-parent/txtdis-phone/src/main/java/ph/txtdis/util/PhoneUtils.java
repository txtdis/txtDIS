package ph.txtdis.util;

import static com.google.i18n.phonenumbers.PhoneNumberUtil.getInstance;
import static com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat.E164;
import static com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType.MOBILE;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

public class PhoneUtils {

	public final static DecimalFormat NO_COMMA_DECIMAL = new DecimalFormat("0.00;(0.00)");
	public final static DecimalFormat TWO_PLACE_DECIMAL = new DecimalFormat("#,##0.00;(#,##0.00)");
	public final static DecimalFormat FOUR_PLACE_DECIMAL = new DecimalFormat("#,##0.0000;(#,##0.0000)");
	public final static DecimalFormat INTEGER = new DecimalFormat("#,##0;(#,##0)");
	public final static DecimalFormat NO_COMMA_INTEGER = new DecimalFormat("0;(0)");

	public final static BigDecimal HUNDRED = new BigDecimal("100");

	public static String displayPhone(String number) {
		PhoneNumber phone = parsePhone(number);
		return phone == null ? "" : phoneUtil().format(phone, PhoneNumberFormat.NATIONAL);
	}

	public static String formatPhone(PhoneNumber number) {
		return number == null || !isPhone(number) ? "" : phoneUtil().formatNumberForMobileDialing(number, "PH", true);
	}

	public static boolean isMobile(PhoneNumber phone) {
		return !isPhone(phone) ? false : phoneUtil().getNumberType(phone) == MOBILE;
	}

	public static boolean isPhone(PhoneNumber phone) {
		return phone == null ? false : phoneUtil().isValidNumberForRegion(phone, "PH");
	}

	public static boolean isPhone(String phone) {
		return parsePhone(phone) == null ? false : true;
	}

	public static PhoneNumber parsePhone(String text) {
		try {
			PhoneNumber phone = phoneUtil().parse(text, "PH");
			return isPhone(phone) ? phone : null;
		} catch (NumberParseException e) {
			return null;
		}
	}

	public static String persistPhone(String number) {
		PhoneNumber phone = parsePhone(number);
		return phone == null ? "" : phoneUtil().format(phone, E164);
	}

	private static PhoneNumberUtil phoneUtil() {
		return getInstance();
	}
}
