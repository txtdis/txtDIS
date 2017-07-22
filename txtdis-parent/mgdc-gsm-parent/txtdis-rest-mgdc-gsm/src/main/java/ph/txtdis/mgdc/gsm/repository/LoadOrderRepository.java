package ph.txtdis.mgdc.gsm.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.type.PartnerType;

@Repository("loadOrderRepository")
public interface LoadOrderRepository //
		extends BookingRepository {

	BillableEntity findByCustomerNameAndOrderDate(//
			@Param("exTruck") String t, //
			@Param("date") LocalDate d);

	List<BillableEntity> findByCustomerTypeAndPickingNotNullAndOrderDateBetween( //
			@Param("exTruck") PartnerType t, //
			@Param("goLive") LocalDate s, //
			@Param("cutOff") LocalDate e);

	BillableEntity findFirstByCustomerTypeAndPickingNullAndOrderDateBetween( //
			@Param("exTruck") PartnerType t, //
			@Param("goLive") LocalDate s, //
			@Param("cutOff") LocalDate e);
}
