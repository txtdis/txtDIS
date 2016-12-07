package ph.txtdis.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.BomEntity;
import ph.txtdis.domain.ItemEntity;

@Repository("bomRepository")
public interface BomRepository extends CrudRepository<BomEntity, Long> {

	List<BomEntity> findByItem(@Param("item") ItemEntity item);
}
