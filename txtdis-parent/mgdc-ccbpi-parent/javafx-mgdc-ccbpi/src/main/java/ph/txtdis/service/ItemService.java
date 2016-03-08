package ph.txtdis.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Item;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;

@Service("itemService")
public class ItemService implements ByNameSearchable<Item>, Iconed, Listed<Item>, Unique<Item>, UniquelyNamed<Item> {

	@Autowired
	private ReadOnlyService<Item> readOnlyService;

	@Autowired
	private SavingService<Item> savingService;

	@Override
	public String getModule() {
		return "item";
	}

	@Override
	public ReadOnlyService<Item> getReadOnlyService() {
		return readOnlyService;
	}

	public Item save(Item i) throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException,
			InvalidException, RestException {
		return savingService.module(getModule()).save(i);
	}

	public Item save(Long id, String name, int bottlePerCase, BigDecimal price) throws NoServerConnectionException,
			StoppedServerException, FailedAuthenticationException, InvalidException, RestException {
		Item i = new Item();
		i.setCode(id);
		i.setName(name);
		i.setBottlePerCase(bottlePerCase);
		i.setPriceValue(price);
		return save(i);
	}

	@Override
	public List<Item> search(String name) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, InvalidException, RestException {
		String endpoint = name.isEmpty() ? "" : "/search?name=" + name;
		return readOnlyService.module(getModule()).getList(endpoint);
	}
}
