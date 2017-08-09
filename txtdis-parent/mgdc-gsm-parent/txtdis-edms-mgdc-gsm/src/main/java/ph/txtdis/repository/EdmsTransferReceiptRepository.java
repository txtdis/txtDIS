package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ph.txtdis.domain.EdmsTransferReceipt;

@Repository("edmsTransferReceiptRepository")
public interface EdmsTransferReceiptRepository
	extends CrudRepository<EdmsTransferReceipt, Long> {
}
