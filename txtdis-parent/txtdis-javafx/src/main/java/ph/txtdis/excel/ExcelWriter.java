package ph.txtdis.excel;

import ph.txtdis.fx.table.AppTable;

import java.io.IOException;
import java.util.List;

public interface ExcelWriter {

	ExcelWriter filename(String filename);

	ExcelWriter sheetname(String... sheetnames);

	ExcelWriter table(AppTable<?>... tables);

	@SuppressWarnings("unchecked")
	ExcelWriter table(List<AppTable<?>>... tables);

	void write() throws IOException;
}