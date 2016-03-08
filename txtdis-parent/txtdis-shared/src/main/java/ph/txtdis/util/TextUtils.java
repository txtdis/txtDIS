package ph.txtdis.util;

import static org.apache.commons.lang3.text.WordUtils.capitalizeFully;

import java.time.ZonedDateTime;

import static org.apache.commons.lang3.StringUtils.startsWith;

public class TextUtils {

	private static final String NULL = "null";

	public static String blankIfNull(String s) {
		return s == null ? "" : s;
	}

	public static String capitalize(String uncapped) {
		uncapped = capitalizeFully(uncapped, '_');
		return uncapped.replace("_", " ");
	}

	public static boolean isEmpty(String s) {
		return s == null || s.isEmpty() ? true : false;
	}

	public static String nullIfEmpty(String s) {
		return s == null || s.isEmpty() ? null : s;
	}

	public static Boolean toBoolean(String s) {
		return s.equals(NULL) ? null : Boolean.valueOf(s);
	}

	public static String toBoolSign(Boolean b) {
		return b == null ? "" : b ? "√" : "x";
	}

	public static String toIdDisplay(String c) {
		return c == null ? "" : startsWith(c, "-") ? c.replace("-", "(") + ")" : c;
	}

	public static String toString(Object o) {
		return o == null ? "" : o.toString();
	}

	public static String toText(String s) {
		return s.equals(NULL) ? null : s;
	}

	public static ZonedDateTime toZonedDateTime(String s) {
		return s.equals(NULL) ? null : ZonedDateTime.parse(s);
	}
}
