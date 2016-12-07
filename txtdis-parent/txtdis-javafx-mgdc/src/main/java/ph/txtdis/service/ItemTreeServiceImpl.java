package ph.txtdis.service;

import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.ItemFamily;
import ph.txtdis.dto.ItemTree;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.exception.NotAllowedOffSiteTransactionException;
import ph.txtdis.info.SuccessfulSaveInfo;
import ph.txtdis.util.ClientTypeMap;

@Service("itemTreeService")
public class ItemTreeServiceImpl implements ItemTreeService {

	@Autowired
	private CredentialService credentialService;

	@Autowired
	private ItemFamilyService familyService;

	@Autowired
	private ReadOnlyService<ItemTree> readOnlyService;

	@Autowired
	private RestServerService serverService;

	@Autowired
	private SavingService<ItemTree> savingService;

	@Autowired
	public ClientTypeMap typeMap;

	@Value("${prefix.module}")
	private String modulePrefix;

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public boolean isDuplicated(ItemFamily family, ItemFamily parent) throws Exception {
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
	public ReadOnlyService<ItemTree> getListedReadOnlyService() {
		return readOnlyService;
	}

	@Override
	public SavingService<ItemTree> getSavingService() {
		return savingService;
	}

	@Override
	public String getHeaderText() {
		return "Item Tree";
	}

	@Override
	public String getTitleText() {
		return credentialService.username() + "@" + modulePrefix + " Item Trees";
	}

	@Override
	public List<ItemFamily> listFamilies() throws Exception {
		return familyService.list();
	}

	@Override
	public List<ItemFamily> listParents(ItemFamily family) throws Exception {
		return family == null ? Collections.emptyList()
				: familyService.list().stream().filter(f -> f.getTier().ordinal() > family.getTier().ordinal())
						.collect(toList());
	}

	@Override
	public ItemTree save(ItemFamily family, ItemFamily parent) throws SuccessfulSaveInfo, Exception {
		if (isOffSite())
			throw new NotAllowedOffSiteTransactionException();
		ItemTree it = new ItemTree();
		it.setFamily(family);
		it.setParent(parent);
		return save(it);
	}

	private boolean isOffSite() {
		return serverService.isOffSite();
	}
}
