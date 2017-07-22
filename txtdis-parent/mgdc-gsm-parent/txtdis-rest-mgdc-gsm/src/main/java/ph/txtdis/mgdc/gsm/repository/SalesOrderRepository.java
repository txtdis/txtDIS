package ph.txtdis.mgdc.gsm.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.type.PartnerType;

@Repository("salesOrderRepository")
public interface SalesOrderRepository //
		extends BookingRepository {

	List<BillableEntity> findByPickingNotNullAndReceivedOnNotNullOrderByPickingPickDateAscPickingIdAsc();

	List<BillableEntity> findByPickingNotNullOrderByPickingPickDateAscPickingIdAsc();

	BillableEntity findFirstByNumIdNullAndRmaNullAndCustomerTypeAndPickingNotNullAndOrderDateBetween( //
			@Param("outlet") PartnerType t, //
			@Param("start") LocalDate s, //
			@Param("end") LocalDate e);

	BillableEntity findFirstByNumIdNullAndRmaNullAndCustomerTypeNotAndPickingNullAndOrderDateBetween( //
			@Param("vendor") PartnerType t, //
			@Param("start") LocalDate s, //
			@Param("end") LocalDate e);
}
