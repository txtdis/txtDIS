package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.CustomerDiscountEntity;

@Repository("customerDiscountRepository")
public interface CustomerDiscountRepository extends CrudRepository<CustomerDiscountEntity, Long> {
}
