package ph.txtdis.mgdc.gsm.service.server;

import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.mgdc.domain.PriceEntity;
import ph.txtdis.mgdc.gsm.domain.BomEntity;
import ph.txtdis.mgdc.gsm.domain.ItemEntity;
import ph.txtdis.mgdc.gsm.dto.Item;
import ph.txtdis.mgdc.service.server.ConvertibleItemFamilyService;

import java.util.List;

public abstract class AbstractPricedItemService //
	extends AbstractItemService {

	@Autowired
	protected PriceService priceService;

	@Autowired
	private BomService bomService;

	@Autowired
	private ConvertibleItemFamilyService familyService;

	@Override
	protected ItemEntity newEntity(Item i) {
		ItemEntity e = super.newEntity(i);
		e.setFamily(familyService.toEntity(i.getFamily()));
		e.setBoms(getBoms(e, i));
		return e;
	}

	private List<BomEntity> getBoms(ItemEntity e, Item i) {
		return bomService.toEntities(e, i);
	}

	@Override
	public ItemEntity toEntity(Item i) {
		ItemEntity e = super.toEntity(i);
		if (e.getDeactivatedOn() != null)
			return e;
		if (i.getPriceList() != null)
			e.setPriceList(priceList(e, i));
		return e;
	}

	private List<PriceEntity> priceList(ItemEntity e, Item i) {
		if (e.getPriceList() == null)
			return priceService.toEntities(i.getPriceList());
		if (e.getPriceList().size() < i.getPriceList().size())
			return priceService.getNewAndOldPriceEntities(e, i);
		if (priceService.hasDecisionOnNewPricingBeenMade(e, i))
			return priceService.updatePricingDecisions(e, i);
		return e.getPriceList();
	}

	@Override
	public Item toModel(ItemEntity e) {
		Item i = super.toModel(e);
		if (i == null)
			return null;
		i.setBoms(bomService.toBoms(e.getBoms()));
		i.setFamily(familyService.toModel(e.getFamily()));
		i.setPriceList(priceService.toList(e.getPriceList()));
		return i;
	}
}