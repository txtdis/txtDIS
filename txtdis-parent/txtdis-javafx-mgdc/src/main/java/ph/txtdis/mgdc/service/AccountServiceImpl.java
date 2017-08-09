package ph.txtdis.mgdc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ph.txtdis.dto.Account;
import ph.txtdis.service.RestClientService;
import ph.txtdis.util.ClientTypeMap;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.capitalize;
import static ph.txtdis.util.UserUtils.username;

@Service("accountService")
public class AccountServiceImpl
	implements AccountService {

	@Autowired
	private RestClientService<Account> restClientService;

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
	public String getModuleName() {
		return "route";
	}

	@Override
	public RestClientService<Account> getRestClientServiceForLists() {
		return restClientService;
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
		return username() + "@" + modulePrefix + " " + capitalizedModule() + " Seller History";
	}

	@Override
	public void reset() {
	}
}
