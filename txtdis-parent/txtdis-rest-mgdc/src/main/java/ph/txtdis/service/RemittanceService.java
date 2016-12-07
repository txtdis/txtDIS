package ph.txtdis.service;

import java.time.LocalDate;
import java.util.List;

import ph.txtdis.domain.RemittanceDetailEntity;
import ph.txtdis.domain.RemittanceEntity;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.Remittance;
import ph.txtdis.exception.DateBeforeGoLiveException;
import ph.txtdis.exception.EndDateBeforeStartException;
import ph.txtdis.repository.RemittanceRepository;
import ph.txtdis.type.PaymentType;

public interface RemittanceService
		extends DecisionDataUpdate<RemittanceEntity, RemittanceRepository>, SpunService<Remittance, Long> {

	Remittance findByCheck(Long bankId, Long checkId);

	Remittance findByCollector(String name, LocalDate d);

	Remittance findByDate(LocalDate d);

	Remittance findByUndepositedPayments(PaymentType payType, String seller, LocalDate upToDate);

	Remittance findIfCurrentlyInvalidated(Long id);

	Remittance findOneUndepositedPayment(PaymentType payment, String seller, LocalDate date);

	List<Remittance> list();

	List<Remittance> list(LocalDate start, LocalDate end) throws DateBeforeGoLiveException, EndDateBeforeStartException;

	List<RemittanceDetailEntity> listFullyPaidForMaturedPostDatedChecks();

	List<Remittance> listRemittanceByBilling(Billable b);

	List<Remittance> save(List<Remittance> p);

	void saveDetails(List<RemittanceDetailEntity> l);

	void updateDeposit(String[] s);

	void updateFundTransfer(String[] s);

	void updatePaymentValidation(String[] s);
}