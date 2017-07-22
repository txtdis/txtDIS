package ph.txtdis.dyvek.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dyvek.fx.table.SalesListTable;
import ph.txtdis.dyvek.service.SalesService;

@Scope("prototype")
@Component("searchedSalesListApp")
public class SearchedSalesListAppImpl //
		extends AbstractSearchedOrderListApp<SalesListTable, SalesService> //
		implements SearchedSalesListApp {
}
