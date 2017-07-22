package ph.txtdis.mgdc.ccbpi.service.server;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Remittance;
import ph.txtdis.mgdc.ccbpi.domain.CustomerEntity;
import ph.txtdis.mgdc.ccbpi.domain.RemittanceEntity;
import ph.txtdis.mgdc.ccbpi.repository.RemittanceRepository;
import ph.txtdis.service.AbstractSpunSavedKeyedService;

@Service("remittanceService")
public class RemittanceServiceImpl //
		extends AbstractSpunSavedKeyedService<RemittanceRepository, RemittanceEntity, Remittance, Long> //
		implements NoDetailRemittanceService {

	@Autowired
	private CustomerService customerService;

	@Value("${grace.period.cash.deposit}")
	private String gracePeriodCashDeposit;

	@Value("${grace.period.check.deposit}")
	private String gracePeriodCheckDeposit;

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
	public List<Remittance> save(List<Remittance> l) {
		return super.save(l);
	}

	@Override
	protected Remittance toModel(RemittanceEntity e) {
		return e == null ? null : newRemittance(e);
	}

	private Remittance newRemittance(RemittanceEntity e) {
		Remittance r = toPaymentOnlyRemittance(e);
		r.setRemarks(e.getRemarks());
		r.setCollector(e.getCollector());
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

	private Remittance toIdOnlyRemittance(RemittanceEntity e) {
		Remittance r = new Remittance();
		r.setId(e.getId());
		return r;
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

	private String draweeBank(RemittanceEntity e) {
		CustomerEntity bank = e == null ? null : e.getDraweeBank();
		return bank == null ? null : bank.getName();
	}

	private Remittance setCheckData(RemittanceEntity e, Remittance r) {
		r.setCheckId(e.getCheckId());
		r.setDraweeBank(draweeBank(e));
		return r;
	}

	@Override
	protected RemittanceEntity toEntity(Remittance r) {
		return r.getId() == null ? newEntity(r) : update(r);
	}

	private RemittanceEntity newEntity(Remittance r) {
		RemittanceEntity e = new RemittanceEntity();
		e.setPaymentDate(r.getPaymentDate());
		e.setValue(r.getValue());
		e.setCollector(r.getCollector());
		e.setRemarks(r.getRemarks());
		return updateCheckData(e, r);
	}

	private RemittanceEntity updateCheckData(RemittanceEntity e, Remittance r) {
		e.setCheckId(r.getCheckId());
		e.setDraweeBank(bank(r.getDraweeBank()));
		return e;
	}

	private CustomerEntity bank(String name) {
		return customerService.findEntityByName(name);
	}

	private RemittanceEntity update(Remittance r) {
		RemittanceEntity e = repository.findOne(r.getId());
		if (r.getReceivedBy() != null && e.getReceivedBy() == null)
			e = updateTransferData(r, e);
		if (r.getDepositorOn() != null && e.getDepositorOn() == null)
			e = updateDepositData(r, e);
		return e;
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
}
