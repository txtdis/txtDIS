package ph.txtdis.dyvek.service;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dyvek.model.Item;
import ph.txtdis.info.Information;
import ph.txtdis.service.ReadOnlyService;
import ph.txtdis.service.SavingService;
import ph.txtdis.util.ClientTypeMap;

@Service("itemService")
public class ItemServiceImpl //
		implements ItemService {

	@Autowired
	private ReadOnlyService<Item> readOnlyService;

	@Autowired
	private SavingService<Item> savingService;

	@Autowired
	private ClientTypeMap typeMap;

	@Override
	public ReadOnlyService<Item> getListedReadOnlyService() {
		return readOnlyService;
	}

	@Override
	public String getModuleName() {
		return "item";
	}

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public List<Item> list() {
		try {
			return getListedReadOnlyService().module(getModuleName()).getList("");
		} catch (Exception e) {
			return null;
		}
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
	public void reset() {
	}

	@Override
	public Item save(String name) throws Information, Exception {
		Item i = new Item();
		i.setName(name);
		return savingService.module(getModuleName()).save(i);
	}
}
