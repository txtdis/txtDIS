package ph.txtdis.mgdc.gsm.service.server;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.Remittance;
import ph.txtdis.dto.RemittanceDetail;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.mgdc.gsm.domain.CustomerEntity;
import ph.txtdis.type.ModuleType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static java.time.LocalDate.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.removeEnd;
import static ph.txtdis.type.ModuleType.INVOICE;
import static ph.txtdis.type.ModuleType.SALES_ORDER;
import static ph.txtdis.type.PartnerType.*;
import static ph.txtdis.type.ScriptType.PAYMENT_VALIDATION;
import static ph.txtdis.util.NumberUtils.nullIfZero;
import static ph.txtdis.util.NumberUtils.toLong;
import static ph.txtdis.util.TextUtils.nullIfEmpty;
import static ph.txtdis.util.TextUtils.to3PartIdNo;

@Service("billingService")
public class AllBillingServiceImpl //
	extends AbstractSpunSavedBillableService //
	implements AllBillingService {

	@Autowired
	private LoadOrderService loadOrderService;

	@Autowired
	private GsmRemittanceService remittanceService;

	@Override
	public List<Billable> findAllAged(Long customerId) {
		List<BillableEntity> l = repository
			.findByNumIdNotNullAndRmaNullAndFullyPaidFalseAndCustomerIdAndOrderDateGreaterThanEqualAndDueDateLessThan( ///
				customerId, goLive(), LocalDate.now());
		return toModels(filterOutInvalid(l));
	}

	private List<BillableEntity> filterOutInvalid(List<BillableEntity> l) {
		return l == null ? null //
			: l.stream().filter(e -> isNotInvalid(e)).collect(toList());
	}

	private boolean isNotInvalid(BillableEntity e) {
		return e.getIsValid() == null || e.getIsValid();
	}

	@Override
	public List<Billable> findAllAging(Long customerId) {
		List<BillableEntity> l =
			repository.findByNumIdNotNullAndRmaNullAndFullyPaidFalseAndCustomerIdAndOrderDateGreaterThanEqual( //
				customerId, goLive());
		return toModels(filterOutInvalid(l));
	}

	@Override
	public List<BillableEntity> findAllValidOutletBillingsBetweenDates(LocalDate start, LocalDate end) {
		List<BillableEntity> l = repository.findByCustomerTypeAndNumIdNotNullAndRmaNullAndOrderDateBetween( //
			OUTLET, start, end);
		return filterOutInvalid(l);
	}

	@Override
	public List<Billable> findAllUnpaid(Long customerId) {
		List<BillableEntity> l =
			repository.findByNumIdNotNullAndRmaNullAndFullyPaidFalseAndCustomerIdOrderByOrderDateDesc(customerId);
		return toModels(filterOutInvalid(l));
	}

	@Override
	public Billable findByCustomerId(Long id) {
		List<BillableEntity> l = repository.findByNumIdGreaterThanAndRmaNullAndCustomerIdOrderByOrderDateDesc(0L, id);
		ArrayList<BillableEntity> noGreaterThan30dayGapInvoices = new ArrayList<>();
		for (int i = 0; i < l.size(); i++)
			if (!greaterThan30DayGapBetweenInvoices(l, i))
				noGreaterThan30dayGapInvoices.add(l.get(i));
		BigDecimal v =
			noGreaterThan30dayGapInvoices.stream().map(BillableEntity::getTotalValue).reduce(ZERO, BigDecimal::add);
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

	private LocalDate latestInvoiceDate(List<BillableEntity> l, int i) {
		return i == 0 ? LocalDate.now() : getInvoiceDate(l, i);
	}

	private LocalDate getInvoiceDate(List<BillableEntity> l, int i) {
		return l.get(i).getOrderDate();
	}

	@Override
	public Billable findByCustomerPurchasedItemId(Long customerId, Long itemId) {
		LocalDate start = now().minusDays(180L);
		return start.isBefore(goLive()) ? new Billable() : findByCustomerPurchasedItemId(customerId, itemId, start);
	}

	private Billable findByCustomerPurchasedItemId(Long customerId, Long itemId, LocalDate start) {
		BillableEntity e = repository
			.findFirstByNumIdNotNullAndRmaNullAndCustomerIdAndOrderDateBetweenAndDetails_Item_IdOrderByOrderDateDesc( //
				customerId, start, now(), itemId);
		return toModel(e);
	}

	@Override
	public Billable findByOrderNo(String prefix, Long id, String suffix) {
		BillableEntity b = findEntityByOrderNo(prefix, id, suffix);
		return toModel(b);
	}

	@Override
	public BillableEntity findEntityByOrderNo(String prefix, Long id, String suffix) {
		return repository.findByPrefixAndSuffixAndNumId(nullIfEmpty(prefix), nullIfEmpty(suffix), nullIfZero(id));
	}

	@Override
	public BillableEntity findEntity(RemittanceDetail d) {
		return d.getId() == null ? getEntity(d.getOrderNo()) : repository.findOne(d.getId());
	}

	private BillableEntity getEntity(String orderNo) {
		return orderNo == null ? null : getEntity(to3PartIdNo(orderNo));
	}

	private BillableEntity getEntity(String[] threePartIdNo) {
		return repository.findByPrefixAndSuffixAndNumId(//
			threePartIdNo[0], threePartIdNo[2], toLong(threePartIdNo[1]));
	}

	@Override
	public Billable findLatest(String prefix, String suffix, Long start, Long end) {
		BillableEntity b = repository.findFirstByPrefixAndSuffixAndNumIdBetweenOrderByNumIdDesc(//
			nullIfEmpty(prefix), nullIfEmpty(suffix), start, end);
		return toOrderNoOnlyBillable(b);
	}

	@Override
	public Billable findNotFullyPaidNotInvalidCOD(LocalDate d) {
		List<BillableEntity> l = repository
			.findByCustomerTypeNotAndNumIdNotNullAndRmaNullAndFullyPaidFalseAndUnpaidValueGreaterThanAndOrderDateBetween(//
				VENDOR, ZERO, goLive(), billingCutoff(d));
		return toOrderNoOnlyBillable(l == null ? null : //
			l.stream().filter(b -> isNotInvalidAndIsCOD(b)).findFirst().orElse(null));
	}

	private boolean isNotInvalidAndIsCOD(BillableEntity e) {
		return isNotInvalid(e) && isCOD(e);
	}

	private boolean isCOD(BillableEntity e) {
		return e.getOrderDate().equals(e.getDueDate());
	}

	@Override
	public Billable findUncorrected(ModuleType t) {
		List<BillableEntity> l = findAllInvalidatedBeforeToday(t);
		if (l != null)
			for (BillableEntity e : l)
				if (findCorrectedBilling(e) == null)
					return findExtractedBooking(e);
		return null;
	}

	private List<BillableEntity> findAllInvalidatedBeforeToday(ModuleType t) {
		ZonedDateTime decidedOn = ZonedDateTime.now().minusDays(1L);
		if (t == SALES_ORDER)
			return repository.findByIsValidFalseAndDecidedOnLessThan(decidedOn);
		if (t == INVOICE)
			return repository.findByIsValidFalseAndNumIdGreaterThanAndDecidedOnLessThan(0L, decidedOn);
		return repository.findByIsValidFalseAndNumIdLessThanAndDecidedOnLessThan(0L, decidedOn);
	}

	private BillableEntity findCorrectedBilling(BillableEntity e) {
		return findEntityByOrderNo( //
			e.getPrefix(), //
			e.getNumId(), //
			suffixEndingWithAnXRemoved(e));
	}

	private Billable findExtractedBooking(BillableEntity e) {
		Billable b = findByBookingId(-e.getBookingId());
		b.setLoaded(b.getCustomerName().startsWith(EX_TRUCK.toString()));
		return setDueDateAndCustomerDataAndOrderNoWithoutXs(b, e);
	}

	private String suffixEndingWithAnXRemoved(BillableEntity e) {
		String s = e.getSuffix();
		s = removeEnd(s, "X");
		return nullIfEmpty(s);
	}

	@Override
	public Billable findByBookingId(Long id) {
		return loadOrderService.findAsReference(id);
	}

	protected Billable setDueDateAndCustomerDataAndOrderNoWithoutXs(Billable b, BillableEntity e) {
		b.setId(null);
		b.setPrefix(e.getPrefix());
		b.setNumId(e.getNumId());
		b.setSuffix(suffixWithoutXs(e));
		b.setDueDate(e.getDueDate());
		return setCustomerData(b, e);
	}

	private String suffixWithoutXs(BillableEntity e) {
		String s = StringUtils.remove(e.getSuffix(), "X");
		return nullIfEmpty(s);
	}

	private Billable setCustomerData(Billable b, BillableEntity e) {
		CustomerEntity c = e.getCustomer();
		if (c == null)
			return b;
		b.setCustomerId(c.getId());
		b.setCustomerName(c.getName());
		b.setCustomerAddress(c.getAddress());
		return b;
	}

	@Override
	public Billable findUnvalidatedCorrected(ModuleType t) {
		List<BillableEntity> l = findAllInvalidatedBeforeToday(t);
		if (l != null)
			for (BillableEntity e : l) {
				e = findCorrectedBilling(e);
				if (e != null && e.getIsValid() == null)
					return toModel(e);
			}
		return null;
	}

	@Override
	public void updateDecisionData(String[] s) {
		BillableEntity b = updateDecisionData(repository, s);
		if (isInvalid(b))
			nullifyBilledAndPaymentData(s, b);
	}

	private boolean isInvalid(BillableEntity b) {
		return b.getIsValid() != null && !b.getIsValid();
	}

	private void nullifyBilledAndPaymentData(String[] s, BillableEntity b) {
		b.setBilledBy(null);
		b.setBilledOn(null);
		b = repository.save(b);
		List<Remittance> l = remittanceService.findAll(toModel(b));
		if (l != null)
			unpayBillings(s.clone(), l);
	}

	private void unpayBillings(String[] s, List<Remittance> l) {
		s[0] = PAYMENT_VALIDATION.toString();
		for (Remittance r : l) {
			s[1] = r.getId().toString();
			remittanceService.updatePaymentBasedOnValidation(s);
		}
	}
}
