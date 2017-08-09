package ph.txtdis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ph.txtdis.domain.EdmsWarehouse;
import ph.txtdis.repository.EdmsWarehouseRepository;

@Service("warehouseService")
public class EdmsWarehouseServiceImpl //
	implements EdmsWarehouseService {

	@Autowired
	private EdmsWarehouseRepository edmsWarehouseRepository;

	@Override
	public String getMainCode() {
		EdmsWarehouse e = edmsWarehouseRepository.findByNameContainingIgnoreCase("MAIN");
		return e.getCode();
	}

	@Override
	public String getBoCode() {
		EdmsWarehouse e = edmsWarehouseRepository.findByNameContainingIgnoreCase("BO");
		return e.getCode();
	}
}