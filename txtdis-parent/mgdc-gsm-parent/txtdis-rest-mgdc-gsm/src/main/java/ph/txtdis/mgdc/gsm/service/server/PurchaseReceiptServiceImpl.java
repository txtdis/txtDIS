package ph.txtdis.mgdc.gsm.service.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ph.txtdis.dto.Billable;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.mgdc.gsm.repository.NoPurchaseOrderReceiptRepository;
import ph.txtdis.service.RestClientService;

import java.util.List;

import static ph.txtdis.type.PartnerType.VENDOR;

@Service("purchaseReceiptService")
public class PurchaseReceiptServiceImpl //
	extends AbstractPurchaseReceiptService //
	implements GsmPurchaseReceiptService {

	@Autowired
	private NoPurchaseOrderReceiptRepository noPurchaseOrderReceiptRepository;

	@Autowired
	private RestClientService<Billable> restClientService;

	@Override
	public Billable findByPrimaryKey(Long id) {
		BillableEntity b = noPurchaseOrderReceiptRepository.findByNumIdAndRmaNullAndCustomerType(id, VENDOR);
		return toModel(b);
	}

	@Override
	public List<Billable> list() {
		List<BillableEntity> l = noPurchaseOrderReceiptRepository.findByCustomerTypeOrderByIdAsc(VENDOR);
		return toModels(l);
	}

	@Override
	@Transactional
	public Billable save(Billable t) {
		try {
			t = super.save(t);
			return saveToEdms(t);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Billable saveToEdms(Billable t) throws Exception {
		restClientService.module("billable").save(t);
		return t;
	}
}