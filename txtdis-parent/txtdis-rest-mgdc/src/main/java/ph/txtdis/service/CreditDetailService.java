package ph.txtdis.service;

import java.util.List;

import ph.txtdis.domain.CreditDetailEntity;
import ph.txtdis.dto.CreditDetail;
import ph.txtdis.repository.CreditDetailRepository;

public interface CreditDetailService extends DecisionDataUpdate<CreditDetailEntity, CreditDetailRepository> {

	void updateDecisionData(String[] s);

	List<CreditDetail> toList(List<CreditDetailEntity> l);

	List<CreditDetailEntity> toEntities(List<CreditDetail> l);

}