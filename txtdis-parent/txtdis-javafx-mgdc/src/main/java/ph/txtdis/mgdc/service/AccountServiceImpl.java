package ph.txtdis.mgdc.service;

import static org.apache.commons.lang3.StringUtils.capitalize;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Account;
import ph.txtdis.service.CredentialService;
import ph.txtdis.service.ReadOnlyService;
import ph.txtdis.util.ClientTypeMap;

@Service("accountService")
public class AccountServiceImpl implements AccountService {

	@Autowired
	private CredentialService credentialService;

	@Autowired
	private ReadOnlyService<Account> readOnlyService;

	@Autowired
	private RouteService routeService;

	@Value("${prefix.module}")
	private String modulePrefix;

	@Override
	public void find(String[] ids) throws Exception {
		routeService.findByIds(ids);
	}

	@Override
	public String getHeaderName() {
		return capitalizedModule() + " Seller";
	}

	private String capitalizedModule() {
		return capitalize(getModuleName());
	}

	@Override
	public ReadOnlyService<Account> getListedReadOnlyService() {
		return readOnlyService;
	}

	@Override
	public String getModuleName() {
		return "route";
	}

	@Override
	public List<Account> getSellerHistory() {
		return routeService.getSellerHistory();
	}

	@Override
	public ClientTypeMap getTypeMap() {
		return routeService.getTypeMap();
	}

	@Override
	public String getTitleName() {
		return credentialService.username() + "@" + modulePrefix + " " + capitalizedModule() + " Seller History";
	}

	@Override
	public void reset() {
	}
}
