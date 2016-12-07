package ph.txtdis.service;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.type.PaymentType.CASH;
import static ph.txtdis.type.PaymentType.CHECK;
import static ph.txtdis.util.DateTimeUtils.toDate;
import static ph.txtdis.util.NumberUtils.isNegative;
import static ph.txtdis.util.NumberUtils.isZero;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import ph.txtdis.domain.BillableEntity;
import ph.txtdis.domain.CustomerEntity;
import ph.txtdis.domain.RemittanceDetailEntity;
import ph.txtdis.domain.RemittanceEntity;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.Remittance;
import ph.txtdis.dto.RemittanceDetail;
import ph.txtdis.exception.DateBeforeGoLiveException;
import ph.txtdis.exception.EndDateBeforeStartException;
import ph.txtdis.repository.CustomerRepository;
import ph.txtdis.repository.HolidayRepository;
import ph.txtdis.repository.RemittanceDetailRepository;
import ph.txtdis.repository.RemittanceRepository;
import ph.txtdis.type.PaymentType;
import ph.txtdis.util.DateTimeUtils;
import ph.txtdis.util.NumberUtils;

public abstract class AbstractRemittanceService
		extends AbstractIdService<RemittanceRepository, RemittanceEntity, Remittance, Long> implements RemittanceService {

	private static Logger logger = getLogger(AbstractRemittanceService.class);

	@Autowired
	private CustomerService customerService;

	@Autowired
	private HolidayRepository holidayRepository;

	@Autowired
	private BillableService billableService;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private RemittanceDetailRepository remittanceDetailRepository;

	@Value("${go.live}")
	private String goLive;

	@Value("${grace.period.cash.deposit}")
	private String gracePeriodCashDeposit;

	@Value("${grace.period.check.deposit}")
	private String gracePeriodCheckDeposit;

	@Override
	public Remittance findById(Long id) {
		RemittanceEntity e = repository.findOne(id);
		return toDTO(e);
	}

	@Override
	public Remittance findByCheck(Long bank, Long id) {
		List<RemittanceEntity> l = repository.findByDraweeBankIdAndCheckId(bank, id);
		RemittanceEntity e = filterValidity(l);
		return toDTO(e);
	}

	@Override
	public Remittance findByCollector(String name, LocalDate d) {
		RemittanceEntity e = repository.findFirstByCollectorAndPaymentDateAndCheckIdNull(name, d);
		return e == null ? null : toIdOnlyRemittance(e);
	}

	@Override
	public Remittance findByDate(LocalDate d) {
		RemittanceEntity e = repository.findFirstByPaymentDateOrderByIdAsc(d);
		return toDTO(e);
	}

	@Override
	public Remittance findByUndepositedPayments(PaymentType payType, String seller, LocalDate upToDate) {
		return findOneUndepositedPayment(payType, seller, upToDate);
	}

	@Override
	public Remittance findIfCurrentlyInvalidated(Long id) {
		return remittanceDetailRepository.findFirstByBillingIdOrderByIdDesc(id);
	}

	@Override
	public Remittance first() {
		RemittanceEntity e = repository.findFirstByOrderByIdAsc();
		return toDTO(e);
	}

	@Override
	public Remittance firstToSpin() {
		RemittanceEntity e = repository.findFirstByOrderByIdAsc();
		return toIdOnlyRemittance(e);
	}

	@Override
	public Remittance last() {
		RemittanceEntity e = repository.findFirstByOrderByIdDesc();
		return toDTO(e);
	}

	@Override
	public Remittance lastToSpin() {
		RemittanceEntity e = repository.findFirstByOrderByIdDesc();
		return toIdOnlyRemittance(e);
	}

	@Override
	public List<Remittance> list() {
		LocalDate goLive = toDate(this.goLive);
		List<RemittanceEntity> l = repository
				.findByPaymentDateGreaterThanAndDepositedOnNullOrDecidedOnNullOrderByIdDesc(goLive);
		return l.stream().map(e -> toRemittanceHistory(e)).collect(Collectors.toList());
	}

	@Override
	public List<Remittance> list(LocalDate startDate, LocalDate endDate)
			throws DateBeforeGoLiveException, EndDateBeforeStartException {
		List<RemittanceEntity> l = repository.findByPaymentDateBetweenOrderByPaymentDateAsc( //
				DateTimeUtils.verifyDateIsOnOrAfterGoLive(startDate, goLive()), //
				DateTimeUtils.validateEndDate(startDate, endDate, goLive()));
		return toList(l);
	}

	@Override
	public List<Remittance> listRemittanceByBilling(Billable b) {
		List<RemittanceEntity> e = repository.findByDetailsBillingId(b.getId());
		return toRemittances(e);
	}

	@Override
	public Remittance next(Long id) {
		RemittanceEntity e = repository.findFirstByIdGreaterThanOrderByIdAsc(id);
		return toDTO(e);
	}

	@Override
	public Remittance previous(Long id) {
		RemittanceEntity e = repository.findFirstByIdLessThanOrderByIdDesc(id);
		return toDTO(e);
	}

	@Override
	public Remittance save(Remittance r) {
		RemittanceEntity e = toEntity(r);
		e = repository.save(e);
		return toDTO(e);
	}

	private RemittanceEntity filterValidity(List<RemittanceEntity> l) {
		try {
			return l.stream().filter(r -> r.getIsValid() == null || r.getIsValid()).findFirst().get();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Remittance findOneUndepositedPayment(PaymentType payment, String seller, LocalDate date) {
		List<RemittanceEntity> l = listUndepositedPayments(payment, date);
		if (seller.equals("all"))
			return l.isEmpty() ? null : toDTO(l.get(0));
		return toDTO(getOneUndepositedPaymentOfACustomerOf(seller, l));
	}

	@Override
	public void updateDeposit(String[] s) {
		RemittanceEntity e = getEntity(s[1]);
		if (e.getDepositorOn() != null)
			return;
		e.setDepositorBank(customerService.toBankEntity(s[2]));
		e.setDepositedOn(DateTimeUtils.toZonedDateTime(s[3]));
		e.setDepositor(s[4]);
		e.setDepositorOn(DateTimeUtils.toZonedDateTime(s[5]));
		save(e);
	}

	@Override
	public void updateFundTransfer(String[] s) {
		RemittanceEntity e = getEntity(s[1]);
		if (e.getReceivedOn() != null)
			return;
		e.setReceivedBy(s[2]);
		e.setReceivedOn(DateTimeUtils.toZonedDateTime(s[3]));
		save(e);
	}

	@Override
	public void updatePaymentValidation(String[] s) {
		RemittanceEntity e = updateDecisionData(repository, s);
		if (e.getIsValid() != null && !e.getIsValid()) {
			e.setDetails(unpayBillings(e));
			save(e);
		}
	}

	private Long bufferDays(PaymentType payment) {
		if (payment == CASH)
			return Long.valueOf(gracePeriodCashDeposit);
		return Long.valueOf(gracePeriodCheckDeposit);
	}

	private boolean customerOf(RemittanceDetailEntity rd, String seller) {
		BillableEntity b = rd.getBilling();
		CustomerEntity c = b.getCustomer();
		return seller.equals(c.getSeller());
	}

	private LocalDate cutoff(PaymentType payment, LocalDate date) {
		long day = bufferDays(payment);
		while (isAWeekendOrAHoliday(date, day))
			++day;
		return date.minusDays(day);
	}

	private RemittanceEntity getOneUndepositedPaymentOfACustomerOf(String seller,
			List<RemittanceEntity> remittancesWithUndepostedPayment) {
		Optional<RemittanceDetailEntity> o = remittancesWithUndepostedPayment.stream()//
				.flatMap(e -> e.getDetails().stream())//
				.filter(rd -> customerOf(rd, seller)).findFirst();
		return !o.isPresent() ? null
				: remittancesWithUndepostedPayment.stream().filter(e -> e.getDetails().contains(o.get())).findFirst().get();
	}

	private RemittanceEntity getEntity(String s) {
		return repository.findOne(Long.valueOf(s));
	}

	private LocalDate goLive() {
		return LocalDate.parse(goLive);
	}

	private boolean isAWeekendOrAHoliday(LocalDate date, long day) {
		LocalDate newDate = date.minusDays(day);
		return newDate.getDayOfWeek() == SATURDAY //
				|| newDate.getDayOfWeek() == SUNDAY //
				|| holidayRepository.findByDeclaredDate(newDate) != null;
	}

	private List<RemittanceEntity> listUndepositedCashPayments(LocalDate date) {
		return repository.findByDetailsBillingCustomerIdNotAndDepositedOnNullAndCheckIdNullAndPaymentDateBetween(
				customerService.getVendor().getId(), goLive(), cutoff(CASH, date));
	}

	private List<RemittanceEntity> listUndepositedCheckPayments(LocalDate date) {
		return repository.findByDetailsBillingCustomerIdNotAndReceivedOnNullAndCheckIdNotNullAndPaymentDateBetween(
				customerService.getVendor().getId(), goLive(), cutoff(CHECK, date));
	}

	private List<RemittanceEntity> listUndepositedPayments(PaymentType payment, LocalDate date) {
		if (payment == CASH)
			return listUndepositedCashPayments(date);
		return listUndepositedCheckPayments(date);
	}

	private void save(RemittanceEntity e) {
		repository.save(e);
	}

	private Remittance toRemittanceHistory(RemittanceEntity e) {
		Remittance r = toPaymentOnlyRemittance(e);
		r.setDepositedOn(e.getDepositedOn());
		r.setIsValid(e.getIsValid());
		return r;
	}

	@Override
	protected Remittance toDTO(RemittanceEntity e) {
		return e == null ? null : newRemittance(e);
	}

	private List<Remittance> toRemittances(List<RemittanceEntity> l) {
		return l.stream().map(e -> newRemittance(e)).collect(Collectors.toList());
	}

	private Remittance newRemittance(RemittanceEntity e) {
		Remittance r = toPaymentOnlyRemittance(e);
		r.setRemarks(e.getRemarks());
		r.setCollector(e.getCollector());
		r.setCreatedBy(e.getCreatedBy());
		r.setCreatedOn(e.getCreatedOn());
		r.setDetails(getDetails(e));
		if (e.getIsValid() != null)
			setAuditData(e, r);
		if (e.getDepositedOn() != null)
			setDepositData(e, r);
		if (e.getReceivedOn() != null)
			setTransferData(e, r);
		if (e.getCheckId() != null)
			setCheckData(e, r);
		return r;
	}

	private Remittance toPaymentOnlyRemittance(RemittanceEntity e) {
		Remittance r = toIdOnlyRemittance(e);
		r.setValue(e.getValue());
		r.setPaymentDate(e.getPaymentDate());
		return r;
	}

	private Remittance toIdOnlyRemittance(RemittanceEntity e) {
		Remittance r = new Remittance();
		r.setId(e.getId());
		return r;
	}

	private List<RemittanceDetail> getDetails(RemittanceEntity e) {
		return e.getDetails().stream().map(d -> getDetail(d)).collect(Collectors.toList());
	}

	private RemittanceDetail getDetail(RemittanceDetailEntity e) {
		BillableEntity b = e.getBilling();
		return b == null ? null : newDetail(e, b);
	}

	private RemittanceDetail newDetail(RemittanceDetailEntity e, BillableEntity b) {
		RemittanceDetail r = new RemittanceDetail();
		r.setId(b.getId());
		r.setOrderNo(b.getOrderNo());
		r.setCustomerName(b.getCustomer().getName());
		r.setDueDate(b.getDueDate());
		r.setTotalDueValue(b.getTotalValue());
		r.setPaymentValue(e.getPaymentValue());
		return r;
	}

	private CustomerEntity draweeBank(RemittanceEntity e) {
		CustomerEntity c = new CustomerEntity();
		c.setId(e.getDraweeBank().getId());
		c.setName(e.getDraweeBank().getName());
		return c;
	}

	private void setAuditData(RemittanceEntity e, Remittance r) {
		r.setIsValid(e.getIsValid());
		r.setDecidedBy(e.getDecidedBy());
		r.setDecidedOn(e.getDecidedOn());
	}

	private void setCheckData(RemittanceEntity e, Remittance r) {
		r.setCheckId(e.getCheckId());
		r.setDraweeBank(draweeBank(e).getName());
	}

	private void setDepositData(RemittanceEntity e, Remittance r) {
		r.setDepositorBank(e.getDepositorBank().getName());
		r.setDepositedOn(e.getDepositedOn());
		r.setDepositor(e.getDepositor());
		r.setDepositorOn(e.getDepositorOn());
	}

	private void setTransferData(RemittanceEntity e, Remittance r) {
		r.setReceivedBy(e.getReceivedBy());
		r.setReceivedOn(e.getReceivedOn());
	}

	@Override
	public List<RemittanceEntity> toEntities(List<Remittance> l) {
		return super.toEntities(l);
	}

	public List<RemittanceDetailEntity> unpayBillings(RemittanceEntity e) {
		return e.getDetails().stream().map(d -> unpayBilling(d)).collect(Collectors.toList());
	}

	@Override
	protected RemittanceEntity toEntity(Remittance r) {
		logger.info("\n    Remittance: " + r.getId() + " - " + r.getDetails());
		return r.getId() == null ? newEntity(r) : update(r);
	}

	private RemittanceEntity newEntity(Remittance r) {
		RemittanceEntity e = new RemittanceEntity();
		e.setPaymentDate(r.getPaymentDate());
		e.setValue(r.getValue());
		e.setCollector(r.getCollector());
		e.setRemarks(r.getRemarks());
		updateCheckData(r, e);
		e.setDetails(getDetails(r, e));
		return e;
	}

	private List<RemittanceDetailEntity> getDetails(Remittance r, RemittanceEntity e) {
		return r.getDetails().stream().map(d -> getDetail(d, r, e)).filter(d -> d.getBilling() != null)
				.collect(Collectors.toList());
	}

	private RemittanceDetailEntity getDetail(RemittanceDetail d, Remittance r, RemittanceEntity e) {
		RemittanceDetailEntity rd = new RemittanceDetailEntity();
		rd.setRemittance(e);
		rd.setPaymentValue(getPayment(d));
		rd.setBilling(billing(r, d));
		return rd;
	}

	private BigDecimal getPayment(RemittanceDetail d) {
		BigDecimal r = d.getPaymentValue();
		return r == null || isNegative(r) ? ZERO : r;
	}

	private BillableEntity billing(Remittance r, RemittanceDetail d) {
		logger.info(
				"\n    RemittanceDetail: " + d.getOrderNo() + " - " + NumberUtils.toCurrencyText(d.getPaymentValue()));
		BillableEntity b = billableService.getEntity(d);
		logger.info("\n    BillableEntity  : " + b);
		return b == null ? null : setBalance(r, d, b);
	}

	private BillableEntity setBalance(Remittance r, RemittanceDetail rd, BillableEntity b) {
		BigDecimal bal = balance(rd, b);
		b.setUnpaidValue(bal);
		b.setFullyPaid(noBalanceAndPaidInCashOrDatedCheck(r, b, bal));
		return b;
	}

	private BigDecimal balance(RemittanceDetail d, BillableEntity b) {
		BigDecimal bal = unpaid(b).subtract(getPayment(d));
		return bal.compareTo(ONE) <= 0 ? ZERO : bal;
	}

	private BigDecimal unpaid(BillableEntity b) {
		BigDecimal u = b.getUnpaidValue();
		return u == null ? ZERO : u;
	}

	private CustomerEntity depositorBank(Remittance r) {
		return customerRepository.findByNameIgnoreCase(r.getDepositorBank());
	}

	private boolean noBalanceAndPaidInCashOrDatedCheck(Remittance r, BillableEntity b, BigDecimal bal) {
		if (!isZero(bal))
			return false;
		if (r.getPaymentDate().isAfter(LocalDate.now()))
			return false;
		return true;
	}

	private void recomputeBillingUnpaidValue(RemittanceDetailEntity d, BillableEntity b) {
		if (!isZero(d.getPaymentValue()))
			b.setUnpaidValue(b.getUnpaidValue().add(d.getPaymentValue()));
	}

	private BillableEntity unpaidBilling(RemittanceDetailEntity d) {
		BillableEntity b = d.getBilling();
		b.setFullyPaid(false);
		recomputeBillingUnpaidValue(d, b);
		return b;
	}

	private RemittanceDetailEntity unpayBilling(RemittanceDetailEntity d) {
		d.setBilling(unpaidBilling(d));
		d.setPaymentValue(ZERO);
		return d;
	}

	private RemittanceEntity update(Remittance r) {
		RemittanceEntity e = repository.findOne(r.getId());
		if (r.getIsValid() != null)
			e = updateAuditData(r, e);
		if (r.getReceivedOn() != null && e.getReceivedOn() == null)
			e = updateTransferData(r, e);
		if (r.getDepositorOn() != null && e.getDepositorOn() == null)
			e = updateDepositData(r, e);
		return e;
	}

	private RemittanceEntity updateAuditData(Remittance r, RemittanceEntity e) {
		boolean isValid = r.getIsValid();
		if (!isValid)
			e.setDetails(unpayBillings(e));
		e.setIsValid(isValid);
		e.setDecidedBy(r.getDecidedBy());
		e.setDecidedOn(r.getDecidedOn());
		e.setRemarks(r.getRemarks());
		return e;
	}

	private RemittanceEntity updateCheckData(Remittance r, RemittanceEntity e) {
		e.setCheckId(r.getCheckId());
		e.setDraweeBank(customerRepository.findByNameIgnoreCase(r.getDraweeBank()));
		return e;
	}

	private RemittanceEntity updateDepositData(Remittance r, RemittanceEntity e) {
		e.setDepositedOn(r.getDepositedOn());
		e.setDepositor(r.getDepositor());
		e.setDepositorBank(depositorBank(r));
		e.setDepositorOn(r.getDepositorOn());
		return e;
	}

	private RemittanceEntity updateTransferData(Remittance r, RemittanceEntity e) {
		e.setReceivedBy(r.getReceivedBy());
		e.setReceivedOn(r.getReceivedOn());
		return e;
	}

	@Override
	public List<RemittanceDetailEntity> listFullyPaidForMaturedPostDatedChecks() {
		return remittanceDetailRepository
				.findByRemittancePaymentDateLessThanEqualAndRemittanceCheckIdNotNullAndBillingFullyPaidFalseAndBillingUnpaidValue(
						LocalDate.now(), ZERO);

	}

	@Override
	public void saveDetails(List<RemittanceDetailEntity> l) {
		remittanceDetailRepository.save(l);
	}

	@Override
	public List<Remittance> save(List<Remittance> r) {
		return post(r).stream().map(e -> toDTO(e)).collect(Collectors.toList());
	}

	protected List<RemittanceEntity> post(List<Remittance> l) {
		return l.stream().map(r -> post(r)).collect(Collectors.toList());
	}

	private RemittanceEntity post(Remittance r) {
		return repository.save(toEntity(r));
	}
}
