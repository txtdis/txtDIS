package ph.txtdis.mgdc.gsm.service;

import static ph.txtdis.type.ItemTier.PRODUCT;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.ItemFamily;
import ph.txtdis.info.Information;
import ph.txtdis.mgdc.gsm.dto.Item;
import ph.txtdis.service.CredentialService;
import ph.txtdis.service.ReadOnlyService;
import ph.txtdis.service.SavingService;
import ph.txtdis.type.ItemTier;
import ph.txtdis.util.ClientTypeMap;

@Service("itemFamilyService")
public class ItemFamilyServiceImpl //
		implements ItemFamilyService {

	@Autowired
	private CredentialService credentialService;

	@Autowired
	private ReadOnlyService<ItemFamily> readOnlyService;

	@Autowired
	private SavingService<ItemFamily> savingService;

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
		return readOnlyService.module(getModuleName()).getList("/ancestry?family=" + item.getFamily());
	}

	@Override
	public String getHeaderName() {
		return "Item Family";
	}

	@Override
	public String getModuleName() {
		return "itemFamily";
	}

	@Override
	public String getTitleName() {
		return credentialService.username() + "@" + modulePrefix + " Item Families";
	}

	@Override
	public ReadOnlyService<ItemFamily> getListedReadOnlyService() {
		return readOnlyService;
	}

	@Override
	public List<ItemFamily> listItemFamily(ItemTier t) throws Exception {
		return readOnlyService.module(getModuleName()).getList("/perTier?tier=" + t);
	}

	@Override
	public List<ItemFamily> listItemParents() throws Exception {
		return readOnlyService.module(getModuleName()).getList("/parents");
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
		return savingService.module(getModuleName()).save(entity);
	}
}
