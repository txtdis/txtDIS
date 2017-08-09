package ph.txtdis.mgdc.gsm.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.type.PartnerType;

import java.time.LocalDate;

@NoRepositoryBean
public interface PurchaseReceiptRepository //
	extends CrudRepository<BillableEntity, Long> {

	BillableEntity findByCustomerIdAndReceivingId( //
	                                               @Param("vendorId") Long vid, //
	                                               @Param("receivingId") Long rid);

	BillableEntity findFirstByCustomerTypeAndReceivingIdNotNullAndOrderDateOrderByIdAsc(//
	                                                                                    @Param("vendorType")
		                                                                                    PartnerType t, //
	                                                                                    @Param("orderDate") LocalDate
		                                                                                    d);

	BillableEntity findFirstByCustomerTypeAndReceivingIdNotNullAndIdGreaterThanOrderByIdAsc( //
	                                                                                         @Param("vendorType")
		                                                                                         PartnerType t, //
	                                                                                         @Param("receivingId")
		                                                                                         Long id);

	BillableEntity findFirstByCustomerTypeAndReceivingIdNotNullAndIdLessThanOrderByIdDesc( //
	                                                                                       @Param("vendorType")
		                                                                                       PartnerType t, //
	                                                                                       @Param("receivingId")
		                                                                                       Long id);

	BillableEntity findFirstByCustomerTypeAndReceivingIdNotNullOrderByIdAsc( //
	                                                                         @Param("vendorType") PartnerType t);

	BillableEntity findFirstByCustomerTypeAndReceivingIdNotNullOrderByIdDesc( //
	                                                                          @Param("vendorType") PartnerType t);
}
