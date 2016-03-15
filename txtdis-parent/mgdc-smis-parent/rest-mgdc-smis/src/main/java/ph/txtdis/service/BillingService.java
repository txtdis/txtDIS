package ph.txtdis.service;

import static java.math.BigDecimal.ZERO;
import static ph.txtdis.util.DateTimeUtils.toDate;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.Billing;
import ph.txtdis.domain.Remittance;
import ph.txtdis.repository.BillingRepository;
import ph.txtdis.type.ScriptType;

@Service("billingService")
public class BillingService implements DecisionDataUpdate {

	@Autowired
	private BillingRepository repository;

	@Autowired
	private RemittanceRestService remittanceService;

	public void updateDecisionData(String[] s) {
		Billing b = updateDecisionData(repository, s);
		if (billingIsInvalid(b))
			nullifyBilledAndPaymentData(s, b);
	}

	private boolean billingIsInvalid(Billing b) {
		return b.getIsValid() != null && !b.getIsValid();
	}

	private void nullifyBilledAndPaymentData(String[] s, Billing b) {
		b.setBilledBy(null);
		b.setBilledOn(null);
		b = repository.save(b);
		List<Remittance> l = remittanceService.findByBilling(b);
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

	public void updateItemReturnPayment(String[] s) {
		Billing b = repository.findOne(Long.valueOf(s[1]));
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
}
