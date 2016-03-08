package ph.txtdis.service;

import static ph.txtdis.type.ItemTier.PRODUCT;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import ph.txtdis.util.TypeMap;

@Service("itemFamilyService")
public class ItemFamilyService implements Listed<ItemFamily>, SavedByName<ItemFamily>, UniquelyNamed<ItemFamily> {

	@Autowired
	private ReadOnlyService<ItemFamily> readOnlyService;

	@Autowired
	private SavingService<ItemFamily> savingService;

	@Autowired
	private ServerService server;

	@Autowired
	public TypeMap typeMap;

	@Override
	public TypeMap getTypeMap() {
		return typeMap;
	}

	public List<ItemFamily> getItemAncestry(Item item) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		return readOnlyService.module(getModule()).getList("/ancestry?family=" + item.getFamily().getId());
	}

	@Override
	public String getModule() {
		return "itemFamily";
	}

	@Override
	public ReadOnlyService<ItemFamily> getReadOnlyService() {
		return readOnlyService;
	}

	public List<ItemFamily> listItemFamily(ItemTier t) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		return readOnlyService.module(getModule()).getList("/perTier?tier=" + t);
	}

	public List<ItemFamily> listItemParents() throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		return readOnlyService.module(getModule()).getList("/parents");
	}

	@Override
	public ItemFamily save(String name) throws SuccessfulSaveInfo, NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, InvalidException, NotAllowedOffSiteTransactionException {
		return save(name, PRODUCT);
	}

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

	public boolean isOffSite() {
		return server.isOffSite();
	}
}
