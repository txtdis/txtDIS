package ph.txtdis.mgdc.gsm.service.server;

import org.springframework.stereotype.Service;

import ph.txtdis.mgdc.gsm.util.InventoryHttpHeader;
import ph.txtdis.service.AbstractReadOnlyService;

@Service("inventoryReadOnlyService")
public class InventoryReadOnlyServiceImpl<T>
		extends AbstractReadOnlyService<T, InventoryHttpHeader, InventoryRestService, InventoryRestServerService>
		implements InventoryReadOnlyService<T> {
}
