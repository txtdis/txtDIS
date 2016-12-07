package ph.txtdis.service;

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
public class ItemServiceImpl extends AbstractItemService implements ImportedItemService {

	@Autowired
	private ReadOnlyService<Item> readOnlyService;

	@Override
	public void importAll() throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException,
			RestException, InvalidException {
		List<Item> l = readOnlyService.module("item").getList();
		repository.save(toEntities(l));
	}
}