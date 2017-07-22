package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.EdmsSalesOrder;

@Repository("edmsSalesOrderRepository")
public interface EdmsSalesOrderRepository //
		extends CrudRepository<EdmsSalesOrder, Long> {

	EdmsSalesOrder findByReferenceNo( //
			@Param("bookingNo") String b);
}