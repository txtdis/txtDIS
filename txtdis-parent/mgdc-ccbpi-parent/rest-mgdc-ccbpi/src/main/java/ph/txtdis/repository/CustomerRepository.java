package ph.txtdis.repository;

import org.springframework.stereotype.Repository;

import ph.txtdis.domain.Customer;

@Repository("customerRepository")
public interface CustomerRepository extends NameListRepository<Customer> {
}
