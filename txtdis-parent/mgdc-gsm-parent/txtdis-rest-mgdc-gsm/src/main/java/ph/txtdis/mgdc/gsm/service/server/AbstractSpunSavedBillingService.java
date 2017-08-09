package ph.txtdis.mgdc.gsm.service.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ph.txtdis.dto.Billable;
import ph.txtdis.mgdc.gsm.domain.*;
import ph.txtdis.mgdc.gsm.repository.RemittanceDetailRepository;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;
import static java.math.BigDecimal.ZERO;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static ph.txtdis.type.PartnerType.EX_TRUCK;
import static ph.txtdis.util.DateTimeUtils.toDateDisplay;
import static ph.txtdis.util.DateTimeUtils.toTimestampText;
import static ph.txtdis.util.NumberUtils.*;
import static ph.txtdis.util.TextUtils.blankIfNull;

public abstract class AbstractSpunSavedBillingService //
	extends AbstractSpunSavedBillableService //
	implements BillingDataService {

	@Autowired
	private AllBillingService allBillingService;

	@Autowired
	private GsmRemittanceService remittanceService;

	@Autowired
	private RemittanceDetailRepository remittanceDetailRepository;

	@Override
	public Billable save(Billable t) {
		BillableEntity e = toEntity(t);
		List<BillableEntity> l = new ArrayList<>(asList(e));
		if (t.getEmployeeId() != null)
			l.add(createDRforAdjustment(e, t));
		return toModel(post(l).iterator().next());
	}

	private BillableEntity createDRforAdjustment(BillableEntity orig, Billable b) {
		BillableEntity e = new BillableEntity();
		e.setNumId(deliveryId());
		e.setOrderDate(b.getOrderDate());
		e.setDueDate(e.getOrderDate());
		e.setCustomer(employee(b));
		e.setGrossValue(grossValue(b));
		e.setTotalValue(e.getGrossValue());
		e.setUnpaidValue(unpaidValue(e));
		e.setFullyPaid(fullyPaid(e));
		e.setBookingId(negateBookingId(orig));
		e.setRemarks("ADJUSTMENT FOR S/I(D/R) No. " + orig.getOrderNo());
		e.setPicking(orig.getPicking());
		e.setBilledBy(b.getBilledBy());
		e.setBilledOn(ZonedDateTime.now());
		return e;
	}

	@Override
	public Billable toModel(BillableEntity e) {
		Billable b = super.toModel(e);
		if (b != null)
			b.setPayments(listPayments(e));
		return b;
	}

	@Override
	@Transactional
	public List<BillableEntity> post(List<BillableEntity> entities) {
		BillableEntity e = entities.get(0);
		if (e.getIsValid() == null || e.getIsValid() == true)
			return super.post(entities);
		return invalidateRemittancesAndBillingsRecreatingSalesOrderCopies(entities, e);
	}

	protected Long deliveryId() {
		BillableEntity e = repository.findFirstByNumIdLessThanOrderByNumIdAsc(0L);
		return e.getNumId() - 1;
	}

	private CustomerEntity employee(Billable b) {
		return customerService.findEntityByPrimaryKey(b.getEmployeeId());
	}

	private BigDecimal grossValue(Billable b) {
		return b.getAdjustmentValue().negate();
	}

	private BigDecimal unpaidValue(BillableEntity e) {
		BigDecimal total = e.getTotalValue();
		return isNegative(total) ? ZERO : total;
	}

	private boolean fullyPaid(BillableEntity e) {
		return !isPositive(e.getUnpaidValue());
	}

	private List<String> listPayments(BillableEntity e) {
		try {
			return listPayments(listRemittanceDetails(e));
		} catch (Exception ex) {
			return emptyList();
		}
	}

	private List<BillableEntity> invalidateRemittancesAndBillingsRecreatingSalesOrderCopies(List<BillableEntity>
		                                                                                        entities,
	                                                                                        BillableEntity e) {
		invalidateRemittancesUsedToPayTheInvalidBillingTherebyUnpayingAffectedBillings(e);
		if (isReferenceALoadOrder(e))
			return super.post(entities);
		return addNewUnbilledUnvalidatedPickedBookingCopy(entities, e);
	}

	private List<String> listPayments(List<RemittanceDetailEntity> e) throws Exception {
		ArrayList<String> list = new ArrayList<>();
		if (e.size() > 1)
			list.add(getTotalInText(sumPayments(e)));
		return getRemitIdAndDateAndPaymentTextList(e, list);
	}

	private List<RemittanceDetailEntity> listRemittanceDetails(BillableEntity e) {
		return remittanceDetailRepository.findByBilling(e);
	}

	private void invalidateRemittancesUsedToPayTheInvalidBillingTherebyUnpayingAffectedBillings(BillableEntity e) {
		List<RemittanceEntity> l = remittanceService.findEntitiesByBillingId(e.getId());
		if (l != null)
			for (RemittanceEntity r : l)
				remittanceService.updatePaymentBasedOnValidation( //
					"", //
					r.getId().toString(), //
					"false", //
					"INVALID S/I(D/R) No. " + e.getOrderNo(), //
					e.getDecidedBy(), //
					toTimestampText(e.getDecidedOn()));
	}

	private boolean isReferenceALoadOrder(BillableEntity e) {
		BillableEntity b = repository.findFirstByBookingId(abs(e.getBookingId()));
		return b == null ? false : b.getCustomer().getName().startsWith(EX_TRUCK.toString());
	}

	private List<BillableEntity> addNewUnbilledUnvalidatedPickedBookingCopy(List<BillableEntity> entities,
	                                                                        BillableEntity e) {
		List<BillableEntity> l = new ArrayList<>(entities);
		l.add(newUnbilledUnvalidatedPickedBookingCopy(e));
		return super.post(l);
	}

	private BigDecimal sumPayments(List<RemittanceDetailEntity> payments) {
		try {
			return payments.stream().filter(d -> isValid(d)).map(d -> d.getPaymentValue())
				.reduce(ZERO, (a, b) -> a.add(b));
		} catch (Exception e) {
			return ZERO;
		}
	}

	private List<String> getRemitIdAndDateAndPaymentTextList(List<RemittanceDetailEntity> r, List<String> list) {
		r.forEach(p -> list.add(createRemitIdAndDateAndPaymentText(p)));
		return list;
	}

	private BillableEntity newUnbilledUnvalidatedPickedBookingCopy(BillableEntity b) {
		BillableEntity e = new BillableEntity();
		e.setBookingId(absoluteBookingId(b));
		e.setOrderDate(b.getOrderDate());
		e.setDueDate(b.getDueDate());
		e.setCustomer(b.getCustomer());
		e.setGrossValue(b.getGrossValue());
		e.setTotalValue(b.getTotalValue());
		e.setUnpaidValue(b.getTotalValue());
		e.setPicking(b.getPicking());
		e.setRemarks(EXTRACTED_FROM_INVALIDATED_S_I_D_R_NO + b.getOrderNo());
		e.setDetails(copyDetails(e, b));
		return e;
	}

	private Boolean isValid(RemittanceDetailEntity d) {
		try {
			return d.getRemittance().getIsValid();
		} catch (Exception e) {
			return false;
		}
	}

	private String createRemitIdAndDateAndPaymentText(RemittanceDetailEntity p) {
		RemittanceEntity r = p.getRemittance();
		return "[" + status(r) + ": " + r + " - " + toDateDisplay(r.getPaymentDate()) + "] " +
			toCurrencyText(p.getPaymentValue());
	}

	private Long absoluteBookingId(BillableEntity b) {
		Long id = b.getBookingId();
		return id == null ? null : Math.abs(id);
	}

	private List<BillableDetailEntity> copyDetails(BillableEntity copy, BillableEntity orig) {
		List<BillableDetailEntity> l = orig.getDetails();
		return l == null ? null : l.stream().map(d -> copyDetail(copy, d)).collect(toList());
	}

	private String status(RemittanceEntity r) {
		if (r.getIsValid() == null)
			return "UNVALIDATED";
		return r.getIsValid() == true ? "VALID" : "INVALID";
	}

	private BillableDetailEntity copyDetail(BillableEntity copy, BillableDetailEntity d) {
		BillableDetailEntity e = new BillableDetailEntity();
		e.setBilling(copy);
		e.setItem(d.getItem());
		e.setInitialQty(d.getInitialQty());
		e.setPriceValue(d.getPriceValue());
		e.setQuality(d.getQuality());
		e.setUom(d.getUom());
		return e;
	}

	@Override
	protected BillableEntity update(BillableEntity e, Billable b) {
		super.update(e, b);
		return noDecisionsNeedsToBeChanged(e, b) ? e : updateDecisionData(e, b);
	}

	private boolean noDecisionsNeedsToBeChanged(BillableEntity e, Billable b) {
		return b.getIsValid() == null || (b.getIsValid() == true && e.getIsValid() != null);
	}

	protected BillableEntity updateDecisionData(BillableEntity e, Billable b) {
		e.setIsValid(b.getIsValid());
		e.setDecidedBy(b.getDecidedBy());
		e.setDecidedOn(ZonedDateTime.now());
		return b.getIsValid() ? e : negateBookingIdAndAppendSuffixWithXs(e, b);
	}

	private BillableEntity negateBookingIdAndAppendSuffixWithXs(BillableEntity e, Billable b) {
		e.setBookingId(negateBookingId(b));
		return appendSuffixWithXs(e);
	}

	private BillableEntity appendSuffixWithXs(BillableEntity e) {
		String suffix = blankIfNull(e.getSuffix()) + "X";
		BillableEntity b = allBillingService.findEntityByOrderNo(e.getPrefix(), e.getNumId(), suffix);
		e.setSuffix(suffix);
		return b == null ? e : appendSuffixWithXs(e);
	}
}