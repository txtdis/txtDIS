package ph.txtdis.util;

import static org.apache.commons.lang3.StringUtils.splitByCharacterType;
import static org.apache.commons.lang3.StringUtils.startsWith;
import static org.apache.commons.lang3.text.WordUtils.capitalizeFully;

import org.apache.commons.lang3.StringUtils;

public class TextUtils {

	public static final String HAS_OVERDUES = "overdues";

	public static final String NULL = "null";

	public static String blankIfNull(Object o) {
		return o == null ? "" : o.toString();
	}

	public static String blankIfNullElseAddCarriageReturn(String s) {
		if (s != null && !s.trim().isEmpty())
			return s.trim() + "\n";
		return blankIfNull(s);
	}

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
		return b == null || !b ? "" : "√";
	}

	public static String toIdDisplay(String c) {
		return c == null ? "" : startsWith(c, "-") ? c.replace("-", "(") + ")" : c;
	}

	public static String toText(Object o) {
		return o == null ? "" : o.toString();
	}

	public static String toText(String s) {
		return s.equals(NULL) ? null : s;
	}

	public static String[] to3PartIdNo(String orderNo) {
		String[] prefixAndRest = StringUtils.split(orderNo, "-");
		if (prefixAndRest.length == 1)
			return to3PartIdNo(null, splitNumIdAndSuffix(prefixAndRest[0]));
		return to3PartIdNo(prefixAndRest[0], splitNumIdAndSuffix(prefixAndRest[1]));
	}

	private static String[] to3PartIdNo(String prefix, String[] numIdAndSuffix) {
		return new String[]{prefix, numIdAndSuffix[0], numIdAndSuffix[1]};
	}

	private static String[] splitNumIdAndSuffix(String orderNoLessPrefix) {
		String[] numIdAndSuffix = splitByCharacterType(orderNoLessPrefix.replace("(", "").replace(")", ""));
		return numIdAndSuffix.length > 2 ? null : toNumIdAndSuffix(orderNoLessPrefix, numIdAndSuffix);
	}

	private static String[] toNumIdAndSuffix(String orderNoLessPrefix, String[] numIdAndSuffix) {
		if (StringUtils.contains(orderNoLessPrefix, "("))
			numIdAndSuffix[0] = "-" + numIdAndSuffix[0];
		return toNumIdAndSuffix(numIdAndSuffix);
	}

	private static String[] toNumIdAndSuffix(String[] orderNoLessPrefix) {
		String[] numIdAndSuffix = new String[2];
		numIdAndSuffix[0] = orderNoLessPrefix[0];
		numIdAndSuffix[1] = orderNoLessPrefix.length == 1 ? null : orderNoLessPrefix[1];
		return numIdAndSuffix;
	}
}
