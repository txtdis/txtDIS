package ph.txtdis.service;

import java.util.List;

import ph.txtdis.dto.Account;

public interface AccountService extends Listed<Account>, Titled {

	void find(String[] ids) throws Exception;

	List<Account> getSellerHistory();
}