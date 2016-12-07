package ph.txtdis.repository;

import org.springframework.stereotype.Repository;

import ph.txtdis.domain.EdmsTruck;

@Repository("edmsTruckRepository")
public interface EdmsTruckRepository extends CodeNameRepository<EdmsTruck> {

	EdmsTruck findFirstByOrderByCodeDesc();
}
