package ph.txtdis.util;

import static java.util.Collections.emptyList;

import java.util.List;

public class ListUtils {

	public static <T> List<T> emptyIfNull(List<T> l) {
		return l == null ? emptyList() : l;
	}
}
