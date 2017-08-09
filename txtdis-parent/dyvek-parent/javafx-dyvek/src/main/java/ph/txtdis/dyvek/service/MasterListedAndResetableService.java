package ph.txtdis.dyvek.service;

import ph.txtdis.dto.Named;
import ph.txtdis.service.ListedAndResettableService;

import java.util.List;

import static java.util.stream.Collectors.toList;

public interface MasterListedAndResetableService<T extends Named> //
	extends ListedAndResettableService<T> {

	default List<String> listNames(String endPt) {
		try {
			return list(endPt).stream().map(e -> e.getName()).collect(toList());
		} catch (Exception e) {
			return null;
		}
	}

	default List<T> list(String endPt) {
		try {
			return getRestClientServiceForLists().module(getModuleName()).getList("/" + endPt);
		} catch (Exception e) {
			return null;
		}
	}
}
