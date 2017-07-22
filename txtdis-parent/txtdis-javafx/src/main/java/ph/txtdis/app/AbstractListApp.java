package ph.txtdis.app;

import ph.txtdis.dto.Keyed;
import ph.txtdis.fx.table.AppTable;
import ph.txtdis.service.HeaderNameService;
import ph.txtdis.service.SavableAsExcelService;

public abstract class AbstractListApp<AT extends AppTable<T>, AS extends SavableAsExcelService<T>, T extends Keyed<Long>> //
		extends AbstractExcelApp<AT, AS, T> //
		implements KeySelectableListApp {

	@Override
	public Long getSelectedKey() {
		T ph = table.getItem();
		return ph == null ? null : ph.getId();
	}

	@Override
	public void start() {
		setStage(mainVerticalPane());
		refresh();
		showAndWait();
	}

	@Override
	protected String getHeaderText() {
		return ((HeaderNameService) service).getHeaderName() + " List";
	}

	@Override
	protected String getTitleText() {
		return getHeaderText();
	}
}
