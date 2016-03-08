package ph.txtdis.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.math.BigDecimal.ZERO;

import ph.txtdis.domain.RemittanceDetail;
import ph.txtdis.repository.RemittanceDetailRepository;

@Component("postDatedChequeService")
public class PostDatedChequeService {

	@Autowired
	private RemittanceDetailRepository repository;

	public void setFullyPaidForMaturedPostDatedChecks() {
		List<RemittanceDetail> l = repository
				.findByRemittancePaymentDateLessThanEqualAndRemittanceCheckIdNotNullAndBillingFullyPaidFalseAndBillingUnpaidValue(
						LocalDate.now(), ZERO);
		l.forEach(d -> d.getBilling().setFullyPaid(true));
		repository.save(l);
	}
}
