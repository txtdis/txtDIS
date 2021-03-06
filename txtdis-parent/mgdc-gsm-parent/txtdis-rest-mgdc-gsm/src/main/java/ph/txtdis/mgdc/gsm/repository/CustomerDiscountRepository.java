package ph.txtdis.mgdc.gsm.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ph.txtdis.mgdc.gsm.domain.CustomerDiscountEntity;
import ph.txtdis.mgdc.gsm.domain.CustomerEntity;

import java.util.List;

@Repository("customerDiscountRepository")
public interface CustomerDiscountRepository //
	extends CrudRepository<CustomerDiscountEntity, Long> {

	List<CustomerDiscountEntity> findByCustomerAndIsValidTrue( //
	                                                           @Param("customer") CustomerEntity e);
}
