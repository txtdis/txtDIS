package ph.txtdis.service;

import ph.txtdis.dto.Remittance;

public interface RemittanceService {

	Remittance findByCheck(String bank, Long checkId);
}