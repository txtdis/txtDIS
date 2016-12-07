package ph.txtdis.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import ph.txtdis.domain.CreditNoteEntity;
import ph.txtdis.repository.SpunRepository;

@Repository("creditNoteRepository")
public interface CreditNoteRepository extends SpunRepository<CreditNoteEntity, Long> {

	List<CreditNoteEntity> findByOrderByIdAsc();
}
