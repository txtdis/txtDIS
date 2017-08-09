package ph.txtdis.mgdc.gsm.service.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.Remittance;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.mgdc.gsm.domain.CustomerEntity;
import ph.txtdis.mgdc.gsm.domain.RemittanceDetailEntity;
import ph.txtdis.mgdc.gsm.domain.RemittanceEntity;
import ph.txtdis.mgdc.gsm.repository.RemittanceDetailRepository;
import ph.txtdis.mgdc.gsm.repository.RemittanceRepository;
import ph.txtdis.mgdc.repository.HolidayRepository;
import ph.txtdis.service.AbstractSpunSavedKeyedService;
import ph.txtdis.type.PartnerType;
import ph.txtdis.type.PaymentType;
import ph.txtdis.util.DateTimeUtils;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ZERO;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.util.Collections.emptyList;
import static ph.txtdis.type.PartnerType.OUTLET;
import static ph.txtdis.type.PaymentType.CASH;
import static ph.txtdis.type.PaymentType.CHECK;
import static ph.txtdis.util.DateTimeUtils.*;
import static ph.txtdis.util.NumberUtils.isZero;
import static ph.txtdis.util.TextUtils.blankIfNull;

public abstract class AbstractRemittanceService //
	extends AbstractSpunSavedKeyedService<RemittanceRepository, RemittanceEntity, Remittance, Long> //
	implements GsmRemittanceService {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private HolidayRepository holidayRepository;

	@Autowired
	private RemittanceDetailRepository remittanceDetailRepository;

	@Value("${vendor.dis.go.live}")
	private String edmsGoLive;

	@Value("${grace.period.cash.deposit}")
	private String gracePeriodCashDeposit;

	@Value("${grace.period.check.deposit}")
	private String gracePeriodCheckDeposit;

	@Override
	public List<Remittance> findAll(Billable b) {
		List<RemittanceEntity> l = findEntitiesByBillingId(b.getId());
		return toRemittances(l);
	}

	@Override
	public List<RemittanceEntity> findEntitiesByBillingId(Long id) {
		return repository.findByDetailsBillingId(id);
	}

	private List<Remittance> toRemittances(List<RemittanceEntity> l) {
		return l.stream().map(e -> newRemittance(e)).collect(Collectors.toList());
	}

	protected Remittance newRemittance(RemittanceEntity e) {
		Remittance r = toPaymentOnlyRemittance(e);
		r.setRemarks(e.getRemarks());
		r.setReceivedFrom(e.getCollector());
		r.setCreatedBy(e.getCreatedBy());
		r.setCreatedOn(e.getCreatedOn());
		if (e.getIsValid() != null)
			r = setAuditData(e, r);
		if (e.getDepositedOn() != null)
			r = setDepositData(e, r);
		if (e.getReceivedOn() != null)
			r = setTransferData(e, r);
		return r;
	}

	private Remittance toPaymentOnlyRemittance(RemittanceEntity e) {
		Remittance r = toIdOnlyRemittance(e);
		r.setPaymentDate(e.getPaymentDate());
		r.setValue(e.getValue());
		return setCheckData(e, r);
	}

	private Remittance setAuditData(RemittanceEntity e, Remittance r) {
		r.setIsValid(e.getIsValid());
		r.setDecidedBy(e.getDecidedBy());
		r.setDecidedOn(e.getDecidedOn());
		return r;
	}

	private Remittance setDepositData(RemittanceEntity e, Remittance r) {
		if (e.getDepositorBank() != null) {
			r.setDepositorBank(e.getDepositorBank().getName());
			r.setDepositedOn(e.getDepositedOn());
			r.setDepositor(e.getDepositor());
			r.setDepositorOn(e.getDepositorOn());
		}
		return r;
	}

	private Remittance setTransferData(RemittanceEntity e, Remittance r) {
		r.setReceivedBy(e.getReceivedBy());
		r.setReceivedOn(e.getReceivedOn());
		return r;
	}

	private Remittance toIdOnlyRemittance(RemittanceEntity e) {
		Remittance r = new Remittance();
		r.setId(e.getId());
		return r;
	}

	private Remittance setCheckData(RemittanceEntity e, Remittance r) {
		r.setCheckId(e.getCheckId());
		r.setDraweeBank(draweeBank(e));
		return r;
	}

	private String draweeBank(RemittanceEntity e) {
		CustomerEntity bank = e == null ? null : e.getDraweeBank();
		return bank == null ? null : bank.getName();
	}

	@Override
	public List<Remittance> findAll(LocalDate startDate, LocalDate endDate) throws Exception {
		List<RemittanceEntity> l = repository.findByPaymentDateBetweenOrderByPaymentDateAsc( //
			verifyDateIsOnOrAfterGoLive(startDate, edmsGoLive()), //
			validateEndDate(startDate, endDate, edmsGoLive()));
		return toModels(l);
	}

	private LocalDate edmsGoLive() {
		return DateTimeUtils.toDate(edmsGoLive);
	}

	@Override
	public List<Remittance> findAll(String collector, LocalDate start, LocalDate end) throws Exception {
		List<RemittanceEntity> l = repository.findByCollectorContainingAndPaymentDateBetween( //
			collector, //
			verifyDateIsOnOrAfterGoLive(start, edmsGoLive()), //
			validateEndDate(start, end, edmsGoLive()));
		return toModels(l);
	}

	@Override
	public List<RemittanceDetailEntity> findFullyPaidEntitiesForMaturedPostDatedChecks() {
		List<RemittanceDetailEntity> l = remittanceDetailRepository
			.findByRemittancePaymentDateLessThanEqualAndRemittanceCheckIdNotNullAndBillingFullyPaidFalseAndBillingUnpaidValue(
				//
				LocalDate.now(), ZERO);
		return l != null ? l : emptyList();
	}

	@Override
	public List<Remittance> findAllUnvalidated() {
		LocalDate goLive = toDate(this.edmsGoLive);
		List<RemittanceEntity> l = repository.findByPaymentDateGreaterThanAndDecidedOnNullOrderByIdDesc(goLive);
		l = removeAdjustingRemittances(l);
		return toRemittanceHistory(l);
	}

	private List<RemittanceEntity> removeAdjustingRemittances(List<RemittanceEntity> l) {
		return l == null ? null : l.stream() //
			.filter(r -> r.getDraweeBank() == null || !adjustingAccounts().contains(r.getDraweeBank().getName()))
			.collect(Collectors.toList());
	}

	private List<Remittance> toRemittanceHistory(List<RemittanceEntity> l) {
		return l.stream().map(e -> toRemittanceHistory(e)).collect(Collectors.toList());
	}

	protected List<String> adjustingAccounts() {
		return Arrays.asList("");
	}

	private Remittance toRemittanceHistory(RemittanceEntity e) {
		return toPaymentOnlyRemittance(e);
	}

	@Override
	public List<Remittance> findAllUnvalidatedChecksByBank(Long bankId) {
		List<RemittanceEntity> l = repository.findByDecidedOnNullAndDraweeBankId(bankId);
		return toRemittanceHistory(l);
	}

	@Override
	public Remittance findByBillingId(Long id) {
		RemittanceEntity e = findEntityByBillingId(id);
		return toModel(e);
	}

	private RemittanceEntity findEntityByBillingId(Long id) {
		RemittanceDetailEntity d = remittanceDetailRepository.findFirstByBillingIdOrderByIdDesc(id);
		return d == null ? null : d.getRemittance();
	}

	@Override
	protected Remittance toModel(RemittanceEntity e) {
		return e == null ? null : newRemittance(e);
	}

	@Override
	public Remittance findByCheck(String bank, Long checkId) {
		RemittanceEntity e = findEntityByCheck(bank, checkId);
		return toModel(e);
	}

	@Override
	public RemittanceEntity findEntityByCheck(String bank, Long checkId) {
		List<RemittanceEntity> l = repository.findByDraweeBankNameAndCheckId(bank, checkId);
		return oneValid(l);
	}

	private RemittanceEntity oneValid(List<RemittanceEntity> l) {
		try {
			return l.stream().filter(notInvalid()).findFirst().get();
		} catch (Exception e) {
			return null;
		}
	}

	private Predicate<RemittanceEntity> notInvalid() {
		return r -> r.getIsValid() == null || r.getIsValid();
	}

	@Override
	public Remittance findByCollector(String name, LocalDate d) {
		RemittanceEntity e = repository.findFirstByCollectorAndPaymentDateAndCheckIdNull(name, d);
		return e == null ? null : toIdOnlyRemittance(e);
	}

	@Override
	public Remittance findByDate(LocalDate d) {
		RemittanceEntity e = repository.findFirstByPaymentDateOrderByIdAsc(d);
		return toModel(e);
	}

	@Override
	public Remittance findByUndepositedPayments(PaymentType payType, String seller, LocalDate upToDate) {
		return findUndepositedPayment(payType, seller, upToDate);
	}

	@Override
	public Remittance findInvalidByBillingId(Long id) {
		RemittanceEntity e = findEntityByBillingId(id);
		return e == null || (e.getIsValid() != null && !e.getIsValid()) ? null : toIdOnlyRemittance(e);
	}

	@Override
	public Remittance findPending() {
		RemittanceEntity r = findUnreceivedCheck();
		if (r != null)
			return toIdOnlyRemittance(r);
		r = findUndepositedCheck();
		if (r != null)
			return toIdOnlyRemittance(r);
		r = findUndepositedCash();
		if (r != null)
			return toIdOnlyRemittance(r);
		r = findUnvalidated();
		if (r != null)
			return toIdOnlyRemittance(r);
		return null;
	}

	private RemittanceEntity findUnreceivedCheck() {
		List<RemittanceEntity> l = repository.findByDecidedOnNullAndCheckIdNotNullAndReceivedOnNull();
		return l == null ? null //
			: l.stream() //
			.filter(r -> collectedOrDatedInThePastCheck(r)) //
			.findFirst().orElse(null);
	}

	private RemittanceEntity findUndepositedCheck() {
		return repository
			.findByDecidedOnNullAndCheckIdNotNullAndReceivedOnNotNullAndDepositedOnNullAndPaymentDateLessThan(
				LocalDate.now());
	}

	private RemittanceEntity findUndepositedCash() {
		return repository.findFirstByDecidedOnNullAndCheckIdNullAndDepositedOnNullAndPaymentDateLessThan(LocalDate.now
			());
	}

	private RemittanceEntity findUnvalidated() {
		return repository.findFirstByDecidedOnNullAndDepositedOnNotNull();
	}

	private boolean collectedOrDatedInThePastCheck(RemittanceEntity r) {
		return r.getCreatedOn().toLocalDate().isBefore(LocalDate.now()) //
			|| r.getPaymentDate().isBefore(LocalDate.now());
	}

	@Override
	public Remittance findUndepositedPayment(PaymentType payment, String seller, LocalDate date) {
		List<RemittanceEntity> l = undepositedPayments(payment, date);
		if (seller.equals("all"))
			return l.isEmpty() ? null : toModel(l.get(0));
		return toModel(oneUndepositedPaymentOfACustomerOf(seller, l));
	}

	private List<RemittanceEntity> undepositedPayments(PaymentType payment, LocalDate date) {
		if (payment == CASH)
			return undepositedCashPayments(date);
		return undepositedCheckPayments(date);
	}

	private List<RemittanceEntity> undepositedCashPayments(LocalDate date) {
		return repository.findByDetailsBillingCustomerTypeAndDepositedOnNullAndCheckIdNullAndPaymentDateBetween( //
			OUTLET, edmsGoLive(), cutoff(CASH, date));
	}

	private LocalDate cutoff(PaymentType payment, LocalDate date) {
		long day = bufferDays(payment);
		while (isAWeekendOrAHoliday(date, day))
			++day;
		return date.minusDays(day);
	}

	private Long bufferDays(PaymentType payment) {
		if (payment == CASH)
			return Long.valueOf(gracePeriodCashDeposit);
		return Long.valueOf(gracePeriodCheckDeposit);
	}

	private boolean isAWeekendOrAHoliday(LocalDate date, long day) {
		LocalDate newDate = date.minusDays(day);
		return newDate.getDayOfWeek() == SATURDAY //
			|| newDate.getDayOfWeek() == SUNDAY //
			|| holidayRepository.findByDeclaredDate(newDate) != null;
	}

	private List<RemittanceEntity> undepositedCheckPayments(LocalDate date) {
		return repository.findByDetailsBillingCustomerTypeAndReceivedOnNullAndCheckIdNotNullAndPaymentDateBetween( //
			PartnerType.OUTLET, edmsGoLive(), cutoff(CHECK, date));
	}

	private RemittanceEntity oneUndepositedPaymentOfACustomerOf(String seller,
	                                                            List<RemittanceEntity>
		                                                            remittancesWithUndepostedPayment) {
		Optional<RemittanceDetailEntity> o = remittancesWithUndepostedPayment.stream()//
			.flatMap(e -> e.getDetails().stream())//
			.filter(rd -> customerOf(rd, seller)).findFirst();
		return !o.isPresent() ? null :
			remittancesWithUndepostedPayment.stream().filter(e -> e.getDetails().contains(o.get())).findFirst().get();
	}

	private boolean customerOf(RemittanceDetailEntity rd, String seller) {
		BillableEntity b = rd.getBilling();
		CustomerEntity c = b.getCustomer();
		return seller.equals(c.getSeller());
	}

	@Override
	public List<Remittance> save(List<Remittance> l) {
		return super.save(l);
	}

	@Override
	public void saveDetails(List<RemittanceDetailEntity> l) {
		remittanceDetailRepository.save(l);
	}

	@Override
	public List<RemittanceEntity> toEntities(List<Remittance> l) {
		return super.toEntities(l);
	}

	@Override
	protected RemittanceEntity toEntity(Remittance r) {
		return r.getId() == null ? newEntity(r) : update(r);
	}

	protected RemittanceEntity newEntity(Remittance r) {
		RemittanceEntity e = new RemittanceEntity();
		e.setPaymentDate(r.getPaymentDate());
		e.setValue(r.getValue());
		e.setCollector(r.getReceivedFrom());
		e.setRemarks(r.getRemarks());
		return updateCheckData(e, r);
	}

	protected RemittanceEntity update(Remittance r) {
		RemittanceEntity e = repository.findOne(r.getId());
		if (r.getIsValid() != null && e.getIsValid() == null)
			e = updateAuditData(r, e);
		if (r.getReceivedBy() != null && e.getReceivedBy() == null)
			e = updateTransferData(r, e);
		if (r.getDepositorOn() != null && e.getDepositorOn() == null)
			e = updateDepositData(r, e);
		return e;
	}

	private RemittanceEntity updateCheckData(RemittanceEntity e, Remittance r) {
		e.setCheckId(r.getCheckId());
		e.setDraweeBank(bank(r.getDraweeBank()));
		return e;
	}

	private RemittanceEntity updateAuditData(Remittance r, RemittanceEntity e) {
		boolean isValid = r.getIsValid();
		if (!isValid)
			e.setDetails(unpayBillings(e));
		e.setRemarks(r.getRemarks());
		return updateValidity(e, r, isValid, r.getDecidedBy());
	}

	private RemittanceEntity updateTransferData(Remittance r, RemittanceEntity e) {
		e.setReceivedBy(r.getReceivedBy());
		e.setReceivedOn(ZonedDateTime.now());
		return e;
	}

	private RemittanceEntity updateDepositData(Remittance r, RemittanceEntity e) {
		e.setDepositedOn(r.getDepositedOn());
		e.setDepositor(r.getDepositor());
		e.setDepositorBank(bank(r.getDepositorBank()));
		e.setDepositorOn(ZonedDateTime.now());
		return e;
	}

	private CustomerEntity bank(String name) {
		return customerService.findEntityByName(name);
	}

	private List<RemittanceDetailEntity> unpayBillings(RemittanceEntity e) {
		return e.getDetails().stream().map(d -> unpayBilling(d)).collect(Collectors.toList());
	}

	protected RemittanceEntity updateValidity(RemittanceEntity e, Remittance r, boolean isValid, String user) {
		e.setIsValid(isValid);
		e.setDecidedBy(user);
		e.setDecidedOn(ZonedDateTime.now());
		return e;
	}

	private RemittanceDetailEntity unpayBilling(RemittanceDetailEntity d) {
		d.setBilling(unpaidBilling(d));
		d.setPaymentValue(ZERO);
		return d;
	}

	private BillableEntity unpaidBilling(RemittanceDetailEntity rd) {
		BillableEntity b = rd.getBilling();
		b.setRemarks(addReasonForUnpayment(b, rd));
		b.setFullyPaid(false);
		return recomputeBillingUnpaidValue(b, rd);
	}

	private String addReasonForUnpayment(BillableEntity b, RemittanceDetailEntity rd) {
		String originalRemarks = b.getRemarks();
		String blankIfNullRemarks = blankIfNull(originalRemarks);
		String unpaidRemarks = "UNPAID DUE TO INVALID REMITTANCE No. " + rd.getRemittance().getId();

		if (blankIfNullRemarks.contains(unpaidRemarks))
			return originalRemarks;
		return unpaidRemarks + "\n" + blankIfNullRemarks;
	}

	private BillableEntity recomputeBillingUnpaidValue(BillableEntity b, RemittanceDetailEntity d) {
		if (!isZero(d.getPaymentValue()))
			b.setUnpaidValue(b.getUnpaidValue().add(d.getPaymentValue()));
		return b;
	}

	@Override
	public void updateDeposit(String... s) {
		RemittanceEntity e = getEntity(s[1]);
		if (e.getDepositorOn() != null)
			return;
		e.setDepositorBank(customerService.toBankEntity(s[2]));
		e.setDepositedOn(DateTimeUtils.toZonedDateTime(s[3]));
		e.setDepositor(s[4]);
		e.setDepositorOn(DateTimeUtils.toZonedDateTime(s[5]));
		post(e);
	}

	private RemittanceEntity getEntity(String s) {
		return repository.findOne(Long.valueOf(s));
	}

	@Override
	public void updateFundTransfer(String... s) {
		RemittanceEntity e = getEntity(s[1]);
		if (e.getReceivedOn() != null)
			return;
		e.setReceivedBy(s[2]);
		e.setReceivedOn(toZonedDateTime(s[3]));
		post(e);
	}

	@Override
	public void updatePaymentBasedOnValidation(String... s) {
		RemittanceEntity e = updateDecisionData(repository, s);
		if (e.getIsValid() != null && e.getIsValid() == false) {
			e.setDetails(unpayBillings(e));
			post(e);
		}
	}
}
