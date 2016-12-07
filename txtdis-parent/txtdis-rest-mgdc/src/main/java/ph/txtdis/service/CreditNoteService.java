package ph.txtdis.service;

import java.util.List;

import ph.txtdis.dto.CreditNote;

public interface CreditNoteService extends SpunService<CreditNote, Long> {

	List<CreditNote> listCreditNotes();
}