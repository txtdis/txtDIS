package ph.txtdis.dyvek.repository;

import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.dyvek.domain.BillableEntity;
import ph.txtdis.dyvek.domain.CustomerEntity;
import ph.txtdis.repository.SpunRepository;
import ph.txtdis.type.PartnerType;

@Repository("vendorBillRepository")
public interface VendorBillRepository //
		extends SpunRepository<BillableEntity, Long> {

	List<BillableEntity> findByDeliveryNotNullAndDeliveryAssignedToPurchaseOnNullOrderByOrderDateAsc();

	List<BillableEntity> findByDeliveryNotNullAndDeliveryAssignedToPurchaseOnNullAndCustomerName( //
			@Param("vendor") String v);

	List<BillableEntity> findByDeliveryNotNullAndDeliveryAssignedToPurchaseOnNullAndCustomerTypeAndOrderNoContainingIgnoreCase( //
			@Param("vendor") PartnerType v, //
			@Param("deliveryNo") String no);

	List<BillableEntity> findByDeliveryAssignedToPurchaseOnNotNullAndOrderNoContainingIgnoreCase( //
			@Param("deliveryNo") String dr);

	List<BillableEntity> findByDeliveryNullAndOrderClosedOnNullAndCustomerOrderByOrderDateAsc( //
			@Param("vendor") CustomerEntity v);

	BillableEntity findFirstByDeliveryAssignedToPurchaseOnNotNullAndIdGreaterThanOrderByIdAsc( //
			@Param("id") Long id);

	BillableEntity findFirstByDeliveryAssignedToPurchaseOnNotNullAndIdLessThanOrderByIdDesc( //
			@Param("id") Long id);

	BillableEntity findFirstByDeliveryAssignedToPurchaseOnNotNullOrderByIdAsc();

	BillableEntity findFirstByDeliveryAssignedToPurchaseOnNotNullOrderByIdDesc();
}
