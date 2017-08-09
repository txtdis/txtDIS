package ph.txtdis.mgdc.gsm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ph.txtdis.dto.ItemFamily;
import ph.txtdis.info.Information;
import ph.txtdis.mgdc.gsm.dto.Item;
import ph.txtdis.service.RestClientService;
import ph.txtdis.type.ItemTier;
import ph.txtdis.util.ClientTypeMap;

import java.util.List;

import static ph.txtdis.type.ItemTier.PRODUCT;
import static ph.txtdis.util.UserUtils.username;

@Service("itemFamilyService")
public class ItemFamilyServiceImpl //
	implements ItemFamilyService {

	@Autowired
	private RestClientService<ItemFamily> restClientService;

	@Autowired
	private ClientTypeMap typeMap;

	@Value("${prefix.module}")
	private String modulePrefix;

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public List<ItemFamily> getItemAncestry(Item item) throws Exception {
		return restClientService.module(getModuleName()).getList("/ancestry?family=" + item.getFamily());
	}

	@Override
	public String getModuleName() {
		return "itemFamily";
	}

	@Override
	public String getHeaderName() {
		return "Item Family";
	}

	@Override
	public String getTitleName() {
		return username() + "@" + modulePrefix + " Item Families";
	}

	@Override
	public RestClientService<ItemFamily> getRestClientService() {
		return restClientService;
	}

	@Override
	public RestClientService<ItemFamily> getRestClientServiceForLists() {
		return restClientService;
	}

	@Override
	public List<ItemFamily> listItemFamily(ItemTier t) throws Exception {
		return restClientService.module(getModuleName()).getList("/perTier?tier=" + t);
	}

	@Override
	public List<ItemFamily> listItemParents() throws Exception {
		return restClientService.module(getModuleName()).getList("/parents");
	}

	@Override
	public void reset() {
	}

	@Override
	public ItemFamily save(String name) throws Information, Exception {
		return save(name, PRODUCT);
	}

	@Override
	public ItemFamily save(String name, ItemTier tier) throws Information, Exception {
		ItemFamily entity = new ItemFamily();
		entity.setName(name);
		entity.setTier(tier);
		return restClientService.module(getModuleName()).save(entity);
	}
}
