package ph.txtdis.repository;

import org.springframework.stereotype.Repository;

import ph.txtdis.domain.TruckEntity;

@Repository("truckRepository")
public interface TruckRepository extends NameListRepository<TruckEntity> {
}
