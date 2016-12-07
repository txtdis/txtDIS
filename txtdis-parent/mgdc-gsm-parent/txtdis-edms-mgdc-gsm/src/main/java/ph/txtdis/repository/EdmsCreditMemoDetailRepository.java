package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.EdmsCreditMemoDetail;

@Repository("edmsCreditMemoDetailRepository")
public interface EdmsCreditMemoDetailRepository extends CrudRepository<EdmsCreditMemoDetail, Long> {
}
