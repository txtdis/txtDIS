package ph.txtdis.service;

import java.util.List;

import ph.txtdis.domain.CustomerDiscountEntity;
import ph.txtdis.dto.CustomerDiscount;
import ph.txtdis.repository.CustomerDiscountRepository;

public interface CustomerDiscountService
		extends DecisionDataUpdate<CustomerDiscountEntity, CustomerDiscountRepository> {

	void updateDecisionData(String[] s);

	List<CustomerDiscount> toList(List<CustomerDiscountEntity> l);

	List<CustomerDiscountEntity> toEntities(List<CustomerDiscount> l);
}