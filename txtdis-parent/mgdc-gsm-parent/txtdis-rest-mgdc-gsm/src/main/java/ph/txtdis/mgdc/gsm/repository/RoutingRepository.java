package ph.txtdis.mgdc.gsm.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ph.txtdis.mgdc.gsm.domain.CustomerEntity;
import ph.txtdis.mgdc.gsm.domain.RoutingEntity;

@Repository("routingRepository")
public interface RoutingRepository //
		extends CrudRepository<RoutingEntity, Long> {

	List<RoutingEntity> findByCustomerOrderByStartDateDesc(CustomerEntity c);
}
