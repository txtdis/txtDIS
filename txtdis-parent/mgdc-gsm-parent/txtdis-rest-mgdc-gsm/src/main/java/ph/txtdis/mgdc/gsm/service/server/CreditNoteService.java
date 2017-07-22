package ph.txtdis.mgdc.gsm.service.server;

import java.util.List;

import ph.txtdis.dto.CreditNote;
import ph.txtdis.mgdc.gsm.domain.CreditNoteEntity;
import ph.txtdis.service.SpunSavedKeyedService;

public interface CreditNoteService
		extends SpunSavedKeyedService<CreditNoteEntity, CreditNote, Long> {

	List<CreditNote> findAllUnpaid();

	List<CreditNote> findAllUnvalidated();

	List<CreditNote> findAll();
}