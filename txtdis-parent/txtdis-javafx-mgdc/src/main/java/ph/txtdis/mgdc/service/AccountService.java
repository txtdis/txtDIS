package ph.txtdis.mgdc.service;

import java.util.List;

import ph.txtdis.dto.Account;
import ph.txtdis.service.ListedAndResetableService;
import ph.txtdis.service.ResettableService;
import ph.txtdis.service.TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService;

public interface AccountService extends ListedAndResetableService<Account>, ResettableService, TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService {

	void find(String[] ids) throws Exception;

	List<Account> getSellerHistory();
}