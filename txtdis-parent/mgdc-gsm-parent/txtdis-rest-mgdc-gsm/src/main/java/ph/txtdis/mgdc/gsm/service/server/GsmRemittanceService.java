package ph.txtdis.mgdc.gsm.service.server;

import java.time.LocalDate;
import java.util.List;

import ph.txtdis.dto.Remittance;
import ph.txtdis.mgdc.gsm.domain.RemittanceEntity;
import ph.txtdis.type.PaymentType;

public interface GsmRemittanceService extends Imported, PastValidationPeriodRemittanceService {

	List<RemittanceEntity> findAllByDraweeBank(String name);

	// TODO

	Remittance findByUndepositedPayments(PaymentType payType, String seller, LocalDate upToDate);

	Remittance findPending();

	Remittance findUndepositedPayment(PaymentType payment, String seller, LocalDate date);

	List<Remittance> findAll(LocalDate start, LocalDate end) throws Exception;

	List<Remittance> findAll(String collector, LocalDate start, LocalDate end) throws Exception;

	List<Remittance> findAllUnvalidated();

	List<Remittance> findAllUnvalidatedChecksByBank(Long bankId);

	Remittance findByCollector(String name, LocalDate d);

	Remittance findByDate(LocalDate d);

	List<Remittance> save(List<Remittance> l);

	void updateDeposit(String... s);

	void updateFundTransfer(String... s);

}
