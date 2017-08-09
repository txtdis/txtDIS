package ph.txtdis.dyvek.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ph.txtdis.dyvek.domain.BillableEntity;
import ph.txtdis.dyvek.domain.CustomerEntity;
import ph.txtdis.repository.SpunRepository;
import ph.txtdis.type.PartnerType;

import java.util.List;

@Repository("salesRepository")
public interface SalesRepository //
	extends SpunRepository<BillableEntity, Long> {

	List<BillableEntity> findByCustomerTypeAndOrderClosedOnNullAndDeliveryNull( //
	                                                                            @Param("client") PartnerType c);

	List<BillableEntity> findByReferencesReferenceCustomerAndReferencesReferenceOrderNo( //
	                                                                                     @Param("client")
		                                                                                     CustomerEntity c, //
	                                                                                     @Param("salesNo") String so);

	BillableEntity findByDeliveryNullAndCustomerNameAndOrderNo( //
	                                                            @Param("client") String c, //
	                                                            @Param("salesNo") String so);

	List<BillableEntity> findByCustomerTypeAndOrderNoNotNullAndOrderNoContainingIgnoreCase( //
	                                                                                        @Param("client")
		                                                                                        PartnerType c, //
	                                                                                        @Param("salesNo") String so);

	BillableEntity findFirstByCustomerTypeAndIdGreaterThanOrderByIdAsc( //
	                                                                    @Param("client") PartnerType c, //
	                                                                    @Param("id") Long id);

	BillableEntity findFirstByCustomerTypeAndIdLessThanOrderByIdDesc( //
	                                                                  @Param("client") PartnerType c, //
	                                                                  @Param("id") Long id);

	BillableEntity findFirstByCustomerTypeOrderByIdAsc( //
	                                                    @Param("client") PartnerType c);

	BillableEntity findFirstByCustomerTypeOrderByIdDesc( //
	                                                     @Param("client") PartnerType c);
}