package ph.txtdis.app;

import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.table.AppTable;
import ph.txtdis.service.SavableAsExcelService;

import java.util.List;

import static java.util.Arrays.asList;

public abstract class AbstractExcelApp<AT extends AppTable<T>, AS extends SavableAsExcelService<T>, T> //
	extends AbstractTableApp<AT, AS, T> {

	@Autowired
	protected AppButton excelButton;

	@Override
	protected List<AppButton> addButtons() {
		createButtons();
		setOnButtonClick();
		return asList(excelButton);
	}

	protected void createButtons() {
		excelButton.icon("excel").tooltip("Save to a spreadsheet").build();
	}

	protected void setOnButtonClick() {
		excelButton.onAction(e -> saveAsExcel());
	}

	private void saveAsExcel() {
		try {
			saveAsExcel(table);
		} catch (Exception e) {
			showErrorDialog(e);
		}
	}

	@SuppressWarnings("unchecked")
	protected void saveAsExcel(AT table) throws Exception {
		service.saveAsExcel(table);
	}
}
