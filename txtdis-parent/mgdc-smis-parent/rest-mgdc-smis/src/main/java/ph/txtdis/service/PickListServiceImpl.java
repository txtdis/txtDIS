package ph.txtdis.service;

import org.springframework.stereotype.Service;

import ph.txtdis.domain.BillableDetailEntity;
import ph.txtdis.domain.BomEntity;

@Service("pickListService")
public class PickListServiceImpl extends AbstractPickListService {

	@Override
	protected BomEntity toBom(BillableDetailEntity d) {
		return createBom(d.getItem(), d.getUnitQty());
	}
}