package ph.txtdis.service;

import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static ph.txtdis.type.PaymentType.CASH;
import static ph.txtdis.type.PaymentType.CHECK;
import static ph.txtdis.util.TextUtils.toZonedDateTime;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.Billing;
import ph.txtdis.domain.Customer;
import ph.txtdis.domain.Remittance;
import ph.txtdis.domain.RemittanceDetail;
import ph.txtdis.repository.HolidayRepository;
import ph.txtdis.repository.RemittanceRepository;
import ph.txtdis.type.PaymentType;

@Service("remittanceRestService")
public class RemittanceRestService implements DecisionDataUpdate {

	@Value("${go.live}")
	private String goLive;

	@Value("${grace.period.cash.deposit}")
	private String gracePeriodCashDeposit;

	@Value("${grace.period.check.deposit}")
	private String gracePeriodCheckDeposit;

	@Autowired
	private CustomerRestService customer;

	@Autowired
	private HolidayRepository holiday;

	@Autowired
	private RemittanceRepository repository;

	@Autowired
	private PaymentToRemittanceService service;

	public List<Remittance> findByBilling(Billing b) {
		return repository.findByDetailsBilling(b);
	}

	public Remittance findOneUndepositedPayment(PaymentType payment, String seller, LocalDate date) {
		List<Remittance> l = listUndepositedPayments(payment, date);
		if (seller.equals("all"))
			return l.isEmpty() ? null : l.get(0);
		return getOneUndepositedPaymentOfACustomerOf(seller, l);
	}

	public void updateDeposit(String[] s) {
		Remittance r = getRemittance(s[1]);
		if (r.getDepositorOn() != null)
			return;
		r.setDepositorBank(customer.toBank(s[2]));
		r.setDepositedOn(toZonedDateTime(s[3]));
		r.setDepositor(s[4]);
		r.setDepositorOn(toZonedDateTime(s[5]));
		save(r);
	}

	public void updateFundTransfer(String[] s) {
		Remittance r = getRemittance(s[1]);
		if (r.getReceivedOn() != null)
			return;
		r.setReceivedBy(s[2]);
		r.setReceivedOn(toZonedDateTime(s[3]));
		save(r);
	}

	public void updatePaymentValidation(String[] s) {
		Remittance r = updateDecisionData(repository, s);
		if (r.getIsValid() != null && !r.getIsValid()) {
			r.setDetails(service.unpayBillings(r));
			save(r);
		}
	}

	private Long bufferDays(PaymentType payment) {
		if (payment == CASH)
			return Long.valueOf(gracePeriodCashDeposit);
		return Long.valueOf(gracePeriodCheckDeposit);
	}

	private boolean customerOf(RemittanceDetail rd, String seller) {
		Billing b = rd.getBilling();
		Customer c = b.getCustomer();
		return seller.equals(c.getSeller());
	}

	private LocalDate cutoff(PaymentType payment, LocalDate date) {
		long day = bufferDays(payment);
		while (isAWeekendOrAHoliday(date, day))
			++day;
		return date.minusDays(day);
	}

	private Remittance getOneUndepositedPaymentOfACustomerOf(String seller, List<Remittance> l) {
		Optional<RemittanceDetail> o = l.stream()//
				.flatMap(r -> r.getDetails().stream())//
				.filter(rd -> customerOf(rd, seller)).findFirst();
		if (!o.isPresent())
			return null;
		return l.stream().filter(r -> r.equals(o.get())).findFirst().get();
	}

	private Remittance getRemittance(String s) {
		return repository.findOne(Long.valueOf(s));
	}

	private LocalDate goLive() {
		return LocalDate.parse(goLive);
	}

	private boolean isAWeekendOrAHoliday(LocalDate date, long day) {
		LocalDate newDate = date.minusDays(day);
		return newDate.getDayOfWeek() == SATURDAY //
				|| newDate.getDayOfWeek() == SUNDAY //
				|| holiday.findByDeclaredDate(newDate) != null;
	}

	private List<Remittance> listUndepositedCashPayments(LocalDate date) {
		return repository.findByDetailsBillingCustomerNotAndDepositedOnNullAndCheckIdNullAndPaymentDateBetween(
				customer.getVendor(), goLive(), cutoff(CASH, date));
	}

	private List<Remittance> listUndepositedCheckPayments(LocalDate date) {
		return repository.findByDetailsBillingCustomerNotAndReceivedOnNullAndCheckIdNotNullAndPaymentDateBetween(
				customer.getVendor(), goLive(), cutoff(CHECK, date));
	}

	private List<Remittance> listUndepositedPayments(PaymentType payment, LocalDate date) {
		if (payment == CASH)
			return listUndepositedCashPayments(date);
		return listUndepositedCheckPayments(date);
	}

	private void save(Remittance r) {
		repository.save(r);
	}
}
