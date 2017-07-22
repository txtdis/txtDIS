package ph.txtdis.mgdc.gsm.service.server;

import java.util.List;

import ph.txtdis.dto.Billable;
import ph.txtdis.dto.Remittance;
import ph.txtdis.mgdc.gsm.domain.RemittanceDetailEntity;
import ph.txtdis.mgdc.gsm.domain.RemittanceEntity;
import ph.txtdis.mgdc.gsm.repository.RemittanceRepository;
import ph.txtdis.service.DecisionDataUpdate;
import ph.txtdis.service.RemittanceService;
import ph.txtdis.service.SpunSavedKeyedService;

public interface DetailedRemittanceService //
		extends DecisionDataUpdate<RemittanceEntity, RemittanceRepository>, SpunSavedKeyedService<RemittanceEntity, Remittance, Long>,
		RemittanceService {

	List<Remittance> findAll(Billable b);

	Remittance findByBillingId(Long id);

	List<RemittanceEntity> findEntitiesByBillingId(Long id);

	RemittanceEntity findEntityByCheck(String bank, Long checkId);

	List<RemittanceDetailEntity> findFullyPaidEntitiesForMaturedPostDatedChecks();

	Remittance findInvalidByBillingId(Long id);

	void saveDetails(List<RemittanceDetailEntity> l);

	void updatePaymentBasedOnValidation(String... s);
}