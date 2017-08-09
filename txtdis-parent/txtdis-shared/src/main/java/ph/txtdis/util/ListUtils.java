package ph.txtdis.util;

import java.util.List;

import static java.util.Collections.emptyList;

public class ListUtils {

	public static <T> List<T> emptyIfNull(List<T> l) {
		return l == null ? emptyList() : l;
	}
}
