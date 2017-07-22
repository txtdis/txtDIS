package ph.txtdis.mgdc.service.server;

import org.springframework.stereotype.Service;

import ph.txtdis.dto.Warehouse;
import ph.txtdis.mgdc.domain.WarehouseEntity;
import ph.txtdis.mgdc.repository.WarehouseRepository;
import ph.txtdis.service.AbstractCreateNameListService;

@Service("warehouseService")
public class WarehouseServiceImpl //
		extends AbstractCreateNameListService<WarehouseRepository, WarehouseEntity, Warehouse> //
		implements WarehouseService {

	@Override
	protected Warehouse toModel(WarehouseEntity e) {
		if (e == null)
			return null;
		Warehouse w = new Warehouse();
		w.setId(e.getId());
		w.setName(e.getName());
		w.setCreatedBy(e.getCreatedBy());
		w.setCreatedOn(e.getCreatedOn());
		return w;
	}

	@Override
	protected WarehouseEntity toEntity(Warehouse t) {
		if (t == null)
			return null;
		WarehouseEntity w = new WarehouseEntity();
		w.setName(t.getName());
		return w;
	}
}