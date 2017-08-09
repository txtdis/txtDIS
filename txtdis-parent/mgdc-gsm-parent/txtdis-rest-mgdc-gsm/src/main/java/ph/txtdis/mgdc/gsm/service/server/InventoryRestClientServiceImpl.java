package ph.txtdis.mgdc.gsm.service.server;

import org.springframework.stereotype.Service;
import ph.txtdis.mgdc.gsm.util.InventoryHttpHeader;
import ph.txtdis.service.AbstractRestClientService;

@Service("inventoryRestClientService")
public class InventoryRestClientServiceImpl<T>
	extends AbstractRestClientService<T, InventoryHttpHeader, InventoryRestService, InventoryRestServerService>
	implements InventoryRestClientService<T> {
}
