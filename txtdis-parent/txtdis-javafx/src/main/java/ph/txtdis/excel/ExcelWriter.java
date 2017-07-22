package ph.txtdis.excel;

import java.io.IOException;
import java.util.List;

import ph.txtdis.fx.table.AppTable;

public interface ExcelWriter {

	ExcelWriter filename(String filename);

	ExcelWriter sheetname(String... sheetnames);

	ExcelWriter table(AppTable<?>... tables);

	@SuppressWarnings("unchecked")
	ExcelWriter table(List<AppTable<?>>... tables);

	void write() throws IOException;
}