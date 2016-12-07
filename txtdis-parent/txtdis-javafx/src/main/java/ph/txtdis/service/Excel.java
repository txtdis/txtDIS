package ph.txtdis.service;

import java.io.IOException;

import ph.txtdis.fx.table.AppTable;

public interface Excel<T> extends Listed<T> {

	@SuppressWarnings("unchecked")
	void saveAsExcel(AppTable<T>... tables) throws IOException;
}
