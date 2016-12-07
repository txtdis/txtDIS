package ph.txtdis.service;

import static org.apache.log4j.Logger.getLogger;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.BillableDetailEntity;
import ph.txtdis.domain.BomEntity;

@Service("pickListService")
public class PickListServiceImpl extends AbstractPickListService {

	private static Logger logger = getLogger(PickListServiceImpl.class);

	@Override
	protected BomEntity toBom(BillableDetailEntity d) {
		logger.info("\n    BillableDetailEntityBeforeBomCreation = " + d);
		return createBom(d.getItem(), d.getInitialQty());
	}
}