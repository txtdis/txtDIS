package ph.txtdis.dyvek.repository;

import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.dyvek.domain.BillableEntity;
import ph.txtdis.repository.SpunRepository;

@Repository("deliveryRepository")
public interface DeliveryRepository //
		extends SpunRepository<BillableEntity, Long> {

	BillableEntity findByDeliveryNotNullAndCustomerNameAndOrderNo(//
			@Param("vendor") String v, //
			@Param("deliveryNo") String dr); //

	List<BillableEntity> findByDeliveryNotNullAndOrderNoContainingIgnoreCase( //
			@Param("deliveryNo") String dr);

	List<BillableEntity> findByDeliveryNotNullAndReferencesReferenceCustomerIdAndReferencesReferenceOrderNo( //
			@Param("customerId") Long id, //
			@Param("orderNo") String key);

	BillableEntity findFirstByDeliveryNotNullAndIdGreaterThanOrderByIdAsc( //
			@Param("id") Long id);

	BillableEntity findFirstByDeliveryNotNullAndIdLessThanOrderByIdDesc( //
			@Param("id") Long id);

	BillableEntity findFirstByDeliveryNotNullOrderByIdAsc();

	BillableEntity findFirstByDeliveryNotNullOrderByIdDesc();
}
