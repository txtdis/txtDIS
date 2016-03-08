package ph.txtdis.repository;

import org.springframework.stereotype.Repository;

import ph.txtdis.domain.Truck;

@Repository("truckRepository")
public interface TruckRepository extends NameListRepository<Truck> {
}
