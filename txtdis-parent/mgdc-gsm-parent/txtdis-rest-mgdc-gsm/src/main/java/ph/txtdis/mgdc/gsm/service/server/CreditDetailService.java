package ph.txtdis.mgdc.gsm.service.server;

import ph.txtdis.dto.CreditDetail;
import ph.txtdis.mgdc.gsm.domain.CreditDetailEntity;
import ph.txtdis.mgdc.gsm.domain.CustomerEntity;
import ph.txtdis.mgdc.gsm.dto.Customer;
import ph.txtdis.mgdc.gsm.repository.CreditDetailRepository;
import ph.txtdis.service.DecisionDataUpdate;

import java.util.List;

public interface CreditDetailService
	extends DecisionDataUpdate<CreditDetailEntity, CreditDetailRepository> {

	List<CreditDetailEntity> getNewAndOldCreditDetails(CustomerEntity e, Customer c);

	List<CreditDetailEntity> getUpdatedCreditDetailDecisions(CustomerEntity e, Customer c);

	boolean hasDecisionOnNewCreditDetailsBeenMade(CustomerEntity e, Customer c);

	List<CreditDetailEntity> toEntities(List<CreditDetail> l);

	List<CreditDetail> toList(List<CreditDetailEntity> l);

	void updateDecisionData(String[] s);
}