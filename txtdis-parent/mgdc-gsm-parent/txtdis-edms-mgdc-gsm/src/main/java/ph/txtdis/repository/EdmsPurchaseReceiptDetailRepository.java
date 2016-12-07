package ph.txtdis.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.EdmsPurchaseReceiptDetail;

@Repository("edmsPurchaseReceiptDetailRepository")
public interface EdmsPurchaseReceiptDetailRepository extends CrudRepository<EdmsPurchaseReceiptDetail, Short> {

	List<EdmsPurchaseReceiptDetail> findByReferenceNo(@Param("referenceNo") String r);
}
