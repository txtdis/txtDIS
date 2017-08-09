package ph.txtdis.dyvek.service;

import ph.txtdis.dyvek.model.Billable;
import ph.txtdis.service.ListedAndResettableService;

import java.util.List;

public interface ListedAndResetableAndSearchedBillableService //
	extends ListedAndResettableService<Billable> {

	List<Billable> listSearched();
}
