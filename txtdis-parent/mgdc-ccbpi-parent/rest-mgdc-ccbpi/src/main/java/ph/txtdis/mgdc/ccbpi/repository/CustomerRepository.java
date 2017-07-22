package ph.txtdis.mgdc.ccbpi.repository;

import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.mgdc.ccbpi.domain.CustomerEntity;
import ph.txtdis.repository.NameListRepository;
import ph.txtdis.repository.SpunRepository;
import ph.txtdis.type.PartnerType;

@Repository("customerRepository")
public interface CustomerRepository //
		extends NameListRepository<CustomerEntity>, SpunRepository<CustomerEntity, Long> {

	List<CustomerEntity> findByDeactivatedOnNullAndTypeOrderByNameAsc( //
			@Param("bank") PartnerType t);

	List<CustomerEntity> findByNameContaining( //
			@Param("name") String n);

	CustomerEntity findByVendorId( //
			@Param("vendorId") Long id);

	List<CustomerEntity> findByVendorIdNotNullAndRouteHistoryNull();
}
