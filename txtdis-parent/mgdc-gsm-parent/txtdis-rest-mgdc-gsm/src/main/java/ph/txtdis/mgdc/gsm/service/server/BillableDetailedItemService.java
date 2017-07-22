package ph.txtdis.mgdc.gsm.service.server;

import ph.txtdis.dto.BillableDetail;
import ph.txtdis.mgdc.gsm.domain.ItemEntity;

public interface BillableDetailedItemService //
		extends ItemService {

	ItemEntity findEntity(BillableDetail billableDetail);
}