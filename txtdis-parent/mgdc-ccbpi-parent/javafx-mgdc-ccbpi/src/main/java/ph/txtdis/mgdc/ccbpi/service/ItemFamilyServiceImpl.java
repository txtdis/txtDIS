package ph.txtdis.mgdc.ccbpi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.ItemFamily;
import ph.txtdis.service.RestClientService;

@Service("itemFamilyService")
public class ItemFamilyServiceImpl //
	implements ItemFamilyService {

	@Autowired
	private RestClientService<ItemFamily> restClientService;

	@Override
	public String getModuleName() {
		return "itemFamily";
	}

	@Override
	public RestClientService<ItemFamily> getRestClientService() {
		return restClientService;
	}
}
