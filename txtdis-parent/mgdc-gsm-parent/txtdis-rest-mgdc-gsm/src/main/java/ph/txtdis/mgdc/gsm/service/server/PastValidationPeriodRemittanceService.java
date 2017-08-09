package ph.txtdis.mgdc.gsm.service.server;

import ph.txtdis.mgdc.gsm.domain.RemittanceEntity;

import java.util.List;

public interface PastValidationPeriodRemittanceService //
	extends DetailedRemittanceService {

	List<RemittanceEntity> findAllUnvalidatedAfterPrescribedPeriodSincePaidHasExpired(long days);
}
