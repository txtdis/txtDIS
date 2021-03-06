package ph.txtdis.mgdc.gsm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ph.txtdis.dto.ItemFamily;
import ph.txtdis.dto.ItemTree;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.info.Information;
import ph.txtdis.service.RestClientService;
import ph.txtdis.util.ClientTypeMap;

import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static ph.txtdis.util.UserUtils.username;

@Service("itemTreeService")
public class ItemTreeServiceImpl //
	implements ItemTreeService {

	@Autowired
	public ClientTypeMap typeMap;

	@Autowired
	private ItemFamilyService familyService;

	@Autowired
	private RestClientService<ItemTree> restClientService;

	@Value("${prefix.module}")
	private String modulePrefix;

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public boolean isDuplicated(ItemFamily family, ItemFamily parent) throws Exception {
		if (getRestClientServiceForLists().module(getModuleName())
			.getOne("/find?family=" + family.getId() + "&parent=" + parent
				.getId()) != null)
			throw new DuplicateException(family + " - " + parent);
		return false;
	}

	@Override
	public RestClientService<ItemTree> getRestClientServiceForLists() {
		return restClientService;
	}

	@Override
	public String getModuleName() {
		return "itemTree";
	}

	@Override
	public RestClientService<ItemTree> getRestClientService() {
		return restClientService;
	}

	@Override
	public String getHeaderName() {
		return "Item Tree";
	}

	@Override
	public String getTitleName() {
		return username() + "@" + modulePrefix + " Item Trees";
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
	public void reset() {
	}

	@Override
	public ItemTree save(ItemFamily family, ItemFamily parent) throws Information, Exception {
		ItemTree it = new ItemTree();
		it.setFamily(family);
		it.setParent(parent);
		return save(it);
	}
}
