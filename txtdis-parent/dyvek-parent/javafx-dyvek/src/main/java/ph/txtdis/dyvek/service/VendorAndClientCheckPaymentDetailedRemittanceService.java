package ph.txtdis.dyvek.service;

import ph.txtdis.dto.Remittance;
import ph.txtdis.service.CheckPaymentDetailedRemittanceService;
import ph.txtdis.type.PartnerType;

public interface VendorAndClientCheckPaymentDetailedRemittanceService //
		extends CheckPaymentDetailedRemittanceService {

	Remittance createEntity();

	void verifyCashAdvance(PartnerType t) throws Exception;
}
