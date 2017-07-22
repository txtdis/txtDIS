package ph.txtdis.mgdc.gsm.service;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.ItemFamily;
import ph.txtdis.dto.Warehouse;
import ph.txtdis.info.Information;
import ph.txtdis.service.CredentialService;
import ph.txtdis.service.ReadOnlyService;
import ph.txtdis.service.SavingService;
import ph.txtdis.util.ClientTypeMap;

@Service("warehouseService")
public class WarehouseServiceImpl //
		implements WarehouseService {

	@Autowired
	private CredentialService credentialService;

	@Autowired
	private ItemFamilyService familyService;

	@Autowired
	private ReadOnlyService<Warehouse> readOnlyService;

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
	public ReadOnlyService<Warehouse> getListedReadOnlyService() {
		return readOnlyService;
	}

	@Override
	public String getModuleName() {
		return "warehouse";
	}

	@Override
	public String getTitleName() {
		return credentialService.username() + "@" + modulePrefix + " " + WarehouseService.super.getTitleName();
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
	public void reset() {
	}

	@Override
	public Warehouse save(String name, ItemFamily family) throws Information, Exception {
		Warehouse w = new Warehouse();
		w.setName(name);
		return savingService.module(getModuleName()).save(w);
	}
}
