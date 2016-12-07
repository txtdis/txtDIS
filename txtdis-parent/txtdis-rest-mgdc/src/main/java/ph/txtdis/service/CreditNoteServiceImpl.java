package ph.txtdis.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import ph.txtdis.domain.CreditNoteEntity;
import ph.txtdis.domain.CreditNotePaymentEntity;
import ph.txtdis.dto.CreditNote;
import ph.txtdis.dto.CreditNotePayment;
import ph.txtdis.repository.CreditNoteRepository;

@Service("creditNoteService")
public class CreditNoteServiceImpl extends AbstractSpunService<CreditNoteRepository, CreditNoteEntity, CreditNote, Long>
		implements CreditNoteService {

	@Override
	public List<CreditNote> listCreditNotes() {
		List<CreditNoteEntity> cnel = repository.findByOrderByIdAsc();
		return convert(cnel);
	}

	private List<CreditNote> convert(List<CreditNoteEntity> cnel) {
		if (cnel == null)
			return null;
		return cnel.stream().map(cne -> toDTO(cne)).collect(Collectors.toList());
	}

	@Override
	protected CreditNote toDTO(CreditNoteEntity e) {
		CreditNote c = new CreditNote();
		c.setId(e.getId());
		c.setBalanceValue(e.getBalanceValue());
		c.setCreatedBy(e.getCreatedBy());
		c.setCreatedOn(e.getCreatedOn());
		c.setCreditDate(e.getCreditDate());
		c.setDescription(e.getDescription());
		c.setLastModifiedBy(e.getLastModifiedBy());
		c.setLastModifiedOn(e.getLastModifiedOn());
		c.setPayments(toPayments(e.getPayments()));
		c.setRemarks(e.getRemarks());
		c.setTotalValue(e.getTotalValue());
		return c;
	}

	private List<CreditNotePayment> toPayments(List<CreditNotePaymentEntity> payments) {
		if (payments == null)
			return null;
		return payments.stream().map(p -> convert(p)).collect(Collectors.toList());
	}

	private CreditNotePayment convert(CreditNotePaymentEntity e) {
		CreditNotePayment p = new CreditNotePayment();
		p.setId(e.getId());
		p.setPaymentDate(e.getPaymentDate());
		p.setPaymentRemarks(e.getPaymentRemarks());
		p.setPaymentValue(e.getPaymentValue());
		p.setReference(e.getReference());
		return p;
	}

	@Override
	protected CreditNoteEntity toEntity(CreditNote t) {
		CreditNoteEntity c = new CreditNoteEntity();
		c.setId(t.getId());
		c.setBalanceValue(t.getBalanceValue());
		c.setCreatedBy(t.getCreatedBy());
		c.setCreatedOn(t.getCreatedOn());
		c.setCreditDate(t.getCreditDate());
		c.setDescription(t.getDescription());
		c.setLastModifiedBy(t.getLastModifiedBy());
		c.setLastModifiedOn(t.getLastModifiedOn());
		c.setPayments(toPaymentEntities(t.getPayments()));
		c.setRemarks(t.getRemarks());
		c.setTotalValue(t.getTotalValue());
		return c;
	}

	private List<CreditNotePaymentEntity> toPaymentEntities(List<CreditNotePayment> payments) {
		if (payments == null)
			return null;
		return payments.stream().map(p -> convert(p)).collect(Collectors.toList());
	}

	private CreditNotePaymentEntity convert(CreditNotePayment p) {
		CreditNotePaymentEntity e = new CreditNotePaymentEntity();
		e.setId(p.getId());
		e.setPaymentDate(p.getPaymentDate());
		e.setPaymentRemarks(p.getPaymentRemarks());
		e.setPaymentValue(p.getPaymentValue());
		e.setReference(p.getReference());
		return e;
	}
}