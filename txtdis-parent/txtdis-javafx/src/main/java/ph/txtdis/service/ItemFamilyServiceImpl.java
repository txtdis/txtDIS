package ph.txtdis.service;

import static ph.txtdis.type.ItemTier.PRODUCT;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Item;
import ph.txtdis.dto.ItemFamily;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.NotAllowedOffSiteTransactionException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;
import ph.txtdis.info.SuccessfulSaveInfo;
import ph.txtdis.type.ItemTier;
import ph.txtdis.util.ClientTypeMap;

@Service("itemFamilyService")
public class ItemFamilyServiceImpl implements ItemFamilyService {

	@Autowired
	private CredentialService credentialService;

	@Autowired
	private ReadOnlyService<ItemFamily> readOnlyService;

	@Autowired
	private SavingService<ItemFamily> savingService;

	@Autowired
	private RestServerService serverService;

	@Autowired
	private ClientTypeMap typeMap;

	@Value("${prefix.module}")
	private String modulePrefix;

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public List<ItemFamily> getItemAncestry(Item item) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		return readOnlyService.module(getModule()).getList("/ancestry?family=" + item.getFamily());
	}

	@Override
	public String getHeaderText() {
		return "Item Family";
	}

	@Override
	public String getModule() {
		return "itemFamily";
	}

	@Override
	public String getTitleText() {
		return credentialService.username() + "@" + modulePrefix + " Item Families";
	}

	@Override
	public ReadOnlyService<ItemFamily> getListedReadOnlyService() {
		return readOnlyService;
	}

	@Override
	public List<ItemFamily> listItemFamily(ItemTier t) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		return readOnlyService.module(getModule()).getList("/perTier?tier=" + t);
	}

	@Override
	public List<ItemFamily> listItemParents() throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		return readOnlyService.module(getModule()).getList("/parents");
	}

	@Override
	public ItemFamily save(String name) throws SuccessfulSaveInfo, NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, InvalidException, NotAllowedOffSiteTransactionException {
		return save(name, PRODUCT);
	}

	@Override
	public ItemFamily save(String name, ItemTier tier)
			throws SuccessfulSaveInfo, NoServerConnectionException, StoppedServerException, FailedAuthenticationException,
			InvalidException, NotAllowedOffSiteTransactionException {
		if (isOffSite())
			throw new NotAllowedOffSiteTransactionException();
		ItemFamily entity = new ItemFamily();
		entity.setName(name);
		entity.setTier(tier);
		return savingService.module(getModule()).save(entity);
	}

	@Override
	public boolean isOffSite() {
		return serverService.isOffSite();
	}
}
