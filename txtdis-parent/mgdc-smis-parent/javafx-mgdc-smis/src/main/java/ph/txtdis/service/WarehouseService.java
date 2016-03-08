package ph.txtdis.service;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.ItemFamily;
import ph.txtdis.dto.Warehouse;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.NotAllowedOffSiteTransactionException;
import ph.txtdis.exception.StoppedServerException;
import ph.txtdis.info.SuccessfulSaveInfo;
import ph.txtdis.util.TypeMap;

@Service("warehouseService")
public class WarehouseService implements Iconed, ItemFamilyLimited, Listed<Warehouse>, UniquelyNamed<Warehouse> {

	@Autowired
	private ItemFamilyService familyService;

	@Autowired
	private ReadOnlyService<Warehouse> readOnlyService;

	@Autowired
	private SavingService<Warehouse> savingService;

	@Autowired
	private ServerService server;

	@Autowired
	public TypeMap typeMap;

	@Override
	public TypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public ItemFamilyService getItemFamilyService() {
		return familyService;
	}

	@Override
	public String getModule() {
		return "warehouse";
	}

	@Override
	public ReadOnlyService<Warehouse> getReadOnlyService() {
		return readOnlyService;
	}

	public List<String> listNames() {
		try {
			return list().stream().map(w -> w.getName()).collect(toList());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

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

	public boolean isOffSite() {
		return server.isOffSite();
	}
}
