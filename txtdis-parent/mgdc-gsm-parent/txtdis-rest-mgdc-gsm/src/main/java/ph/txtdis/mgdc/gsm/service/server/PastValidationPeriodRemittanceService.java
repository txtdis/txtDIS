package ph.txtdis.mgdc.gsm.service.server;

import java.util.List;

import ph.txtdis.mgdc.gsm.domain.RemittanceEntity;

public interface PastValidationPeriodRemittanceService //
		extends DetailedRemittanceService {

	List<RemittanceEntity> findAllUnvalidatedAfterPrescribedPeriodSincePaidHasExpired(long days);
}
