package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ph.txtdis.domain.EdmsCreditMemo;

@Repository("edmsCreditMemoRepository")
public interface EdmsCreditMemoRepository
	extends CrudRepository<EdmsCreditMemo, Long> {

	EdmsCreditMemo findByReferenceNo(@Param("referenceNo") String r);

	EdmsCreditMemo findFirstByOrderByIdDesc();
}
