package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.EdmsLoadOrderDetail;

@Repository("edmsLoadOrderDetailRepository")
public interface EdmsLoadOrderDetailRepository extends CrudRepository<EdmsLoadOrderDetail, Long> {
}
