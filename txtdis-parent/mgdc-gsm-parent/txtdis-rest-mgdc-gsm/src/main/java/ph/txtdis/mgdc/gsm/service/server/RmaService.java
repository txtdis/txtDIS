package ph.txtdis.mgdc.gsm.service.server;

import ph.txtdis.dto.Billable;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.mgdc.service.server.SpunSavedReferencedKeyedService;

public interface RmaService //
	extends SpunSavedReferencedKeyedService<BillableEntity, Billable, Long> {

	Billable findOpenRmaByCustomerId(Long customerId) throws Exception;

	Billable print(Long id) throws Exception;
}
