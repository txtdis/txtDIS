package ph.txtdis.dyvek.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ph.txtdis.dyvek.domain.CustomerEntity;
import ph.txtdis.repository.NameListRepository;
import ph.txtdis.type.PartnerType;

import java.util.List;

@Repository("customerRepository")
public interface CustomerRepository //
	extends NameListRepository<CustomerEntity> {

	List<CustomerEntity> findByType( //
	                                 @Param("type") PartnerType t);

	List<CustomerEntity> findByTypeIn( //
	                                   @Param("types") List<PartnerType> l);
}
