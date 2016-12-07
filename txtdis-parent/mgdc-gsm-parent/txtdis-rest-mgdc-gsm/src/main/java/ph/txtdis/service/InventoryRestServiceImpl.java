package ph.txtdis.service;

import org.springframework.stereotype.Service;

@Service("inventoryRestService")
public class InventoryRestServiceImpl extends AbstractRestService<InventoryRestServerService>
		implements InventoryRestService {
}
