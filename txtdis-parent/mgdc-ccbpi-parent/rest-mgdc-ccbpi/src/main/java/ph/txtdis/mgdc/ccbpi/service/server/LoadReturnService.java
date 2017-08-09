package ph.txtdis.mgdc.ccbpi.service.server;

import ph.txtdis.dto.PickList;
import ph.txtdis.mgdc.ccbpi.domain.PickListEntity;
import ph.txtdis.mgdc.service.server.SpunSavedReferencedKeyedService;

public interface LoadReturnService //
	extends SpunSavedReferencedKeyedService<PickListEntity, PickList, Long> {
}
