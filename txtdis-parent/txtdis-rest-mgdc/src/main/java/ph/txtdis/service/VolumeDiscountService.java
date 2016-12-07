package ph.txtdis.service;

import java.util.List;

import ph.txtdis.domain.ItemEntity;
import ph.txtdis.domain.VolumeDiscountEntity;
import ph.txtdis.dto.Item;
import ph.txtdis.dto.VolumeDiscount;
import ph.txtdis.repository.VolumeDiscountRepository;

public interface VolumeDiscountService extends DecisionDataUpdate<VolumeDiscountEntity, VolumeDiscountRepository> {

	void updateDecisionData(String[] s);

	List<VolumeDiscountEntity> toEntities(List<VolumeDiscount> l);

	List<VolumeDiscount> toList(List<VolumeDiscountEntity> l);

	boolean decisionOnNewVolumeDiscountMade(ItemEntity e, Item t);

	List<VolumeDiscountEntity> newDiscounts(ItemEntity e, Item t);

	List<VolumeDiscountEntity> getUpdatedVolumeDiscountDecisions(ItemEntity e, Item t);

}