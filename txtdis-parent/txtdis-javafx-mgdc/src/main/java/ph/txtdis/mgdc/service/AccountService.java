package ph.txtdis.mgdc.service;

import ph.txtdis.dto.Account;
import ph.txtdis.service.ListedAndResettableService;
import ph.txtdis.service.ResettableService;
import ph.txtdis.service.TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService;

import java.util.List;

public interface AccountService
	extends ListedAndResettableService<Account>,
	ResettableService,
	TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService {

	void find(String[] ids) throws Exception;

	List<Account> getSellerHistory();
}