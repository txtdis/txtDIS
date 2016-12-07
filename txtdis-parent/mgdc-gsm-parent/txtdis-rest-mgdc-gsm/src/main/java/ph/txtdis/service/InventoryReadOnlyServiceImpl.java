package ph.txtdis.service;

import org.springframework.stereotype.Service;

import ph.txtdis.util.InventoryHttpHeader;

@Service("inventoryReadOnlyService")
public class InventoryReadOnlyServiceImpl<T>
		extends AbstractReadOnlyService<T, InventoryHttpHeader, InventoryRestService, InventoryRestServerService>
		implements InventoryReadOnlyService<T> {
}
