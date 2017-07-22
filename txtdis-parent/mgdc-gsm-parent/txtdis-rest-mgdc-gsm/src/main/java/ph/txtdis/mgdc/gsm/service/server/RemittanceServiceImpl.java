package ph.txtdis.mgdc.gsm.service.server;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.util.stream.Collectors.toList;
import static ph.txtdis.util.NumberUtils.isNegative;
import static ph.txtdis.util.NumberUtils.isZero;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ph.txtdis.dto.Remittance;
import ph.txtdis.dto.RemittanceDetail;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.mgdc.gsm.domain.RemittanceDetailEntity;
import ph.txtdis.mgdc.gsm.domain.RemittanceEntity;
import ph.txtdis.service.CredentialService;
import ph.txtdis.service.ReadOnlyService;

@Service("remittanceService")
public class RemittanceServiceImpl //
		extends AbstractRemittanceService //
		implements GsmRemittanceService {

	@Autowired
	private AllBillingService billingService;

	@Autowired
	private CredentialService userService;

	@Autowired
	private ReadOnlyService<Remittance> readOnlyService;

	@Value("#{'${adjusting.accounts}'.split(',')}")
	private List<String> adjustingAccounts;

	@Override
	protected List<String> adjustingAccounts() {
		return adjustingAccounts;
	}

	@Override
	public List<RemittanceEntity> findAllByDraweeBank(String name) {
		return repository.findByDraweeBankName(name);
	}

	@Override
	public List<RemittanceEntity> findAllUnvalidatedAfterPrescribedPeriodSincePaidHasExpired(long days) {
		LocalDate latestDateToValidate = LocalDate.now().minusDays(days);
		ZonedDateTime createdOn = latestDateToValidate.atStartOfDay(ZoneId.systemDefault());
		return repository.findByIsValidNullAndPaymentDateLessThanAndCreatedOnLessThan(latestDateToValidate, createdOn);
	}

	@Override
	@Transactional
	public void importAll() throws Exception {
		post(toEntities(remittances()));
	}

	private List<Remittance> remittances() throws Exception {
		return readOnlyService.module("remittance").getList();
	}

	@Override
	protected RemittanceEntity newEntity(Remittance r) {
		RemittanceEntity e = super.newEntity(r);
		e.setDetails(details(e, r));
		return autoValidateAdjustmentPayments(e, r);
	}

	private List<RemittanceDetailEntity> details(RemittanceEntity e, Remittance r) {
		return r.getDetails().stream() //
				.map(d -> detail(d, r, e)) //
				.filter(d -> d.getBilling() != null) //
				.collect(toList());
	}

	private RemittanceDetailEntity detail(RemittanceDetail d, Remittance r, RemittanceEntity e) {
		RemittanceDetailEntity rd = new RemittanceDetailEntity();
		rd.setRemittance(e);
		rd.setPaymentValue(paymentValue(d));
		rd.setBilling(billing(r, d));
		return rd;
	}

	private BigDecimal paymentValue(RemittanceDetail d) {
		BigDecimal r = d.getPaymentValue();
		return r == null || isNegative(r) ? ZERO : r;
	}

	private BillableEntity billing(Remittance r, RemittanceDetail d) {
		BillableEntity b = billingService.findEntity(d);
		return b == null ? null : setBalance(r, d, b);
	}

	private BillableEntity setBalance(Remittance r, RemittanceDetail rd, BillableEntity b) {
		BigDecimal bal = balance(rd, b);
		b.setUnpaidValue(bal);
		b.setFullyPaid(noBalanceAndPaidInCashOrDatedCheck(r, b, bal));
		return b;
	}

	private BigDecimal balance(RemittanceDetail d, BillableEntity b) {
		BigDecimal bal = unpaid(b).subtract(paymentValue(d));
		return bal.compareTo(ONE) <= 0 ? ZERO : bal;
	}

	private BigDecimal unpaid(BillableEntity b) {
		BigDecimal u = b.getUnpaidValue();
		return u == null ? ZERO : u;
	}

	private boolean noBalanceAndPaidInCashOrDatedCheck(Remittance r, BillableEntity b, BigDecimal bal) {
		if (!isZero(bal))
			return false;
		if (r.getPaymentDate().isAfter(LocalDate.now()))
			return false;
		return true;
	}

	private RemittanceEntity autoValidateAdjustmentPayments(RemittanceEntity e, Remittance r) {
		if (adjustingAccounts.contains(r.getDraweeBank()))
			return updateValidity(e, r, true, userService.username());
		return e;
	}

	@Override
	protected Remittance newRemittance(RemittanceEntity e) {
		Remittance r = super.newRemittance(e);
		r.setDetails(details(e));
		return r;
	}

	private List<RemittanceDetail> details(RemittanceEntity e) {
		List<RemittanceDetailEntity> l = e.getDetails();
		return l == null ? null : l.stream().map(d -> detail(d)).collect(Collectors.toList());
	}

	private RemittanceDetail detail(RemittanceDetailEntity e) {
		BillableEntity b = e.getBilling();
		return b == null ? null : newDetail(e, b);
	}

	private RemittanceDetail newDetail(RemittanceDetailEntity e, BillableEntity b) {
		RemittanceDetail r = new RemittanceDetail();
		r.setId(b.getId());
		r.setOrderNo(b.getOrderNo());
		r.setCustomer(b.getCustomer().getName());
		r.setDueDate(b.getDueDate());
		r.setTotalDueValue(b.getTotalValue());
		r.setPaymentValue(e.getPaymentValue());
		return r;
	}
}