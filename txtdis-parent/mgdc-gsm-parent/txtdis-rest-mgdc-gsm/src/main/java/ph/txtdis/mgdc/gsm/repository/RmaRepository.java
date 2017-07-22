package ph.txtdis.mgdc.gsm.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import ph.txtdis.mgdc.gsm.domain.BillableEntity;

@NoRepositoryBean
public interface RmaRepository //
		extends CrudRepository<BillableEntity, Long> {

	//open rma
	List<BillableEntity> findByNumIdNullAndRmaNotNullAndCustomerId(@Param("customerId") Long id);

	BillableEntity findByRmaAndBookingId( //
			@Param("rma") boolean b, //
			@Param("id") Long id);

	BillableEntity findFirstByRmaAndIdGreaterThanOrderByIdAsc( //
			@Param("rma") boolean b, //
			@Param("id") Long id);

	BillableEntity findFirstByRmaAndIdLessThanOrderByIdDesc( //
			@Param("rma") boolean b, //
			@Param("id") Long id);

	BillableEntity findFirstByRmaOrderByIdAsc( //
			@Param("rma") boolean b);

	BillableEntity findFirstByRmaOrderByIdDesc( //
			@Param("rma") boolean b);
}
