package ph.txtdis.dyvek.app;

import ph.txtdis.app.AbstractTableApp;
import ph.txtdis.app.KeySelectableListApp;
import ph.txtdis.dto.Keyed;
import ph.txtdis.fx.table.AppTable;
import ph.txtdis.service.ListedAndResetableService;
import ph.txtdis.service.ModuleAlternateNameAndNoPromptService;

public abstract class AbstractListApp< //
		OT extends AppTable<T>, //
		OS extends ListedAndResetableService<T>, //
		T extends Keyed<Long>> //
		extends AbstractTableApp<OT, OS, T> //
		implements KeySelectableListApp {

	protected static final String LIST = " List";

	@Override
	public Long getSelectedKey() {
		T t = table.getItem();
		return t == null ? null : t.getId();
	}

	@Override
	protected String getTitleText() {
		return ((ModuleAlternateNameAndNoPromptService) service).getAlternateName() + LIST;
	}

	@Override
	public void start() {
		setStage(mainVerticalPane());
		refresh();
		showAndWait();
	}
}
