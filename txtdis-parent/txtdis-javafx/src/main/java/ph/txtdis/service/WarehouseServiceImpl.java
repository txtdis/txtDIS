package ph.txtdis.service;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.ItemFamily;
import ph.txtdis.dto.Warehouse;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.NotAllowedOffSiteTransactionException;
import ph.txtdis.exception.StoppedServerException;
import ph.txtdis.info.SuccessfulSaveInfo;
import ph.txtdis.util.ClientTypeMap;

@Service("warehouseService")
public class WarehouseServiceImpl implements WarehouseService {

	@Autowired
	private CredentialService credentialService;

	@Autowired
	private ItemFamilyService familyService;

	@Autowired
	private ReadOnlyService<Warehouse> readOnlyService;

	@Autowired
	private RestServerService serverService;

	@Autowired
	private SavingService<Warehouse> savingService;

	@Autowired
	private ClientTypeMap typeMap;

	@Value("${prefix.module}")
	private String modulePrefix;

	@Override
	public ItemFamilyService getItemFamilyService() {
		return familyService;
	}

	@Override
	public String getModule() {
		return "warehouse";
	}

	@Override
	public ReadOnlyService<Warehouse> getListedReadOnlyService() {
		return readOnlyService;
	}

	@Override
	public String getTitleText() {
		return credentialService.username() + "@" + modulePrefix + " " + WarehouseService.super.getTitleText();
	}

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public List<String> listNames() {
		try {
			return list().stream().map(w -> w.getName()).collect(toList());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Warehouse save(String name, ItemFamily family)
			throws SuccessfulSaveInfo, NoServerConnectionException, StoppedServerException, FailedAuthenticationException,
			InvalidException, NotAllowedOffSiteTransactionException {
		if (isOffSite())
			throw new NotAllowedOffSiteTransactionException();
		Warehouse w = new Warehouse();
		w.setName(name);
		w.setFamily(family);
		return savingService.module(getModule()).save(w);
	}

	@Override
	public boolean isOffSite() {
		return serverService.isOffSite();
	}
}
