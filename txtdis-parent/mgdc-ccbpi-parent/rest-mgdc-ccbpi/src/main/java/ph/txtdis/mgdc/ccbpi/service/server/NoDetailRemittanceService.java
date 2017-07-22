package ph.txtdis.mgdc.ccbpi.service.server;

import ph.txtdis.dto.Remittance;
import ph.txtdis.mgdc.ccbpi.domain.RemittanceEntity;
import ph.txtdis.mgdc.ccbpi.repository.RemittanceRepository;
import ph.txtdis.service.DecisionDataUpdate;
import ph.txtdis.service.RemittanceService;
import ph.txtdis.service.SpunSavedKeyedService;

public interface NoDetailRemittanceService //
		extends DecisionDataUpdate<RemittanceEntity, RemittanceRepository>, SpunSavedKeyedService<RemittanceEntity, Remittance, Long>,
		RemittanceService {

	RemittanceEntity findEntityByCheck(String bank, Long checkId);
}