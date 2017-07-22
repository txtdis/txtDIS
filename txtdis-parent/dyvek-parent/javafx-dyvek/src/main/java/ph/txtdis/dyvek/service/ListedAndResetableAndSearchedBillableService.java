package ph.txtdis.dyvek.service;

import java.util.List;

import ph.txtdis.dyvek.model.Billable;
import ph.txtdis.service.ListedAndResetableService;

public interface ListedAndResetableAndSearchedBillableService //
		extends ListedAndResetableService<Billable> {

	List<Billable> listSearched();
}
