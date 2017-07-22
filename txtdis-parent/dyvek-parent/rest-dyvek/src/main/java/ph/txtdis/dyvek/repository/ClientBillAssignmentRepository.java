package ph.txtdis.dyvek.repository;

import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.dyvek.domain.BillableEntity;
import ph.txtdis.dyvek.domain.CustomerEntity;
import ph.txtdis.repository.SpunRepository;

@Repository("clientBillAssignmentRepository")
public interface ClientBillAssignmentRepository //
		extends SpunRepository<BillableEntity, Long> {

	List<BillableEntity> findByDeliveryNotNullAndDeliveryAssignedToSalesOnNullOrderByOrderDateAsc();

	List<BillableEntity> findByDeliveryAssignedToSalesOnNotNullAndOrderNoContainingIgnoreCase( //
			@Param("deliveryNo") String dr);

	List<BillableEntity> findByDeliveryNullAndOrderClosedOnNullAndCustomerOrderByOrderDateAsc( //
			@Param("client") CustomerEntity c);

	BillableEntity findFirstByDeliveryAssignedToSalesOnNotNullAndIdGreaterThanOrderByIdAsc( //
			@Param("id") Long id);

	BillableEntity findFirstByDeliveryAssignedToSalesOnNotNullAndIdLessThanOrderByIdDesc( //
			@Param("id") Long id);

	BillableEntity findFirstByDeliveryAssignedToSalesOnNotNullOrderByIdAsc();

	BillableEntity findFirstByDeliveryAssignedToSalesOnNotNullOrderByIdDesc();

	List<BillableEntity> findByReferencesReferenceCustomerAndReferencesReferenceOrderNo( //
			@Param("client") CustomerEntity c, //
			@Param("salesNo") String so);
}
