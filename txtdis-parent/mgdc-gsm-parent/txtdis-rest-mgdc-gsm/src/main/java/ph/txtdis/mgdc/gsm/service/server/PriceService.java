package ph.txtdis.mgdc.gsm.service.server;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import ph.txtdis.dto.Price;
import ph.txtdis.mgdc.domain.PriceEntity;
import ph.txtdis.mgdc.gsm.domain.ItemEntity;
import ph.txtdis.mgdc.gsm.dto.Item;
import ph.txtdis.mgdc.repository.PriceRepository;
import ph.txtdis.service.DecisionDataUpdate;

public interface PriceService //
		extends DecisionDataUpdate<PriceEntity, PriceRepository> {

	BigDecimal getCurrentValue(String pricingType, ItemEntity e);

	BigDecimal getLatestValue(String pricingType, ItemEntity e, LocalDate d);

	List<PriceEntity> getNewAndOldPriceEntities(ItemEntity e, Item i);

	List<Price> getNewPricesNeedingApproval(Item i);

	PriceEntity getRegularEntity(String pricingType, ItemEntity e);

	BigDecimal getRegularValue(String pricingType, ItemEntity e);

	boolean hasDecisionOnNewPricingBeenMade(ItemEntity e, Item t);

	List<PriceEntity> toEntities(List<Price> l);

	List<Price> toList(List<PriceEntity> l);

	void updateDecisionData(String[] s);

	List<PriceEntity> updatePricingDecisions(ItemEntity e, Item t);
}