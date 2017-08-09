package ph.txtdis.mgdc.gsm.service.server;

import org.springframework.stereotype.Service;
import ph.txtdis.service.AbstractRestService;

@Service("inventoryRestService")
public class InventoryRestServiceImpl
	extends AbstractRestService<InventoryRestServerService>
	implements InventoryRestService {

	public InventoryRestServiceImpl(InventoryRestServerService serverService) {
		super(serverService);
	}
}
