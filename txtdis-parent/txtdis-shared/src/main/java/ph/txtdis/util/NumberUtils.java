package ph.txtdis.util;

import org.apache.commons.lang3.math.Fraction;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_EVEN;

public class NumberUtils {

	public final static DecimalFormat INTEGER = new DecimalFormat("#,##0;(#,##0)");

	private final static DecimalFormat NO_COMMA_DECIMAL = new DecimalFormat("0.00;(0.00)");

	private final static DecimalFormat TWO_PLACE_DECIMAL = new DecimalFormat("#,##0.00;(#,##0.00)");

	private final static DecimalFormat FOUR_PLACE_DECIMAL = new DecimalFormat("#,##0.0000;(#,##0.0000)");

	private final static DecimalFormat NO_COMMA_INTEGER = new DecimalFormat("0;(0)");

	private final static BigDecimal HUNDRED = new BigDecimal("100");

	public static BigDecimal divide(int dividend, int divisor) {
		return divide(toDecimal(dividend), toDecimal(divisor));
	}

	public static BigDecimal divide(BigDecimal dividend, BigDecimal divisor) {
		if (dividend == null || isZero(divisor))
			return ZERO;
		return dividend.divide(divisor, 8, HALF_EVEN);
	}

	public static BigDecimal toDecimal(int integer) {
		return toBigDecimal(String.valueOf(integer));
	}

	public static BigDecimal toBigDecimal(String text) {
		if (text == null)
			return ZERO;
		text = text.trim()
			.replace(" ", "")
			.replace(",", "")
			.replace("(", "-")
			.replace(")", "")
			.replace("%", "")
			.replace("₱", "");
		if (text.equals("-") || text.isEmpty())
			return ZERO;
		return new BigDecimal(text);
	}

	public static boolean isNegative(BigDecimal no) {
		return no != null && no.compareTo(ZERO) < 0;
	}

	public static boolean isNegative(Integer no) {
		return no != null && no < 0;
	}

	public static boolean isNegative(Long no) {
		return no != null && no < 0;
	}

	public static boolean isPositive(BigDecimal no) {
		return no != null && no.compareTo(ZERO) > 0;
	}

	public static boolean isNegative(String text) {
		return text.startsWith("-") || (text.startsWith("(") && text.endsWith(")"));
	}

	public static String to4PlaceDecimalText(BigDecimal no) {
		return isZero(no) ? "" : FOUR_PLACE_DECIMAL.format(no);
	}

	public static BigDecimal nullIfZero(BigDecimal no) {
		return isZero(no) ? null : no;
	}

	public static Long nullIfZero(Long id) {
		return id == 0L ? null : id;
	}

	public static String printDecimal(BigDecimal no) {
		return NO_COMMA_DECIMAL.format(no);
	}

	public static String printInteger(BigDecimal no) {
		return NO_COMMA_INTEGER.format(no);
	}

	public static BigDecimal remainder(BigDecimal dividend, BigDecimal divisor) {
		if (dividend == null || isZero(divisor))
			return ZERO;
		int remainder = dividend.intValue() % divisor.intValue();
		return toDecimal(remainder);
	}

	public static String to4PlacePercentText(BigDecimal no) {
		return isZero(no) ? "" : to4PlaceDecimalText(no) + "%";
	}

	public static boolean isZero(BigDecimal no) {
		return (no == null) || (no.compareTo(ZERO) == 0);
	}

	public static String toAsNeededNoOfPlaceDecimalText(BigDecimal no) {
		return isZero(no) ? "" : isZero(no.remainder(BigDecimal.ONE)) ? INTEGER.format(no) : to2PlaceDecimalText(no);
	}

	public static String to2PlaceDecimalText(BigDecimal no) {
		return isZero(no) ? "" : TWO_PLACE_DECIMAL.format(no);
	}

	public static String toCurrencyText(BigDecimal no) {
		return isZero(no) ? "" : "₱" + to2PlaceDecimalText(no);
	}

	public static double toDouble(String text) {
		return toBigDecimal(text).doubleValue();
	}

	public static Fraction toFraction(String text) {
		try {
			return Fraction.getFraction(text);
		} catch (Exception e) {
			return Fraction.ZERO;
		}
	}

	public static String toIdText(BigDecimal no) {
		return isZero(no) ? "" : NO_COMMA_INTEGER.format(no);
	}

	public static String toIdText(Long no) {
		return isZero(no) ? "" : NO_COMMA_INTEGER.format(no);
	}

	public static boolean isZero(Long no) {
		return no == null || no == 0;
	}

	public static Integer toInteger(String text) {
		return toBigDecimal(text).intValue();
	}

	public static String toIntegerWithComma(Integer no) {
		return isZero(no) ? "" : INTEGER.format(no);
	}

	public static boolean isZero(Integer no) {
		return no == null || no == 0;
	}

	public static Long toLong(String text) {
		return toBigDecimal(text).longValue();
	}

	public static BigDecimal toPercentRate(BigDecimal percent) {
		return percent == null ? ZERO : divide(percent, HUNDRED);
	}

	public static String toPercentText(BigDecimal no) {
		return isZero(no) ? "" : to2PlaceDecimalText(no) + "%";
	}

	public static String toQuantityText(BigDecimal no) {
		return isZero(no) ? "" : INTEGER.format(no);
	}

	public static BigDecimal toWholeNo(BigDecimal no) {
		return no == null ? ZERO : toDecimal(no.intValue());
	}

	public static BigDecimal zeroIfNull(BigDecimal no) {
		return no == null ? ZERO : no;
	}
}
