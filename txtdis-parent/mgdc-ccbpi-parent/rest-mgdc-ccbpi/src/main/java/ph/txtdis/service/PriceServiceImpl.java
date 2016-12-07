package ph.txtdis.service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import ph.txtdis.domain.ItemEntity;
import ph.txtdis.domain.PriceEntity;
import ph.txtdis.dto.Item;
import ph.txtdis.dto.Price;

@Service("priceService")
public class PriceServiceImpl extends AbstractPriceService {

	@Override
	public List<PriceEntity> newPrices(ItemEntity e, Item i) {
		List<PriceEntity> l = super.newPrices(e, i);
		return approve(l);
	}

	private List<PriceEntity> approve(List<PriceEntity> l) {
		return l.stream().map(p -> approve(p)).collect(Collectors.toList());
	}

	private PriceEntity approve(PriceEntity e) {
		e.setIsValid(true);
		e.setDecidedBy("SYSGEN");
		e.setDecidedOn(ZonedDateTime.now());
		return e;
	}

	@Override
	public List<PriceEntity> toEntities(List<Price> prices) {
		List<PriceEntity> l = super.toEntities(prices);
		return approve(l);
	}
}
