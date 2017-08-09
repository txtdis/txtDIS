package ph.txtdis.dyvek.service.server;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ph.txtdis.dto.Remittance;
import ph.txtdis.dto.RemittanceDetail;
import ph.txtdis.dyvek.domain.*;
import ph.txtdis.dyvek.model.Billable;
import ph.txtdis.dyvek.repository.RemittanceDetailRepository;
import ph.txtdis.dyvek.repository.RemittanceRepository;
import ph.txtdis.service.AbstractSpunSavedKeyedService;
import ph.txtdis.type.PartnerType;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static ph.txtdis.dyvek.service.server.CashAdvanceService.CASH_ADVANCE;

@Service("remittanceService")
public class RemittanceServiceImpl
	extends AbstractSpunSavedKeyedService<RemittanceRepository, RemittanceEntity, Remittance, Long>
	implements DyvekRemittanceService {

	private final CashAdvanceService cashAdvanceService;

	private final CustomerService customerService;

	private final ClientBillService billingService;

	private final RemittanceDetailRepository remittanceDetailRepository;

	public RemittanceServiceImpl(CashAdvanceService cashAdvanceService,
	                             CustomerService customerService,
	                             ClientBillService billingService,
	                             RemittanceDetailRepository remittanceDetailRepository) {
		this.cashAdvanceService = cashAdvanceService;
		this.customerService = customerService;
		this.billingService = billingService;
		this.remittanceDetailRepository = remittanceDetailRepository;
	}

	@Override
	public List<Remittance> findAll(Billable b) {
		List<RemittanceEntity> l = findEntitiesByBillingId(b.getId());
		return toRemittances(l);
	}

	public List<RemittanceEntity> findEntitiesByBillingId(Long id) {
		return repository.findByDetailsBillingId(id);
	}

	private List<Remittance> toRemittances(List<RemittanceEntity> l) {
		return l.stream().map(this::newRemittance).collect(Collectors.toList());
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

	@Override
	public Remittance findByCheck(String bank, Long checkId) {
		RemittanceEntity e = findEntityByCheck(bank, checkId);
		return toModel(e);
	}

	@Override
	public RemittanceEntity findEntityByCheck(String bank, Long checkId) {
		List<RemittanceEntity> l = repository.findByDrawnFromNameAndCheckId(bank, checkId);
		return l == null ? null : oneValid(l);
	}

	@Override
	protected Remittance toModel(RemittanceEntity e) {
		return e == null ? null : newRemittance(e);
	}

	private RemittanceEntity oneValid(List<RemittanceEntity> l) {
		return l.stream().filter(notInvalid()).findFirst().orElse(null);
	}

	private Remittance newRemittance(RemittanceEntity e) {
		Remittance r = toPaymentOnlyRemittance(e);
		r.setReceivedDate(e.getReceivedDate());
		r.setRemarks(e.getRemarks());
		r.setReceivedFrom(getReceivedFrom(e));
		r.setCreatedBy(e.getCreatedBy());
		r.setCreatedOn(e.getCreatedOn());
		r.setDetails(details(e));
		if (e.getIsValid() != null)
			r = setAuditData(e, r);
		if (e.getDepositedOn() != null)
			r = setDepositData(e, r);
		return r;
	}

	private Predicate<RemittanceEntity> notInvalid() {
		return r -> r.getIsValid() == null || r.getIsValid();
	}

	private Remittance toPaymentOnlyRemittance(RemittanceEntity e) {
		Remittance r = toIdOnlyRemittance(e);
		r.setPaymentDate(e.getPaymentDate());
		r.setValue(e.getValue());
		return setCheckData(e, r);
	}

	private String getReceivedFrom(RemittanceEntity e) {
		CustomerEntity c = e.getReceivedFrom();
		return c == null ? null : c.getName();
	}

	private List<RemittanceDetail> details(RemittanceEntity e) {
		List<RemittanceDetailEntity> l = e.getDetails();
		return toDetails(l);
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

	@Override
	public List<RemittanceDetail> toDetails(List<RemittanceDetailEntity> l) {
		return l == null ? null : l.stream().map(this::detail).collect(Collectors.toList());
	}

	private String draweeBank(RemittanceEntity e) {
		if (e == null)
			return null;
		CustomerEntity bank = e.getDrawnFrom();
		return bank == null ? null : bank.getName();
	}

	@Override
	public List<RemittanceDetail> findLiquidationsByCashAdvanceId(Long id) {
		CashAdvanceEntity ca = cashAdvanceService.findEntityByPrimaryKey(id);
		return toDetails(ca.getLiquidations());
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
		e.setReceivedFrom(customer(r.getReceivedFrom()));
		e.setRemarks(r.getRemarks());
		e.setDetails(details(r, e));
		return updateCheckData(r, e);
	}

	private RemittanceEntity update(Remittance r) {
		RemittanceEntity e = repository.findOne(r.getId());
		if (r.getIsValid() != null)
			e = updateAuditData(r, e);
		if (r.getDepositorOn() != null && e.getDepositorOn() == null)
			e = updateDepositData(r, e);
		return e;
	}

	private CustomerEntity customer(String name) {
		return customerService.findEntityByName(name);
	}

	private List<RemittanceDetailEntity> details(Remittance r, RemittanceEntity e) {
		return r.getDetails().stream()
			.map(d -> detail(d, e))
			.filter(d -> d.getBilling() != null)
			.collect(toList());
	}

	private RemittanceEntity updateCheckData(Remittance r, RemittanceEntity e) {
		e.setCheckId(r.getCheckId());
		e.setDrawnFrom(customer(r.getDraweeBank()));
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

	private RemittanceDetailEntity detail(RemittanceDetail rd, RemittanceEntity e) {
		RemittanceDetailEntity ed = new RemittanceDetailEntity();
		ed.setRemittance(e);
		ed.setBilling(billingService.findEntityByPrimaryKey(rd.getId()));
		return ed;
	}
}
