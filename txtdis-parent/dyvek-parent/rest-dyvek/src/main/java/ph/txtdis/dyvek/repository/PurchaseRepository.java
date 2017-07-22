package ph.txtdis.dyvek.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.dyvek.domain.BillableEntity;
import ph.txtdis.repository.SpunRepository;
import ph.txtdis.type.PartnerType;

@Repository("purchaseRepository")
public interface PurchaseRepository //
		extends SpunRepository<BillableEntity, Long> {

	List<BillableEntity> findByDeliveryNullAndCustomerTypeAndOrderClosedOnNullAndOrderEndDateLessThan( //
			@Param("vendor") PartnerType v, //
			@Param("endDate") LocalDate d);

	List<BillableEntity> findByDeliveryNotNullAndReferencesReferenceCustomerTypeAndReferencesReferenceOrderNo( // 
			@Param("vendor") PartnerType v, //
			@Param("purchaseNo") String po);

	BillableEntity findByDeliveryNullAndCustomerTypeAndOrderNo( //
			@Param("vendor") PartnerType v, //
			@Param("purchaseNo") String po);

	List<BillableEntity> findByDeliveryNullAndCustomerTypeAndOrderNoNotNullAndOrderNoContainingIgnoreCase( //
			@Param("vendor") PartnerType v, //
			@Param("purchaseNo") String po);

	BillableEntity findFirstByDeliveryNullAndCustomerTypeAndIdGreaterThanOrderByIdAsc( //
			@Param("vendor") PartnerType v, //
			@Param("id") Long id);

	BillableEntity findFirstByDeliveryNullAndCustomerTypeAndIdLessThanOrderByIdDesc( //
			@Param("vendor") PartnerType v, //
			@Param("id") Long id);

	BillableEntity findFirstByDeliveryNullAndCustomerTypeOrderByIdAsc( //
			@Param("vendor") PartnerType v);

	BillableEntity findFirstByDeliveryNullAndCustomerTypeOrderByIdDesc( //
			@Param("vendor") PartnerType v);
}
