package ph.txtdis.dyvek.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ph.txtdis.dyvek.model.Item;
import ph.txtdis.info.Information;
import ph.txtdis.service.RestClientService;
import ph.txtdis.util.ClientTypeMap;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service("itemService")
public class ItemServiceImpl //
	implements ItemService {

	@Autowired
	private RestClientService<Item> restClientService;

	@Autowired
	private ClientTypeMap typeMap;

	@Override
	public RestClientService<Item> getRestClientService() {
		return restClientService;
	}

	@Override
	public RestClientService<Item> getRestClientServiceForLists() {
		return restClientService;
	}

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public List<String> listNames() {
		try {
			return list().stream().map(e -> e.getName()).collect(toList());
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public List<Item> list() {
		try {
			return restClientService.module(getModuleName()).getList("");
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String getModuleName() {
		return "item";
	}

	@Override
	public void reset() {
	}

	@Override
	public Item save(String name) throws Information, Exception {
		Item i = new Item();
		i.setName(name);
		return restClientService.module(getModuleName()).save(i);
	}
}
