package ph.txtdis.dyvek.app;

import ph.txtdis.dyvek.model.Aging;
import ph.txtdis.fx.table.AppTable;
import ph.txtdis.service.ListedAndResettableService;

public abstract class AbstractAgingListApp< //
	OT extends AppTable<Aging>, //
	OS extends ListedAndResettableService<Aging>> //
	extends AbstractListApp<OT, OS, Aging> {

	private static final String AGING = "Aging ";

	@Override
	protected String getHeaderText() {
		return AGING + super.getHeaderText();
	}

	@Override
	protected String getTitleText() {
		return AGING + super.getTitleText();
	}
}
