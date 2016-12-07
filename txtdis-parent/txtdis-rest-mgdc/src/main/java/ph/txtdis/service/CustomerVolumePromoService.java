package ph.txtdis.service;

import java.util.List;

import ph.txtdis.domain.CustomerVolumePromoEntity;
import ph.txtdis.dto.CustomerVolumePromo;
import ph.txtdis.repository.CustomerVolumePromoRepository;

public interface CustomerVolumePromoService
		extends DecisionDataUpdate<CustomerVolumePromoEntity, CustomerVolumePromoRepository> {

	void updateDecisionData(String[] s);

	List<CustomerVolumePromo> toList(List<CustomerVolumePromoEntity> l);

	List<CustomerVolumePromoEntity> toEntities(List<CustomerVolumePromo> l);
}