package ph.txtdis.service;

import ph.txtdis.fx.table.AppTable;

public interface SavableAsExcelService<T> //
	extends ListedAndResettableService<T> {

	@SuppressWarnings("unchecked")
	void saveAsExcel(AppTable<T>... tables) throws Exception;
}
