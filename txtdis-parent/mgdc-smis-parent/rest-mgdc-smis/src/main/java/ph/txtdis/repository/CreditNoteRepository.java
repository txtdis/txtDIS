package ph.txtdis.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import ph.txtdis.domain.CreditNote;

@Repository("creditNoteRepository")
public interface CreditNoteRepository extends SpunRepository<CreditNote, Long> {

	List<CreditNote> findByOrderByIdAsc();
}
