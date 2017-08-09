package ph.txtdis.mgdc.gsm.service.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ph.txtdis.mgdc.gsm.dto.Item;
import ph.txtdis.service.RestClientService;

import java.util.List;

@Service("itemService")
public class ItemServiceImpl
	extends AbstractPricedItemService //
	implements ImportedItemService {

	private static final String ITEM = "item";

	@Autowired
	private RestClientService<Item> restClientService;

	@Override
	public void importAll() throws Exception {
		List<Item> l = restClientService.module(ITEM).getList();
		repository.save(toEntities(l));
	}

	@Override
	@Transactional
	public Item save(Item i) {
		try {
			i = super.save(i);
			return i.getVendorNo() == null ? i : saveToEdms(i);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Item saveToEdms(Item i) throws Exception {
		restClientService.module(ITEM).save(i);
		return i;
	}
}