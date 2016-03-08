package ph.txtdis.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.Customer;
import ph.txtdis.domain.SalesOrder;

@Repository("salesOrderRepository")
public interface SalesOrderRepository extends SpunRepository<SalesOrder, Long> {

	SalesOrder findByIdAndCustomer(@Param("id") Long id, @Param("customer") Customer c);
}
