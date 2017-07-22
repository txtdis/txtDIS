package ph.txtdis.mgdc.gsm.service.server;

import static ph.txtdis.type.PartnerType.VENDOR;

import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.mgdc.gsm.repository.PurchaseReceiptRepository;

public abstract class AbstractPurchaseReceiptService extends AbstractReceivingService //
		implements GsmPurchaseReceiptService {

	@Autowired
	private PurchaseReceiptRepository purchaseReceiptRepository;

	@Override
	protected BillableEntity firstEntity() {
		return purchaseReceiptRepository.findFirstByCustomerTypeAndReceivingIdNotNullOrderByIdAsc(VENDOR);
	}

	@Override
	protected BillableEntity nextEntity(Long id) {
		return purchaseReceiptRepository.findFirstByCustomerTypeAndReceivingIdNotNullAndIdGreaterThanOrderByIdAsc(VENDOR, id);
	}

	@Override
	protected BillableEntity lastEntity() {
		return purchaseReceiptRepository.findFirstByCustomerTypeAndReceivingIdNotNullOrderByIdDesc(VENDOR);
	}

	@Override
	protected BillableEntity previousEntity(Long id) {
		return purchaseReceiptRepository.findFirstByCustomerTypeAndReceivingIdNotNullAndIdLessThanOrderByIdDesc(VENDOR, id);
	}
}