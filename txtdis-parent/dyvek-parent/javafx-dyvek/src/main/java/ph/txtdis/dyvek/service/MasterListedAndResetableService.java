package ph.txtdis.dyvek.service;

import static java.util.stream.Collectors.toList;

import java.util.List;

import ph.txtdis.dto.Named;
import ph.txtdis.service.ListedAndResetableService;

public interface MasterListedAndResetableService<T extends Named> //
		extends ListedAndResetableService<T> {

	default List<T> list(String endPt) {
		try {
			return getListedReadOnlyService().module(getModuleName()).getList("/" + endPt);
		} catch (Exception e) {
			return null;
		}
	}

	default List<String> listNames(String endPt) {
		try {
			return list(endPt).stream().map(e -> e.getName()).collect(toList());
		} catch (Exception e) {
			return null;
		}
	}
}
