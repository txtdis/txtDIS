package ph.txtdis.util;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_EVEN;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class NumberUtils {

	public final static DecimalFormat NO_COMMA_DECIMAL = new DecimalFormat("0.00;(0.00)");
	public final static DecimalFormat TWO_PLACE_DECIMAL = new DecimalFormat("#,##0.00;(#,##0.00)");
	public final static DecimalFormat FOUR_PLACE_DECIMAL = new DecimalFormat("#,##0.0000;(#,##0.0000)");
	public final static DecimalFormat INTEGER = new DecimalFormat("#,##0;(#,##0)");
	public final static DecimalFormat NO_COMMA_INTEGER = new DecimalFormat("0;(0)");

	public final static BigDecimal HUNDRED = new BigDecimal("100");

	public static BigDecimal divide(BigDecimal dividend, BigDecimal divisor) {
		if (dividend == null || isZero(divisor))
			return ZERO;
		return dividend.divide(divisor, 4, HALF_EVEN);
	}

	public static String format2Place(BigDecimal number) {
		return isZero(number) ? "" : TWO_PLACE_DECIMAL.format(number);
	}

	public static String format4Place(BigDecimal number) {
		return isZero(number) ? "" : FOUR_PLACE_DECIMAL.format(number);
	}

	public static String formatCurrency(BigDecimal number) {
		return isZero(number) ? "" : "₱" + format2Place(number);
	}

	public static String formatDecimal(BigDecimal number) {
		return isZero(number) ? ""
				: isZero(number.remainder(BigDecimal.ONE)) ? INTEGER.format(number) : format2Place(number);
	}

	public static String formatId(Long number) {
		return isZero(number) ? "" : NO_COMMA_INTEGER.format(number);
	}

	public static String formatInt(Integer number) {
		return isZero(number) ? "" : INTEGER.format(number);
	}

	public static String formatPercent(BigDecimal number) {
		return isZero(number) ? "" : format2Place(number) + "%";
	}

	public static boolean isNegative(BigDecimal number) {
		return number == null ? false : number.compareTo(ZERO) < 0;
	}

	public static boolean isNegative(Integer number) {
		return number == null ? false : number < 0;
	}

	public static boolean isNegative(Long number) {
		return number == null ? false : number < 0;
	}

	public static boolean isNegative(String string) {
		return string.startsWith("-") || (string.startsWith("(") && string.endsWith(")"));
	}

	public static boolean isPositive(BigDecimal number) {
		return number == null ? false : number.compareTo(ZERO) > 0;
	}

	public static boolean isZero(BigDecimal number) {
		return number == null ? true : number.compareTo(ZERO) == 0;
	}

	public static boolean isZero(Integer number) {
		return number == null ? true : number == 0;
	}

	public static boolean isZero(Long number) {
		return number == null ? true : number == 0;
	}

	public static double parseDouble(String text) {
		return toBigDecimal(text).doubleValue();
	}

	public static BigDecimal toBigDecimal(String text) {
		if (text == null)
			return ZERO;
		text = text.trim().replace(",", "").replace("(", "-").replace(")", "").replace("₱", "");
		if (text.equals("-") || text.isEmpty())
			return ZERO;
		return new BigDecimal(text);
	}

	public static Integer toInteger(String text) {
		return toBigDecimal(text).intValue();
	}

	public static Long toLong(String text) {
		return toBigDecimal(text).longValue();
	}

	public static String formatQuantity(BigDecimal number) {
		return isZero(number) ? "" : INTEGER.format(number);
	}

	public static BigDecimal parseBigDecimal(String text) {
		if (text == null)
			return ZERO;
		text = text.trim()//
				.replace(",", "")//
				.replace("(", "-")//
				.replace(")", "")//
				.replace("₱", "");
		if (text.equals("-") || text.isEmpty())
			return ZERO;
		return new BigDecimal(text);
	}

	public static Integer parseInteger(String text) {
		return parseBigDecimal(text).intValue();
	}

	public static Long parseLong(String text) {
		return parseBigDecimal(text).longValue();
	}

	public static String printDecimal(BigDecimal number) {
		return NO_COMMA_DECIMAL.format(number);
	}

	public static String printInteger(BigDecimal number) {
		return NO_COMMA_INTEGER.format(number);
	}

	public static BigDecimal toPercentRate(BigDecimal percent) {
		return percent == null ? ZERO : divide(percent, HUNDRED);
	}
}
