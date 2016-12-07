package ph.txtdis.service;

import static java.math.BigDecimal.ZERO;
import static java.time.LocalDate.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.dto.PartnerType.OUTLET;
import static ph.txtdis.util.DateTimeUtils.toDate;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.domain.BillableDetailEntity;
import ph.txtdis.domain.BillableEntity;
import ph.txtdis.domain.BomEntity;
import ph.txtdis.domain.ItemEntity;
import ph.txtdis.domain.PickListEntity;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.PartnerType;
import ph.txtdis.dto.Remittance;
import ph.txtdis.dto.RemittanceDetail;
import ph.txtdis.type.ScriptType;
import ph.txtdis.type.TransactionDirectionType;
import ph.txtdis.util.NumberUtils;
import ph.txtdis.util.TextUtils;

public abstract class AbstractBillableService extends AbstractSpunBillableService implements BillableService {

	private static Logger logger = getLogger(AbstractBillableService.class);

	@Autowired
	private RemittanceService remittanceService;

	@Override
	public Billable findBadOrderByCustomerId(Long id) {
		List<BillableEntity> b = repository.findByNumIdNotNullAndRmaNotNullAndRmaFalseAndCustomerId(id);
		BigDecimal v = b.stream().map(BillableEntity::getTotalValue).reduce(ZERO, BigDecimal::add);
		return toTotalValueOnlyBillable(v);
	}

	@Override
	public Billable findBadOrderById(Long id) {
		BillableEntity b = repository.findByRmaFalseAndBookingId(id);
		return toDTO(b);
	}

	@Override
	public Billable findBadOrderReceiptById(Long id) {
		BillableEntity b = repository.findByRmaFalseAndReceivingId(id);
		return toDTO(b);
	}

	@Override
	public Billable findBillableById(Long id) {
		BillableEntity b = repository.findOne(id);
		return toDTO(b);
	}

	@Override
	public Billable findBilledByCustomerId(Long id) {
		List<BillableEntity> l = repository.findByNumIdGreaterThanAndRmaNullAndCustomerIdOrderByOrderDateDesc(0L, id);
		ArrayList<BillableEntity> noGreaterThan30dayGapInvoices = new ArrayList<>();
		for (int i = 0; i < l.size(); i++) {
			if (greaterThan30DayGapBetweenInvoices(l, i))
				break;
			noGreaterThan30dayGapInvoices.add(l.get(i));
		}
		BigDecimal v = noGreaterThan30dayGapInvoices.stream().map(BillableEntity::getTotalValue).reduce(ZERO,
				BigDecimal::add);
		return toTotalValueOnlyBillable(v);
	}

	private boolean greaterThan30DayGapBetweenInvoices(List<BillableEntity> l, int i) {
		LocalDate previous = previousInvoiceDate(l, i);
		LocalDate latest = latestInvoiceDate(l, i);
		return previous.until(latest, DAYS) > 30;
	}

	private LocalDate previousInvoiceDate(List<BillableEntity> l, int i) {
		if (i != 0)
			i--;
		return getInvoiceDate(l, i);
	}

	private LocalDate getInvoiceDate(List<BillableEntity> l, int i) {
		return l.get(i).getOrderDate();
	}

	private LocalDate latestInvoiceDate(List<BillableEntity> l, int i) {
		return i == 0 ? LocalDate.now() : getInvoiceDate(l, i);
	}

	@Override
	public Billable findBookingById(Long id) {
		BillableEntity b = findEntityByLoadOrSalesOrderId(id);
		return toDTO(b);
	}

	@Override
	public Billable findByBookingId(Long id) {
		BillableEntity b = findEntityBySalesOrderId(id);
		return toOrderNoOnlyBillable(b);
	}

	@Override
	public Billable findByCustomerPurchasedItemId(Long customerId, Long itemId) {
		LocalDate start = now().minusDays(180L);
		return start.isBefore(goLive()) ? new Billable() : findByCustomerPurchasedItemId(customerId, itemId, start);
	}

	private Billable findByCustomerPurchasedItemId(Long customerId, Long itemId, LocalDate start) {
		BillableEntity e = repository.findFirstByNumIdNotNullAndRmaNullAndCustomerIdAndOrderDateBetweenAndDetails_Item_Id(
				customerId, start, now(), itemId);
		return toOrderNoOnlyBillable(e);
	}

	@Override
	public Billable findByDate(Date d) {
		BillableEntity b = repository.findFirstByBilledOnNotNullAndNumIdGreaterThanAndOrderDateOrderByIdAsc(0L,
				d.toLocalDate());
		return toDTO(b);
	}

	@Override
	public Billable findByItemId(Long itemId, Long customerId, Date d) {
		ItemEntity i = itemService.findEntity(itemId);
		BillableEntity b = repository.findByDetailsItemAndOrderDateAndCustomerIdAndRmaNull(i, d.toLocalDate(),
				customerId);
		return toDTO(b);
	}

	@Override
	public Billable findByOrderNo(String prefix, Long id, String suffix) {
		BillableEntity b = repository.findByPrefixAndSuffixAndNumId(nullIfEmpty(prefix), nullIfEmpty(suffix), id);
		return toDTO(b);
	}

	private String nullIfEmpty(String s) {
		return s.isEmpty() ? null : s;
	}

	@Override
	public Billable findDeliveryReportById(Long id) {
		BillableEntity b = repository.findByNumId(-id);
		return toDTO(b);
	}

	@Override
	public BillableEntity findEntityByLoadOrSalesOrderId(Long id) {
		return repository.findFirstByCustomerTypeInAndBookingIdAndRmaNullOrderByIdAsc(
				Arrays.asList(PartnerType.OUTLET, PartnerType.EX_TRUCK), id);
	}

	@Override
	public BillableEntity findEntityBySalesOrderId(Long id) {
		return repository.findByCustomerTypeAndBookingIdAndRmaNull(PartnerType.OUTLET, id);
	}

	@Override
	public Billable findLoadOrderById(Long id) {
		BillableEntity b = repository.findByCustomerTypeAndBookingIdAndRmaNull(PartnerType.EX_TRUCK, id);
		return toDTO(b);
	}

	@Override
	public Billable findNotFullyPaidCOD(String seller, Date endDate) {
		List<BillableEntity> l = repository
				.findByNumIdNotNullAndRmaNullAndCustomerIdNotAndFullyPaidFalseAndUnpaidValueGreaterThanAndOrderDateBetweenOrderByOrderDateAsc(
						vendorId(), BigDecimal.ZERO, goLive(), billingCutoff(endDate, seller));
		Optional<BillableEntity> o = l.stream().filter(codCustomerOf(seller)).findFirst();
		return o.isPresent() ? toOrderNoOnlyBillable(o.get()) : null;
	}

	private Predicate<BillableEntity> codCustomerOf(String seller) {
		return a -> a.getOrderDate().equals(a.getDueDate()) && a.getCustomer().getSeller().equals(seller);
	}

	@Override
	public Billable findOpenBadOrReturnOrderByCustomerId(Long id) {
		List<BillableEntity> b = repository.findByNumIdNullAndRmaNotNullAndCustomerId(id);
		return removeCancelled(b);
	}

	private Billable removeCancelled(List<BillableEntity> l) {
		try {
			BillableEntity i = l.stream().filter(b -> b.getIsValid() != null && b.getIsValid()).findFirst().get();
			logger.info("\n    PendingBad/ReturnOrder = " + i);
			return toBookingIdOnlyBillable(i);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Billable findPurchaseOrderById(Long bookingId) {
		BillableEntity b = repository.findByCustomerIdAndBookingId(vendorId(), bookingId);
		return toDTO(b);
	}

	@Override
	public Billable findPurchaseReceiptById(Long receivingId) {
		BillableEntity b = repository.findByCustomerIdAndReceivingId(vendorId(), receivingId);
		return toDTO(b);
	}

	@Override
	public Billable findReceivingReportById(Long id) {
		BillableEntity b = repository.findByCustomerIdNotAndReceivingIdAndRmaNull(vendorId(), id);
		return toDTO(b);
	}

	@Override
	public Billable findReturnOrderByCustomerId(Long customerId) {
		List<BillableEntity> b = repository.findByNumIdNotNullAndRmaNotNullAndRmaTrueAndCustomerId(customerId);
		BigDecimal v = b.stream().map(BillableEntity::getTotalValue).reduce(ZERO, BigDecimal::add);
		return toTotalValueOnlyBillable(v);
	}

	@Override
	public Billable findRmaById(Long id) {
		BillableEntity b = repository.findByRmaTrueAndBookingId(id);
		return toDTO(b);
	}

	@Override
	public Billable findRmaReceiptById(Long id) {
		BillableEntity b = repository.findByRmaTrueAndReceivingId(id);
		return toDTO(b);
	}

	@Override
	public Billable findSalesOrderById(Long id) {
		BillableEntity b = findEntityBySalesOrderId(id);
		return toDTO(b);
	}

	@Override
	public Billable findUnbilledPickedUpTo(String seller, Date d) {
		List<BillableEntity> l = repository.findByNumIdNullAndRmaNullAndCustomerTypeAndPickingNotNullAndOrderDateBetween(
				OUTLET, goLive(), billingCutoff(d, seller));
		BillableEntity e = l.stream().filter(b -> filterBySeller(b, seller)).findFirst().orElse(null);
		return toBookingIdOnlyBillable(e);
	}

	private boolean filterBySeller(BillableEntity b, String seller) {
		if (seller.equalsIgnoreCase("ALL"))
			return true;
		return seller.equals(b.getCustomer().getSeller());
	}

	@Override
	public List<Billable> findUnpickedOn(Date d) {
		List<BillableEntity> l = repository.findByOrderDateAndCustomerIdNotAndRmaNullAndPickingNull(d.toLocalDate(),
				vendorId());
		return toList(l).stream().filter(byValidity()).collect(Collectors.toList());
	}

	private Predicate<Billable> byValidity() {
		return b -> b.getIsValid() == null || b.getIsValid();
	}

	@Override
	public List<Billable> findUnpaidBillables(Long customerId) {
		List<BillableEntity> l = repository
				.findByNumIdNotNullAndFullyPaidFalseAndCustomerIdOrderByOrderDateDesc(customerId);
		return toList(l);
	}

	@Override
	public List<BillableDetailEntity> getDetails(PickListEntity p) {
		List<BillableEntity> l = p.getBillings();
		return l == null ? null
				: l.stream() //
						.filter(e -> e != null && e.getDetails() != null) //
						.flatMap(e -> e.getDetails().stream()) //
						.collect(Collectors.toList());
	}

	@Override
	public BillableEntity getEntity(RemittanceDetail d) {
		return d.getId() == null ? getEntity(d.getOrderNo()) : repository.findOne(d.getId());
	}

	private BillableEntity getEntity(String orderNo) {
		return orderNo == null ? null : getEntity(TextUtils.to3PartIdNo(orderNo));
	}

	private BillableEntity getEntity(String[] threePartIdNo) {
		return repository.findByPrefixAndSuffixAndNumId(//
				threePartIdNo[0], //
				threePartIdNo[2], //
				NumberUtils.toLong(threePartIdNo[1]));
	}

	@Override
	public Billable findLatest(String prefix, String suffix, Long start, Long end) {
		BillableEntity b = repository.findFirstByPrefixAndSuffixAndNumIdBetweenOrderByNumIdDesc(//
				nullIfEmpty(prefix), //
				nullIfEmpty(suffix), //
				start, //
				end);
		return toOrderNoOnlyBillable(b);
	}

	@Override
	public List<Billable> listAgingBillables(Long customerId) {
		List<BillableEntity> l = repository
				.findByNumIdNotNullAndFullyPaidFalseAndCustomerIdAndOrderDateGreaterThanEqual(customerId, goLive());
		return toList(l);
	}

	@Override
	public List<Billable> listAgedBillables(Long customerId) {
		List<BillableEntity> l = repository
				.findByNumIdNotNullAndFullyPaidFalseAndCustomerIdAndOrderDateGreaterThanEqualAndDueDateLessThan(customerId,
						goLive(), LocalDate.now());
		return toList(l);
	}

	@Override
	public List<BomEntity> listBadItemsIncomingQty(LocalDate start, LocalDate end) {
		List<BillableEntity> l = listWithReceiptEntities(start, end);
		if (l == null)
			return null;
		l = l.stream().filter(e -> e.getRma() != null && e.getRma() == false).collect(Collectors.toList());
		return toBomList(TransactionDirectionType.INCOMING, l);
	}

	private List<BillableEntity> listWithReceiptEntities(LocalDate start, LocalDate end) {
		return repository.findByOrderDateBetweenAndReceivedOnNotNull(start, end);
	}

	@Override
	public List<BomEntity> listBadItemsOutgoingQty(LocalDate start, LocalDate end) {
		List<BillableEntity> l = listPickedEntities(start, end);
		if (l == null)
			return null;
		l = l.stream().filter(e -> e.getRma() != null && e.getRma() == false).collect(Collectors.toList());
		return toBomList(TransactionDirectionType.OUTGOING, l);
	}

	private List<BillableEntity> listPickedEntities(LocalDate start, LocalDate end) {
		return repository.findByOrderDateBetweenAndPickingNotNull(start, end);
	}

	@Override
	public List<BomEntity> listGoodItemsIncomingQty(LocalDate start, LocalDate end) {
		List<BillableEntity> l = listWithReceiptEntities(start, end);
		if (l == null)
			return null;
		l = l.stream().filter(e -> e.getRma() == null || e.getRma() == true).collect(Collectors.toList());
		return toBomList(TransactionDirectionType.INCOMING, l);
	}

	@Override
	public List<BomEntity> listGoodItemsOutgoingQty(LocalDate start, LocalDate end) {
		List<BillableEntity> l = listPickedEntities(start, end);
		if (l == null)
			return null;
		l = l.stream().filter(e -> e.getRma() == null || e.getRma() == true).collect(Collectors.toList());
		return toBomList(TransactionDirectionType.OUTGOING, l);
	}

	@Override
	public Billable next(Long id) {
		BillableEntity b = repository.findFirstByBilledOnNotNullAndNumIdGreaterThanAndIdGreaterThanOrderByIdAsc(0L, id);
		return toDTO(b);
	}

	@Override
	public Billable previous(Long id) {
		BillableEntity b = repository.findFirstByBilledOnNotNullAndNumIdGreaterThanAndIdLessThanOrderByIdDesc(0L, id);
		return toDTO(b);
	}

	@Override
	public Billable save(Billable i) {
		BillableEntity b = toEntity(i);
		return toDTO(post(b));
	}

	private BillableEntity post(BillableEntity b) {
		return repository.save(b);
	}

	@Override
	protected Billable firstSpun() {
		BillableEntity b = repository.findFirstByBilledOnNotNullAndNumIdGreaterThanOrderByNumIdAsc(0L);
		return toDTO(b);
	}

	@Override
	protected Billable lastSpun() {
		BillableEntity b = repository.findFirstByBilledOnNotNullAndNumIdGreaterThanOrderByNumIdDesc(0L);
		return toDTO(b);
	}

	@Override
	public void updateDecisionData(String[] s) {
		BillableEntity b = updateDecisionData(repository, s);
		if (billingIsInvalid(b))
			nullifyBilledAndPaymentData(s, b);
	}

	private boolean billingIsInvalid(BillableEntity b) {
		return b.getIsValid() != null && !b.getIsValid();
	}

	private void nullifyBilledAndPaymentData(String[] s, BillableEntity b) {
		b.setBilledBy(null);
		b.setBilledOn(null);
		b = repository.save(b);
		List<Remittance> l = remittanceService.listRemittanceByBilling(toDTO(b));
		if (l != null)
			unpayBillings(s.clone(), l);
	}

	private void unpayBillings(String[] s, List<Remittance> l) {
		s[0] = ScriptType.PAYMENT_VALIDATION.toString();
		for (Remittance r : l) {
			s[1] = r.getId().toString();
			remittanceService.updatePaymentValidation(s);
		}
	}

	@Override
	public void updateItemReturnPayment(String[] s) {
		BillableEntity b = repository.findOne(Long.valueOf(s[1]));
		b.setOrderDate(toDate(s[2]));
		b.setPrefix(s[3]);
		b.setNumId(Long.valueOf(s[4]));
		b.setSuffix(s[5]);
		b.setBilledBy(s[6]);
		b.setBilledOn(ZonedDateTime.parse(s[7]));
		b.setUnpaidValue(ZERO);
		b.setFullyPaid(true);
		repository.save(b);
	}

	protected void post(List<Billable> l) {
		l.forEach(p -> post(p));
	}

	private BillableEntity post(Billable p) {
		logger.info("\n    BillableToPost: " + p.getOrderNo() + " - " + p.getCustomerName() + ", "
				+ NumberUtils.toCurrencyText(p.getTotalValue()));
		return repository.save(toEntity(p));
	}
}