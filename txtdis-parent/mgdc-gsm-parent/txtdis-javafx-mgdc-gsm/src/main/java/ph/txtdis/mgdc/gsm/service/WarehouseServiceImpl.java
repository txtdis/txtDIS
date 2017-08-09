package ph.txtdis.mgdc.gsm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ph.txtdis.dto.ItemFamily;
import ph.txtdis.dto.Warehouse;
import ph.txtdis.info.Information;
import ph.txtdis.service.RestClientService;
import ph.txtdis.util.ClientTypeMap;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static ph.txtdis.util.UserUtils.username;

@Service("warehouseService")
public class WarehouseServiceImpl //
	implements WarehouseService {

	@Autowired
	private ItemFamilyService familyService;

	@Autowired
	private RestClientService<Warehouse> restClientService;

	@Autowired
	private ClientTypeMap typeMap;

	@Value("${prefix.module}")
	private String modulePrefix;

	@Override
	public ItemFamilyService getItemFamilyService() {
		return familyService;
	}

	@Override
	public String getTitleName() {
		return username() + "@" + modulePrefix + " " + WarehouseService.super.getTitleName();
	}

	@Override
	public RestClientService<Warehouse> getRestClientService() {
		return restClientService;
	}

	@Override
	public RestClientService<Warehouse> getRestClientServiceForLists() {
		return restClientService;
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
		return restClientService.module(getModuleName()).save(w);
	}

	@Override
	public String getModuleName() {
		return "warehouse";
	}
}
