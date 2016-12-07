package ph.txtdis.app;

import static java.util.Arrays.asList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.table.AppTable;
import ph.txtdis.service.Excel;

public abstract class AbstractExcelApp<AT extends AppTable<T>, AS extends Excel<T>, T>
		extends AbstractTableApp<AT, AS, T> {

	@Autowired
	private AppButton excelButton;

	private void saveAsExcel() {
		try {
			saveAsExcel(table);
		} catch (Exception e) {
			showErrorDialog(e);
		}
	}

	@Override
	protected List<AppButton> addButtons() {
		createButtons();
		setOnButtonClick();
		return asList(excelButton);
	}

	protected void createButtons() {
		excelButton.icon("excel").tooltip("Save to a spreadsheet").build();
	}

	@SuppressWarnings("unchecked")
	protected void saveAsExcel(AT table) throws Exception {
		service.saveAsExcel(table);
	}

	protected void setOnButtonClick() {
		excelButton.setOnAction(e -> saveAsExcel());
	}
}
