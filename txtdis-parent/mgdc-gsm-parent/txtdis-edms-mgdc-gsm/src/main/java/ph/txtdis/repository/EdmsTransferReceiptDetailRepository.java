package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ph.txtdis.domain.EdmsTransferReceiptDetail;

@Repository("edmsTransferReceiptDetailRepository")
public interface EdmsTransferReceiptDetailRepository
	extends CrudRepository<EdmsTransferReceiptDetail, Long> {
}
