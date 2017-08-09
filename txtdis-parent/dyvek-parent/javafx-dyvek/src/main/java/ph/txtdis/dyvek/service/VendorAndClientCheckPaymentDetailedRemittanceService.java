package ph.txtdis.dyvek.service;

import ph.txtdis.dto.Remittance;
import ph.txtdis.service.CheckPaymentDetailedRemittanceService;
import ph.txtdis.type.PartnerType;

import java.time.LocalDate;

public interface VendorAndClientCheckPaymentDetailedRemittanceService //
	extends CheckPaymentDetailedRemittanceService {

	Remittance createEntity();

	LocalDate getCheckDate();

	void verifyCashAdvance(PartnerType t) throws Exception;
}
