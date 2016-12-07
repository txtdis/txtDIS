package ph.txtdis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.WarehouseEntity;
import ph.txtdis.dto.Warehouse;
import ph.txtdis.repository.WarehouseRepository;

@Service("warehouseService")
public class WarehouseServiceImpl extends AbstractCreateNameListService<WarehouseRepository, WarehouseEntity, Warehouse>
		implements WarehouseService {

	@Autowired
	private PrimaryItemFamilyService familyService;

	@Override
	protected Warehouse toDTO(WarehouseEntity e) {
		if (e == null)
			return null;
		Warehouse w = new Warehouse();
		w.setId(e.getId());
		w.setName(e.getName());
		w.setFamily(familyService.toDTO(e.getFamily()));
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
		w.setFamily(familyService.toEntity(t.getFamily()));
		return w;
	}
}