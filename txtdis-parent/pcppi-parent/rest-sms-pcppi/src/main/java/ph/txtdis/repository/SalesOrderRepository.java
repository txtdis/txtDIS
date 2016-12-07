package ph.txtdis.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.CustomerImpl;
import ph.txtdis.domain.SalesOrder;

@Repository("salesOrderRepository")
public interface SalesOrderRepository extends SpunRepository<SalesOrder, Long> {

	SalesOrder findByIdAndCustomer(@Param("id") Long id, @Param("customer") CustomerImpl c);
}
