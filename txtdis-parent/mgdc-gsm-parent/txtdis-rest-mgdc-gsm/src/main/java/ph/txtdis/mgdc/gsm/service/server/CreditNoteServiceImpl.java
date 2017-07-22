package ph.txtdis.mgdc.gsm.service.server;

import static java.util.stream.Collectors.toList;
import static ph.txtdis.util.DateTimeUtils.toTimestampText;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.CreditNote;
import ph.txtdis.dto.CreditNotePayment;
import ph.txtdis.mgdc.gsm.domain.CreditNoteEntity;
import ph.txtdis.mgdc.gsm.domain.CreditNotePaymentEntity;
import ph.txtdis.mgdc.gsm.domain.RemittanceEntity;
import ph.txtdis.mgdc.gsm.repository.CreditNoteRepository;
import ph.txtdis.service.AbstractSpunSavedKeyedService;

@Service("creditNoteService")
public class CreditNoteServiceImpl
		extends AbstractSpunSavedKeyedService<CreditNoteRepository, CreditNoteEntity, CreditNote, Long> //
		implements CreditNoteService {

	@Autowired
	private GsmRemittanceService remittanceService;

	@Override
	public List<CreditNote> findAll() {
		List<CreditNoteEntity> l = repository.findByOrderByIdAsc();
		return toModels(l);
	}

	@Override
	public List<CreditNote> findAllUnpaid() {
		List<CreditNoteEntity> l = repository.findByBalanceValueGreaterThanOrderByIdAsc(BigDecimal.ZERO);
		return toModels(l);
	}

	@Override
	public List<CreditNote> findAllUnvalidated() {
		List<CreditNoteEntity> l = repository.findByIsValidNullOrderByIdAsc();
		return toModels(l);
	}

	@Override
	protected List<CreditNote> toModels(List<CreditNoteEntity> l) {
		return l == null ? null : l.stream().map(e -> toModel(e)).collect(toList());
	}

	@Override
	protected CreditNote toModel(CreditNoteEntity e) {
		if (e == null)
			return null;
		CreditNote c = new CreditNote();
		c.setId(e.getId());
		c.setBalanceValue(e.getBalanceValue());
		c.setCreatedBy(e.getCreatedBy());
		c.setCreatedOn(e.getCreatedOn());
		c.setCreditDate(e.getCreditDate());
		c.setDecidedBy(e.getDecidedBy());
		c.setDecidedOn(e.getDecidedOn());
		c.setDescription(e.getDescription());
		c.setIsValid(e.getIsValid());
		c.setLastModifiedBy(e.getLastModifiedBy());
		c.setLastModifiedOn(e.getLastModifiedOn());
		c.setPayments(payments(e));
		c.setReference(e.getReference());
		c.setRemarks(e.getRemarks());
		c.setTotalValue(e.getTotalValue());
		return c;
	}

	private List<CreditNotePayment> payments(CreditNoteEntity e) {
		List<CreditNotePaymentEntity> l = e.getPayments();
		return l == null ? null : l.stream().map(p -> toModel(p)).collect(toList());
	}

	private CreditNotePayment toModel(CreditNotePaymentEntity e) {
		if (e == null)
			return null;
		CreditNotePayment p = new CreditNotePayment();
		p.setId(e.getId());
		p.setPaymentDate(e.getPaymentDate());
		p.setPaymentRemarks(e.getPaymentRemarks());
		p.setPaymentValue(e.getPaymentValue());
		p.setReference(e.getReference());
		return p;
	}

	@Override
	protected CreditNoteEntity toEntity(CreditNote c) {
		return c == null ? null : setEntity(c);
	}

	private CreditNoteEntity setEntity(CreditNote c) {
		return c.getId() == null ? create(c) : update(c);
	}

	private CreditNoteEntity create(CreditNote c) {
		CreditNoteEntity e = new CreditNoteEntity();
		e.setCreditDate(c.getCreditDate());
		e.setDescription(c.getDescription());
		e.setReference(c.getReference());
		e.setTotalValue(c.getTotalValue());
		return setRemarksAndBalance(e, c);
	}

	private CreditNoteEntity setRemarksAndBalance(CreditNoteEntity e, CreditNote c) {
		e.setRemarks(c.getRemarks());
		e.setBalanceValue(c.getBalanceValue());
		return e;
	}

	private CreditNoteEntity update(CreditNote c) {
		CreditNoteEntity e = repository.findOne(c.getId());
		if (isDecisionToBeChanged(e, c))
			e = updateDecisionData(e, c);
		if (isNotInvalid(e))
			return setPaymentsAndRemarksAndBalance(e, c);
		return invalidateTheRemittanceFromTheInvalidCreditNoteUnpayingAffectedBillings(e);
	}

	private boolean isDecisionToBeChanged(CreditNoteEntity e, CreditNote c) {
		return c.getIsValid() != null && e.getIsValid() == null;
	}

	private CreditNoteEntity updateDecisionData(CreditNoteEntity e, CreditNote c) {
		e.setIsValid(c.getIsValid());
		e.setDecidedBy(c.getDecidedBy());
		e.setDecidedOn(ZonedDateTime.now());
		return e;
	}

	private boolean isNotInvalid(CreditNoteEntity e) {
		return e.getIsValid() == null || e.getIsValid() == true;
	}

	private CreditNoteEntity setPaymentsAndRemarksAndBalance(CreditNoteEntity e, CreditNote c) {
		e.setPayments(payments(c));
		return setRemarksAndBalance(e, c);
	}

	private List<CreditNotePaymentEntity> payments(CreditNote c) {
		List<CreditNotePayment> l = c.getPayments();
		return l == null ? null : l.stream().map(p -> toEntity(p)).collect(Collectors.toList());
	}

	private CreditNotePaymentEntity toEntity(CreditNotePayment p) {
		CreditNotePaymentEntity e = new CreditNotePaymentEntity();
		e.setId(p.getId());
		e.setPaymentDate(p.getPaymentDate());
		e.setPaymentRemarks(p.getPaymentRemarks());
		e.setPaymentValue(p.getPaymentValue());
		e.setReference(p.getReference());
		return e;
	}

	private CreditNoteEntity invalidateTheRemittanceFromTheInvalidCreditNoteUnpayingAffectedBillings(CreditNoteEntity e) {
		RemittanceEntity r = remittanceService.findEntityByCheck("CREDIT MEMO", e.getId());
		if (r != null)
			remittanceService.updatePaymentBasedOnValidation( //
					"", //
					r.getId().toString(), //
					"false", //
					"INVALID C/N No. " + e.getId(), // 
					e.getDecidedBy(), //
					toTimestampText(e.getDecidedOn()));
		return e;
	}
}