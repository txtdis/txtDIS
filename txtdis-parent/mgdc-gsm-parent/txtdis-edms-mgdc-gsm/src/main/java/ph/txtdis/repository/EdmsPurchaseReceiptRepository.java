package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ph.txtdis.domain.EdmsPurchaseReceipt;

@Repository("edmsPurchaseReceiptRepository")
public interface EdmsPurchaseReceiptRepository //
	extends CrudRepository<EdmsPurchaseReceipt, Long> {
}
