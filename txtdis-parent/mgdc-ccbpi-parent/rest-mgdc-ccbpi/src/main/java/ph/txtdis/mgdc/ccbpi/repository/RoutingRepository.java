package ph.txtdis.mgdc.ccbpi.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.mgdc.ccbpi.domain.CustomerEntity;
import ph.txtdis.mgdc.ccbpi.domain.RoutingEntity;

@Repository("routingRepository")
public interface RoutingRepository //
		extends CrudRepository<RoutingEntity, Long> {

	List<RoutingEntity> findByCustomerOrderByStartDateDesc( //
			@Param("customer") CustomerEntity c);
}
