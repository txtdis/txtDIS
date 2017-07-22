package ph.txtdis.mgdc.gsm.service.server;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ph.txtdis.mgdc.gsm.dto.Item;
import ph.txtdis.service.ReadOnlyService;
import ph.txtdis.service.SavingService;

@Service("itemService")
public class ItemServiceImpl
		extends AbstractPricedItemService //
		implements ImportedItemService {

	private static final String ITEM = "item";

	@Autowired
	private SavingService<Item> savingService;

	@Autowired
	private ReadOnlyService<Item> readOnlyService;

	@Override
	public void importAll() throws Exception {
		List<Item> l = readOnlyService.module(ITEM).getList();
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
		savingService.module(ITEM).save(i);
		return i;
	}
}