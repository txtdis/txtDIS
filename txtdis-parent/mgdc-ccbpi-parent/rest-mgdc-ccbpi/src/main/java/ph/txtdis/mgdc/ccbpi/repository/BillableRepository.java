package ph.txtdis.mgdc.ccbpi.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.mgdc.ccbpi.domain.BillableEntity;
import ph.txtdis.mgdc.ccbpi.domain.CustomerEntity;
import ph.txtdis.repository.SpunRepository;

@Repository("billableRepository")
public interface BillableRepository //
	extends SpunRepository<BillableEntity, Long> {

	List<BillableEntity> findByOrderDateBetweenAndPickingNotNull( //
	                                                              @Param("start") LocalDate s, //
	                                                              @Param("end") LocalDate e);

	List<BillableEntity> findByOrderDateBetweenAndReceivedOnNotNull( //
	                                                                 @Param("startDate") LocalDate s, //
	                                                                 @Param("endDate") LocalDate e);

	BillableEntity findFirstByBookingIdNotNullOrderByBookingIdDesc();

	BillableEntity findFirstByReceivingIdNotNullOrderByReceivingIdDesc();

	List<BillableEntity> findByPickingPrintedOnNullAndRmaNotNullAndOrderDateNullAndCustomerIn( //
	                                                                                           @Param("customers")
		                                                                                           List<CustomerEntity> c);
}
