package ph.txtdis.service;

import org.springframework.stereotype.Service;

import ph.txtdis.domain.BillableEntity;
import ph.txtdis.dto.Billable;

@Service("purchaseReceiptService")
public class PurchaseReceiptServiceImpl extends AbstractPurchaseReceiptService {

	@Override
	public Billable findById(Long id) {
		BillableEntity b = repository.findByNumIdAndRmaNullAndCustomerId(id, vendorId());
		return toDTO(b);
	}
}