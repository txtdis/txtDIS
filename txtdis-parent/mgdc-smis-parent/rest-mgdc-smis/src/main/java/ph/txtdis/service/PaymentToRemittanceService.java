package ph.txtdis.service;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.util.stream.Collectors.toList;
import static ph.txtdis.util.NumberUtils.isNegative;
import static ph.txtdis.util.NumberUtils.isZero;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.Billing;
import ph.txtdis.domain.Customer;
import ph.txtdis.domain.Remittance;
import ph.txtdis.domain.RemittanceDetail;
import ph.txtdis.dto.Payment;
import ph.txtdis.dto.PaymentDetail;
import ph.txtdis.repository.BillingRepository;
import ph.txtdis.repository.CustomerRepository;
import ph.txtdis.repository.RemittanceRepository;

@Service("paymentToRemittanceService")
public class PaymentToRemittanceService {

	@Autowired
	private BillingRepository billingRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private RemittanceRepository remittanceRepository;

	public List<Remittance> toRemittance(List<Payment> l) {
		return l == null ? null : convert(l);
	}

	public Remittance toRemittance(Payment p) {
		return p == null ? null : convert(p);
	}

	public List<RemittanceDetail> unpayBillings(Remittance r) {
		return r.getDetails().stream().map(d -> unpayBilling(d)).collect(toList());
	}

	private BigDecimal balance(PaymentDetail pd, Billing b) {
		BigDecimal bal = unpaid(b).subtract(payment(pd));
		return bal.compareTo(ONE) <= 0 ? ZERO : bal;
	}

	private Billing billing(Payment p, PaymentDetail pd) {
		Billing b = billingRepository.findOne(pd.getId());
		BigDecimal bal = balance(pd, b);
		b.setUnpaidValue(bal);
		b.setFullyPaid(noBalanceAndPaidInCashOrDatedCheck(p, b, bal));
		return b;
	}

	private List<Remittance> convert(List<Payment> l) {
		return l.stream().map(p -> convert(p)).collect(toList());
	}

	private Remittance convert(Payment p) {
		return p.getId() == null ? create(p) : update(p);
	}

	private List<RemittanceDetail> convertDetails(Payment p, Remittance r) {
		return p.getDetails().stream().map(d -> createDetail(d, p, r)).collect(toList());
	}

	private Remittance create(Payment p) {
		Remittance r = new Remittance();
		r.setPaymentDate(p.getPaymentDate());
		r.setValue(p.getValue());
		r.setCollector(p.getCollector());
		r.setRemarks(p.getRemarks());
		updateCheckData(p, r);
		r.setDetails(convertDetails(p, r));
		return r;
	}

	private RemittanceDetail createDetail(PaymentDetail pd, Payment p, Remittance r) {
		RemittanceDetail rd = new RemittanceDetail();
		rd.setRemittance(r);
		rd.setBilling(billing(p, pd));
		rd.setPaymentValue(payment(pd));
		return rd;
	}

	private Customer depositorBank(Payment p) {
		return customerRepository.findByName(p.getDepositorBank());
	}

	private boolean noBalanceAndPaidInCashOrDatedCheck(Payment p, Billing b, BigDecimal bal) {
		if (!isZero(bal))
			return false;
		if (p.getPaymentDate().isAfter(LocalDate.now()))
			return false;
		return true;
	}

	private BigDecimal payment(PaymentDetail d) {
		BigDecimal p = d.getPaymentValue();
		return p == null || isNegative(p) ? ZERO : p;
	}

	private void recomputeBillingUnpaidValue(RemittanceDetail d, Billing b) {
		if (!isZero(d.getPaymentValue()))
			b.setUnpaidValue(b.getUnpaidValue().add(d.getPaymentValue()));
	}

	private BigDecimal unpaid(Billing b) {
		BigDecimal u = b.getUnpaidValue();
		return u == null ? ZERO : u;
	}

	private Billing unpaidBilling(RemittanceDetail d) {
		Billing b = d.getBilling();
		b.setFullyPaid(false);
		recomputeBillingUnpaidValue(d, b);
		return b;
	}

	private RemittanceDetail unpayBilling(RemittanceDetail d) {
		d.setBilling(unpaidBilling(d));
		d.setPaymentValue(ZERO);
		return d;
	}

	private Remittance update(Payment p) {
		Remittance r = remittanceRepository.findOne(p.getId());
		if (p.getIsValid() != null)
			r = updateAuditData(p, r);
		if (p.getReceivedOn() != null && r.getReceivedOn() == null)
			r = updateTransferData(p, r);
		if (p.getDepositorOn() != null && r.getDepositorOn() == null)
			r = updateDepositData(p, r);
		return r;
	}

	private Remittance updateAuditData(Payment p, Remittance r) {
		boolean isValid = p.getIsValid();
		if (!isValid)
			r.setDetails(unpayBillings(r));
		r.setIsValid(isValid);
		r.setDecidedBy(p.getDecidedBy());
		r.setDecidedOn(p.getDecidedOn());
		r.setRemarks(p.getRemarks());
		return r;
	}

	private Remittance updateCheckData(Payment p, Remittance r) {
		r.setCheckId(p.getCheckId());
		r.setDraweeBank(customerRepository.findByName(p.getDraweeBank()));
		return r;
	}

	private Remittance updateDepositData(Payment p, Remittance r) {
		r.setDepositedOn(p.getDepositedOn());
		r.setDepositor(p.getDepositor());
		r.setDepositorBank(depositorBank(p));
		r.setDepositorOn(p.getDepositorOn());
		return r;
	}

	private Remittance updateTransferData(Payment p, Remittance r) {
		r.setReceivedBy(p.getReceivedBy());
		r.setReceivedOn(p.getReceivedOn());
		return r;
	}
}
