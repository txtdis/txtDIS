package ph.txtdis.dyvek.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.dyvek.domain.BillableReferenceEntity;
import ph.txtdis.dyvek.domain.CustomerEntity;
import ph.txtdis.type.PartnerType;

@Repository("billableReferenceRepository")
public interface BillableReferenceRepository //
		extends CrudRepository<BillableReferenceEntity, Long> {

	List<BillableReferenceEntity> findByReferenceCustomerAndReferenceOrderNo( //
			@Param("client") CustomerEntity c, //
			@Param("salesNo") String so);

	List<BillableReferenceEntity> findByReferenceCustomerTypeAndBillingNullOrderByReferenceOrderDateAsc( //
			@Param("client") PartnerType c);
}
