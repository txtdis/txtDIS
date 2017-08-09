package ph.txtdis.dyvek.service.server;

import ph.txtdis.dto.Remittance;
import ph.txtdis.dto.RemittanceDetail;
import ph.txtdis.dyvek.domain.RemittanceDetailEntity;
import ph.txtdis.dyvek.domain.RemittanceEntity;
import ph.txtdis.dyvek.model.Billable;
import ph.txtdis.dyvek.repository.RemittanceRepository;
import ph.txtdis.service.DecisionDataUpdate;
import ph.txtdis.service.RemittanceService;
import ph.txtdis.service.SpunSavedKeyedService;

import java.util.List;

public interface DyvekRemittanceService
	extends DecisionDataUpdate<RemittanceEntity, RemittanceRepository>,
	RemittanceService,
	SpunSavedKeyedService<RemittanceEntity, Remittance, Long> {

	List<Remittance> findAll(Billable b);

	List<RemittanceEntity> findEntitiesByBillingId(Long id);

	RemittanceEntity findEntityByCheck(String bank, Long checkId);

	List<RemittanceDetail> findLiquidationsByCashAdvanceId(Long id);

	void saveDetails(List<RemittanceDetailEntity> l);

	List<RemittanceDetail> toDetails(List<RemittanceDetailEntity> l);
}