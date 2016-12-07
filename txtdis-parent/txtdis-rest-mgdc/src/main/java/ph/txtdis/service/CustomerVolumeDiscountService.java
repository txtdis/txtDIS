package ph.txtdis.service;

import java.util.List;

import ph.txtdis.domain.CustomerVolumeDiscountEntity;
import ph.txtdis.dto.CustomerVolumeDiscount;
import ph.txtdis.repository.CustomerVolumeDiscountRepository;

public interface CustomerVolumeDiscountService
		extends DecisionDataUpdate<CustomerVolumeDiscountEntity, CustomerVolumeDiscountRepository> {

	void updateDecisionData(String[] s);

	List<CustomerVolumeDiscount> toList(List<CustomerVolumeDiscountEntity> l);

	List<CustomerVolumeDiscountEntity> toEntities(List<CustomerVolumeDiscount> l);
}