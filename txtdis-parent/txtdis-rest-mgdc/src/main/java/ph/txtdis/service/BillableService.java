package ph.txtdis.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import ph.txtdis.domain.BillableDetailEntity;
import ph.txtdis.domain.BillableEntity;
import ph.txtdis.domain.BomEntity;
import ph.txtdis.domain.PickListEntity;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.RemittanceDetail;
import ph.txtdis.repository.BillingRepository;

public interface BillableService
		extends DecisionDataUpdate<BillableEntity, BillingRepository>, QtyPerItemService, SpunBillableService {

	Billable findBadOrderByCustomerId(Long customerId);

	Billable findBadOrderById(Long id);

	Billable findBadOrderReceiptById(Long id);

	Billable findBillableById(Long id);

	Billable findBilledByCustomerId(Long customerId);

	Billable findBookingById(Long id);

	Billable findByBookingId(Long id);

	Billable findByCustomerPurchasedItemId(Long customerId, Long itemId);

	Billable findByItemId(Long itemId, Long customerId, Date d);

	Billable findByOrderNo(String prefix, Long id, String suffix);

	Billable findDeliveryReportById(Long id);

	BillableEntity findEntityByLoadOrSalesOrderId(Long id);

	BillableEntity findEntityBySalesOrderId(Long id);

	Billable findLatest(String prefix, String suffix, Long startId, Long endId);

	Billable findLoadOrderById(Long id);

	Billable findNotFullyPaidCOD(String seller, Date endDate);

	Billable findOpenBadOrReturnOrderByCustomerId(Long customerId);

	Billable findPurchaseOrderById(Long id);

	Billable findPurchaseReceiptById(Long id);

	Billable findReceivingReportById(Long id);

	Billable findReturnOrderByCustomerId(Long customerId);

	Billable findRmaById(Long id);

	Billable findRmaReceiptById(Long id);

	Billable findSalesOrderById(Long id);

	Billable findUnbilledPickedUpTo(String seller, Date d);

	List<Billable> findUnpaidBillables(Long customerId);

	List<Billable> findUnpickedOn(Date d);

	List<BillableDetailEntity> getDetails(PickListEntity p);

	BillableEntity getEntity(RemittanceDetail d);

	List<Billable> listAgedBillables(Long customerId);

	List<Billable> listAgingBillables(Long customerId);

	List<BomEntity> listBadItemsIncomingQty(LocalDate start, LocalDate end);

	List<BomEntity> listBadItemsOutgoingQty(LocalDate start, LocalDate end);

	List<BomEntity> listGoodItemsIncomingQty(LocalDate start, LocalDate end);

	List<BomEntity> listGoodItemsOutgoingQty(LocalDate start, LocalDate end);

	void updateDecisionData(String[] s);

	void updateItemReturnPayment(String[] s);
}