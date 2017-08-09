package ph.txtdis.mgdc.gsm.service.server;

import ph.txtdis.dto.Billable;
import ph.txtdis.dto.RemittanceDetail;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.type.ModuleType;

import java.time.LocalDate;
import java.util.List;

public interface AllBillingService //
	extends BillingService {

	List<Billable> findAllAged(Long customerId);

	List<Billable> findAllAging(Long customerId);

	List<Billable> findAllUnpaid(Long customerId);

	List<BillableEntity> findAllValidOutletBillingsBetweenDates(LocalDate start, LocalDate end);

	Billable findByBookingId(Long id) throws Exception;

	Billable findByCustomerId(Long customerId);

	Billable findByCustomerPurchasedItemId(Long customerId, Long itemId);

	Billable findByOrderNo(String p, Long id, String s);

	BillableEntity findEntity(RemittanceDetail d);

	BillableEntity findEntityByOrderNo(String prefix, Long id, String suffix);

	Billable findLatest(String prefix, String suffix, Long startId, Long endId);

	Billable findNotFullyPaidNotInvalidCOD(LocalDate d);

	Billable findUncorrected(ModuleType t);

	Billable findUnvalidatedCorrected(ModuleType t);

	void updateDecisionData(String[] s);
}
