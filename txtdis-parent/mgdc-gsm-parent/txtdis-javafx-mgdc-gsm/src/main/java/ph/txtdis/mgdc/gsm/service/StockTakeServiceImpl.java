package ph.txtdis.mgdc.gsm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ph.txtdis.dto.Billable;
import ph.txtdis.mgdc.service.VerifiedSalesOrderService;
import ph.txtdis.service.RestClientService;

@Service("stockTakeService")
public class StockTakeServiceImpl
	extends AbstractStockTakeService //
	implements StockTakeService,
	VerifiedLoadOrderService,
	VerifiedSalesOrderService {

	@Autowired
	private RestClientService<Billable> billableRestClientService;

	@Override
	protected void verifyAllCountDateTransactionsAreComplete() throws Exception {
		verifyAllPickedLoadOrdersHaveNoItemQuantityVariances(billableRestClientService, getCountDate());
		verifyAllPickedSalesOrderHaveBeenBilled(billableRestClientService, getCountDate());
	}
}