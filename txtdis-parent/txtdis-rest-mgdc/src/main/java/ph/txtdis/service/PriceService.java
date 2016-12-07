package ph.txtdis.service;

import java.math.BigDecimal;
import java.util.List;

import ph.txtdis.domain.ItemEntity;
import ph.txtdis.domain.PriceEntity;
import ph.txtdis.dto.Item;
import ph.txtdis.dto.Price;
import ph.txtdis.repository.PriceRepository;

public interface PriceService extends DecisionDataUpdate<PriceEntity, PriceRepository> {

	boolean decisionOnNewPricingMade(ItemEntity e, Item t);

	BigDecimal getLatest(String pricingType, ItemEntity e);

	List<Price> getPendingPrices(Item i);

	List<PriceEntity> getUpdatedPricingDecisions(ItemEntity e, Item t);

	List<PriceEntity> newPrices(ItemEntity e, Item i);

	List<PriceEntity> toEntities(List<Price> l);

	List<Price> toList(List<PriceEntity> l);

	void updateDecisionData(String[] s);
}