package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ph.txtdis.domain.EdmsPurchaseReceiptDetail;

import java.util.List;

@Repository("edmsPurchaseReceiptDetailRepository")
public interface EdmsPurchaseReceiptDetailRepository
	extends CrudRepository<EdmsPurchaseReceiptDetail, Short> {

	List<EdmsPurchaseReceiptDetail> findByReferenceNo(@Param("referenceNo") String r);
}
