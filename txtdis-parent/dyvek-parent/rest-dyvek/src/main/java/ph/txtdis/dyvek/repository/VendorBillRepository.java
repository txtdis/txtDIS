package ph.txtdis.dyvek.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ph.txtdis.dyvek.domain.BillableEntity;
import ph.txtdis.dyvek.domain.CustomerEntity;
import ph.txtdis.dyvek.domain.ItemEntity;
import ph.txtdis.repository.SpunRepository;
import ph.txtdis.type.PartnerType;

import java.util.List;

@Repository("vendorBillRepository")
public interface VendorBillRepository
	extends SpunRepository<BillableEntity, Long> {

	List<BillableEntity> findByDeliveryNotNullAndDeliveryAssignedToPurchaseOnNullOrderByOrderDateAsc();

	List<BillableEntity> findByDeliveryNotNullAndDeliveryAssignedToPurchaseOnNullAndCustomerName(
		@Param("vendor") String v);

	List<BillableEntity>
	findByDeliveryNotNullAndDeliveryAssignedToPurchaseOnNullAndCustomerTypeAndOrderNoContainingIgnoreCase(
		@Param("vendor") PartnerType v,
		@Param("deliveryNo") String no);

	List<BillableEntity> findByDeliveryAssignedToPurchaseOnNotNullAndOrderNoContainingIgnoreCase(
		@Param("deliveryNo") String dr);

	List<BillableEntity> findByDeliveryNullAndOrderClosedOnNullAndCustomerAndItemOrderByOrderDateAsc(
		@Param("vendor") CustomerEntity v,
		@Param("item") ItemEntity i	);

	BillableEntity findFirstByDeliveryAssignedToPurchaseOnNotNullAndIdGreaterThanOrderByIdAsc(
		@Param("id") Long id);

	BillableEntity findFirstByDeliveryAssignedToPurchaseOnNotNullAndIdLessThanOrderByIdDesc(
		@Param("id") Long id);

	BillableEntity findFirstByDeliveryAssignedToPurchaseOnNotNullOrderByIdAsc();

	BillableEntity findFirstByDeliveryAssignedToPurchaseOnNotNullOrderByIdDesc();
}
