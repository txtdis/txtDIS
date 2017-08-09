package ph.txtdis.mgdc.gsm.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ph.txtdis.mgdc.gsm.domain.CreditNoteEntity;
import ph.txtdis.repository.SpunRepository;

import java.math.BigDecimal;
import java.util.List;

@Repository("creditNoteRepository")
public interface CreditNoteRepository
	extends SpunRepository<CreditNoteEntity, Long> {

	List<CreditNoteEntity> findByBalanceValueGreaterThanOrderByIdAsc( //
	                                                                  @Param("zeroBalance") BigDecimal z);

	List<CreditNoteEntity> findByIsValidNullOrderByIdAsc();

	List<CreditNoteEntity> findByOrderByIdAsc();
}
