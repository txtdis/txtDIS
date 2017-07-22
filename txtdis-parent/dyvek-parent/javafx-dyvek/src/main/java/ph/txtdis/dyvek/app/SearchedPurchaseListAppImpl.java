package ph.txtdis.dyvek.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dyvek.fx.table.PurchaseListTable;
import ph.txtdis.dyvek.service.PurchaseService;

@Scope("prototype")
@Component("searchedPurchaseListApp")
public class SearchedPurchaseListAppImpl //
		extends AbstractSearchedOrderListApp<PurchaseListTable, PurchaseService> //
		implements SearchedPurchaseListApp {
}
