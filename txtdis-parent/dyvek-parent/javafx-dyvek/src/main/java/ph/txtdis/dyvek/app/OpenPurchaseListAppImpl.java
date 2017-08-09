package ph.txtdis.dyvek.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.layout.HBox;
import ph.txtdis.dyvek.fx.table.PurchaseListTable;
import ph.txtdis.dyvek.service.PurchaseService;

@Scope("prototype")
@Component("openPurchaseOrderListApp")
public class OpenPurchaseListAppImpl
	extends AbstractOpenOrderListApp<PurchaseListTable, PurchaseService>
	implements OpenPurchaseListApp {

	@Override
	protected HBox tablePane() {
		return pane.centeredHorizontal(table.noStatusColumn().build());
	}
}
