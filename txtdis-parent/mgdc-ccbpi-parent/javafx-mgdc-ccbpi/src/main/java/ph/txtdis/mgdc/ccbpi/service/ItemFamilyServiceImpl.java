package ph.txtdis.mgdc.ccbpi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.ItemFamily;
import ph.txtdis.service.ReadOnlyService;

@Service("itemFamilyService")
public class ItemFamilyServiceImpl //
		implements ItemFamilyService {

	@Autowired
	private ReadOnlyService<ItemFamily> readOnlyService;

	@Override
	public String getModuleName() {
		return "itemFamily";
	}

	@Override
	public ReadOnlyService<ItemFamily> getListedReadOnlyService() {
		return readOnlyService;
	}
}
