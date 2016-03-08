package ph.txtdis.util;

import static org.apache.commons.lang3.StringUtils.removeEnd;
import static org.apache.commons.lang3.StringUtils.uncapitalize;

import ph.txtdis.app.Launchable;
import ph.txtdis.app.Startable;

public class NameUtils {

	public static String toName(Launchable a) {
		return uncapitalize(removeAppSuffix(a));
	}

	public static String toName(Startable a) {
		return uncapitalize(removeAppSuffix(a));
	}

	public static String toString(Object o) {
		return o == null ? "" : o.toString();
	}

	private static String getClassname(Object o) {
		return o.getClass().getSimpleName();
	}

	private static String removeAppSuffix(Launchable a) {
		return removeEnd(getClassname(a), "App");
	}

	private static String removeAppSuffix(Startable a) {
		return removeEnd(getClassname(a), "App");
	}
}
