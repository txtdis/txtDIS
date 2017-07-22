package ph.txtdis.service;

import ph.txtdis.domain.TruckEntity;
import ph.txtdis.dto.Truck;
import ph.txtdis.repository.TruckRepository;

public abstract class AbstractTruckService //
		extends AbstractCreateNameListService<TruckRepository, TruckEntity, Truck> //
		implements ServerTruckService {

	@Override
	public Truck toModel(TruckEntity e) {
		return e == null ? null : newTruck(e);
	}

	private Truck newTruck(TruckEntity e) {
		Truck t = new Truck();
		t.setId(e.getId());
		t.setName(e.getName());
		t.setCreatedBy(e.getCreatedBy());
		t.setCreatedOn(e.getCreatedOn());
		return t;
	}

	@Override
	public TruckEntity toEntity(Truck t) {
		return t == null ? null : newEntity(t);
	}

	private TruckEntity newEntity(Truck t) {
		TruckEntity e = new TruckEntity();
		e.setName(t.getName());
		return e;
	}

	@Override
	public TruckEntity findEntityByName(String truck) {
		return repository.findByNameIgnoreCase(truck);
	}
}