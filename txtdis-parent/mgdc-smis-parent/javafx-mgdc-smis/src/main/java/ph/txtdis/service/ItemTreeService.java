package ph.txtdis.service;

import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.ItemFamily;
import ph.txtdis.dto.ItemTree;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.exception.NotAllowedOffSiteTransactionException;
import ph.txtdis.info.SuccessfulSaveInfo;
import ph.txtdis.util.TypeMap;

@Service
public class ItemTreeService implements Iconed, Listed<ItemTree>, SavedByEntity<ItemTree> {

	@Autowired
	private ItemFamilyService familyService;

	@Autowired
	private ReadOnlyService<ItemTree> readOnlyService;

	@Autowired
	private SavingService<ItemTree> savingService;

	@Autowired
	private ServerService server;

	@Autowired
	public TypeMap typeMap;

	@Override
	public TypeMap getTypeMap() {
		return typeMap;
	}

	public boolean duplicated(ItemFamily family, ItemFamily parent) throws Exception {
		if (readOnlyService.module(getModule())
				.getOne("/find?family=" + family.getId() + "&parent=" + parent.getId()) != null)
			throw new DuplicateException(family + " - " + parent);
		return false;
	}

	@Override
	public String getModule() {
		return "itemTree";
	}

	@Override
	public ReadOnlyService<ItemTree> getReadOnlyService() {
		return readOnlyService;
	}

	@Override
	public SavingService<ItemTree> getSavingService() {
		return savingService;
	}

	public List<ItemFamily> listFamilies() throws Exception {
		return familyService.list();
	}

	public List<ItemFamily> listParents(ItemFamily family) throws Exception {
		return family == null ? Collections.emptyList()
				: familyService.list().stream().filter(f -> f.getTier().ordinal() > family.getTier().ordinal())
						.collect(toList());
	}

	public ItemTree save(ItemFamily family, ItemFamily parent) throws SuccessfulSaveInfo, Exception {
		if (isOffSite())
			throw new NotAllowedOffSiteTransactionException();
		ItemTree it = new ItemTree();
		it.setFamily(family);
		it.setParent(parent);
		return save(it);
	}

	public boolean isOffSite() {
		return server.isOffSite();
	}
}
