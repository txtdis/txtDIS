package ph.txtdis.mgdc.gsm.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.type.PartnerType;

@Repository("receivingReportRepository")
public interface ReceivingReportRepository //
		extends CrudRepository<BillableEntity, Long> {

	BillableEntity findByCustomerTypeInAndRmaNullAndReceivingId( //
			@Param("types") List<PartnerType> l, //
			@Param("id") Long id);

	BillableEntity findFirstByCustomerTypeInAndRmaNullAndReceivingIdNotNullAndIdGreaterThanOrderByIdAsc( //
			@Param("types") List<PartnerType> l, //
			@Param("id") Long id);

	BillableEntity findFirstByCustomerTypeInAndRmaNullAndReceivingIdNotNullAndIdLessThanOrderByIdDesc( //
			@Param("types") List<PartnerType> l, //
			@Param("id") Long id);

	BillableEntity findFirstByCustomerTypeInAndRmaNullAndReceivingIdNotNullOrderByIdAsc( //
			@Param("types") List<PartnerType> l);

	BillableEntity findFirstByCustomerTypeInAndRmaNullAndReceivingIdNotNullOrderByIdDesc( //
			@Param("types") List<PartnerType> l);
}
