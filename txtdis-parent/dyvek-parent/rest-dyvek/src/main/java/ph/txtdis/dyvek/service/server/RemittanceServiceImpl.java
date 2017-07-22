package ph.txtdis.dyvek.service.server;

import static java.util.stream.Collectors.toList;
import static ph.txtdis.dyvek.service.server.CashAdvanceService.CASH_ADVANCE;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ph.txtdis.dto.Remittance;
import ph.txtdis.dto.RemittanceDetail;
import ph.txtdis.dyvek.domain.BillableEntity;
import ph.txtdis.dyvek.domain.CustomerEntity;
import ph.txtdis.dyvek.domain.RemittanceDetailEntity;
import ph.txtdis.dyvek.domain.RemittanceEntity;
import ph.txtdis.dyvek.model.Billable;
import ph.txtdis.dyvek.repository.RemittanceDetailRepository;
import ph.txtdis.dyvek.repository.RemittanceRepository;
import ph.txtdis.service.AbstractSpunSavedKeyedService;
import ph.txtdis.type.PartnerType;

@Service("remittanceService")
public class RemittanceServiceImpl //
		extends AbstractSpunSavedKeyedService<RemittanceRepository, RemittanceEntity, Remittance, Long> //
		implements DyvekRemittanceService {

	@Autowired
	private CashAdvanceService cashAdvanceService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private ClientBillService billingService;

	@Autowired
	private RemittanceDetailRepository remittanceDetailRepository;

	@Override
	public List<Remittance> findAll(Billable b) {
		List<RemittanceEntity> l = findEntitiesByBillingId(b.getId());
		return toRemittances(l);
	}

	private List<RemittanceEntity> findEntitiesByBillingId(Long id) {
		return repository.findByDetailsBillingId(id);
	}

	private List<Remittance> toRemittances(List<RemittanceEntity> l) {
		return l.stream().map(e -> newRemittance(e)).collect(Collectors.toList());
	}

	private Remittance newRemittance(RemittanceEntity e) {
		Remittance r = toPaymentOnlyRemittance(e);
		r.setReceivedDate(e.getReceivedDate());
		r.setRemarks(e.getRemarks());
		r.setCollector(getReceivedFrom(e));
		r.setCreatedBy(e.getCreatedBy());
		r.setCreatedOn(e.getCreatedOn());
		r.setDetails(details(e));
		if (e.getIsValid() != null)
			r = setAuditData(e, r);
		if (e.getDepositedOn() != null)
			r = setDepositData(e, r);
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

	private Remittance setCheckData(RemittanceEntity e, Remittance r) {
		r.setCheckId(e.getCheckId());
		r.setDraweeBank(draweeBank(e));
		return r;
	}

	private String draweeBank(RemittanceEntity e) {
		if (e == null)
			return null;
		CustomerEntity bank = e.getDrawnFrom();
		return bank == null ? null : bank.getName();
	}

	private String getReceivedFrom(RemittanceEntity e) {
		CustomerEntity c = e.getReceivedFrom();
		return c == null ? null : c.getName();
	}

	private List<RemittanceDetail> details(RemittanceEntity e) {
		List<RemittanceDetailEntity> l = e.getDetails();
		return toDetails(l);
	}

	private RemittanceDetail detail(RemittanceDetailEntity e) {
		BillableEntity b = e.getBilling();
		return b == null ? null : newDetail(e, b);
	}

	private RemittanceDetail newDetail(RemittanceDetailEntity de, BillableEntity b) {
		RemittanceEntity e = de.getRemittance();
		RemittanceDetail d = new RemittanceDetail();
		d.setId(b.getId());
		d.setOrderNo(b.getOrderNo());
		d.setCustomer(customer(e, b));
		d.setPaymentValue(e.getValue());
		return d;
	}

	private String customer(RemittanceEntity r, BillableEntity b) {
		CustomerEntity c = b.getCustomer();
		if (r.getReceivedFrom().getType() == PartnerType.VENDOR)
			c = b.getDelivery().getRecipient();
		return c.getName();
	}

	private Remittance setAuditData(RemittanceEntity e, Remittance r) {
		r.setIsValid(e.getIsValid());
		r.setDecidedBy(e.getDecidedBy());
		r.setDecidedOn(e.getDecidedOn());
		return r;
	}

	private Remittance setDepositData(RemittanceEntity e, Remittance r) {
		r.setDepositorBank(e.getDepositedTo().getName());
		r.setDepositedOn(e.getDepositedOn());
		r.setDepositor(e.getDepositor());
		r.setDepositorOn(e.getDepositorOn());
		return r;
	}

	@Override
	public Remittance findByCheck(String bank, Long checkId) {
		RemittanceEntity e = findEntityByCheck(bank, checkId);
		return toModel(e);
	}

	@Override
	public RemittanceEntity findEntityByBillingId(Long id) {
		return repository.findFirstByDetailsBillingId(id);
	}

	@Override
	public RemittanceEntity findEntityByCheck(String bank, Long checkId) {
		List<RemittanceEntity> l = repository.findByDrawnFromNameAndCheckId(bank, checkId);
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
	@Transactional
	public RemittanceEntity post(RemittanceEntity e) {
		e = super.post(e);
		if (e != null && e.getDrawnFrom().getName().equals(CASH_ADVANCE))
			cashAdvanceService.update(e);
		return e;
	}

	@Override
	public void saveDetails(List<RemittanceDetailEntity> l) {
		remittanceDetailRepository.save(l);
	}

	@Override
	protected RemittanceEntity toEntity(Remittance r) {
		return r.getId() == null ? newEntity(r) : update(r);
	}

	private RemittanceEntity newEntity(Remittance r) {
		RemittanceEntity e = new RemittanceEntity();
		e.setReceivedDate(r.getReceivedDate());
		e.setPaymentDate(r.getPaymentDate());
		e.setValue(r.getValue());
		e.setReceivedFrom(customer(r.getCollector()));
		e.setRemarks(r.getRemarks());
		e.setDetails(details(r, e));
		return updateCheckData(r, e);
	}

	private List<RemittanceDetailEntity> details(Remittance r, RemittanceEntity e) {
		return r.getDetails().stream() //
				.map(d -> detail(d, r, e)) //
				.filter(d -> d.getBilling() != null) //
				.collect(toList());
	}

	private RemittanceDetailEntity detail(RemittanceDetail rd, Remittance r, RemittanceEntity e) {
		RemittanceDetailEntity ed = new RemittanceDetailEntity();
		ed.setRemittance(e);
		ed.setBilling(billingService.findEntityByPrimaryKey(rd.getId()));
		return ed;
	}

	private RemittanceEntity updateCheckData(Remittance r, RemittanceEntity e) {
		e.setCheckId(r.getCheckId());
		e.setDrawnFrom(customer(r.getDraweeBank()));
		return e;
	}

	private RemittanceEntity update(Remittance r) {
		RemittanceEntity e = repository.findOne(r.getId());
		if (r.getIsValid() != null)
			e = updateAuditData(r, e);
		if (r.getDepositorOn() != null && e.getDepositorOn() == null)
			e = updateDepositData(r, e);
		return e;
	}

	private RemittanceEntity updateAuditData(Remittance r, RemittanceEntity e) {
		boolean isValid = r.getIsValid();
		e.setIsValid(isValid);
		e.setDecidedBy(r.getDecidedBy());
		e.setDecidedOn(r.getDecidedOn());
		e.setRemarks(r.getRemarks());
		return e;
	}

	private RemittanceEntity updateDepositData(Remittance r, RemittanceEntity e) {
		e.setDepositedOn(r.getDepositedOn());
		e.setDepositor(r.getDepositor());
		e.setDepositedTo(customer(r.getDepositorBank()));
		e.setDepositorOn(r.getDepositorOn());
		return e;
	}

	private CustomerEntity customer(String name) {
		return customerService.findEntityByName(name);
	}

	@Override
	public List<RemittanceDetail> toDetails(List<RemittanceDetailEntity> l) {
		return l == null ? null : l.stream().map(d -> detail(d)).collect(Collectors.toList());
	}

	@Override
	protected Remittance toModel(RemittanceEntity e) {
		return e == null ? null : newRemittance(e);
	}
}
