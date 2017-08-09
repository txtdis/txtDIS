package ph.txtdis.util;

import org.apache.commons.lang3.math.Fraction;
import ph.txtdis.fx.control.StylableTextField;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;

import static ph.txtdis.util.DateTimeUtils.*;
import static ph.txtdis.util.NumberUtils.*;
import static ph.txtdis.util.TextUtils.toBoolSign;
import static ph.txtdis.util.TextUtils.toIdDisplay;

public class Styled {

	private static final String ALIGN = " -fx-alignment:";

	private static final String COLOR = " -fx-t-fill:";

	private static final String UNDIMMED = " -fx-opacity: 1; ";

	protected static final String RIGHT_ALIGN = UNDIMMED + ALIGN + " center-right; ";

	private static final String CENTER = UNDIMMED + ALIGN + " center; ";

	private static final String GREEN = COLOR + " green; ";

	private static final String RED = COLOR + " red; ";

	public static void for2Place(StylableTextField f, BigDecimal d) {
		f.setText(to2PlaceDecimalText(d));
		setNumberStyle(f, d);
	}

	private static void setNumberStyle(StylableTextField f, BigDecimal d) {
		f.setStyle(RIGHT_ALIGN + (isNegative(d) ? RED : ""));
	}

	public static void for4Place(StylableTextField f, BigDecimal d) {
		f.setText(to4PlaceDecimalText(d));
		setNumberStyle(f, d);
	}

	public static void for4PlacePercent(StylableTextField f, BigDecimal d) {
		f.setText(to4PlacePercentText(d));
		setNumberStyle(f, d);
	}

	public static void forBoolean(StylableTextField f, Boolean b) {
		f.setText(toBoolSign(b));
		f.setStyle(CENTER + (b == null ? "" : b ? GREEN : RED));
	}

	public static void forCode(StylableTextField f, String c) {
		f.setText(toIdDisplay(c));
		f.setStyle(RIGHT_ALIGN);
	}

	public static void forCurrency(StylableTextField f, BigDecimal d) {
		f.setText(toCurrencyText(d));
		setNumberStyle(f, d);
	}

	public static void forDate(StylableTextField f, LocalDate d) {
		f.setText(toDateDisplay(d));
		center(f);
	}

	private static void center(StylableTextField f) {
		f.setStyle(CENTER);
	}

	public static <T> void forEnum(StylableTextField f, T t) {
		f.setText(t.toString());
		center(f);
	}

	public static void forFraction(StylableTextField t, Fraction f) {
		t.setText(formatFraction(f));
		t.setStyle(RIGHT_ALIGN);
	}

	private static String formatFraction(Fraction f) {
		return f.compareTo(Fraction.ZERO) == 0 ? "" : f.toProperString();
	}

	public static void forIdNo(StylableTextField f, Long l) {
		f.setText(toIdText(l));
		setNumberStyle(f, l);
	}

	private static void setNumberStyle(StylableTextField f, Long l) {
		f.setStyle(RIGHT_ALIGN + (isNegative(l) ? RED : ""));
	}

	public static void forInteger(StylableTextField f, Integer i) {
		f.setText(toIntegerWithComma(i));
		setNumberStyle(f, i);
	}

	private static void setNumberStyle(StylableTextField f, Integer i) {
		f.setStyle(RIGHT_ALIGN + (isNegative(i) ? RED : ""));
	}

	public static void forPercent(StylableTextField f, BigDecimal d) {
		f.setText(toPercentText(d));
		setNumberStyle(f, d);
	}

	public static void forPhone(StylableTextField f, String t) {
		f.setText(PhoneUtils.displayPhone(t));
		f.setStyle(RIGHT_ALIGN);
	}

	public static void forQuantity(StylableTextField f, BigDecimal d) {
		f.setText(toAsNeededNoOfPlaceDecimalText(d));
		setNumberStyle(f, d);
	}

	public static <T> void forText(StylableTextField f, T t) {
		f.setText(t == null ? "" : t.toString());
		f.setStyle(UNDIMMED);
	}

	public static void forTime(StylableTextField f, LocalTime t) {
		f.setText(toTimeDisplay(t));
		f.setStyle(UNDIMMED);
	}

	public static void forTimestamp(StylableTextField f, ZonedDateTime t) {
		f.setText(toTimestampText(t));
		center(f);
	}
}
