package ph.txtdis.mgdc.ccbpi.fx.table;

import javafx.scene.control.TableColumn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.mgdc.ccbpi.fx.dialog.LoadManifestDialog;
import ph.txtdis.mgdc.ccbpi.service.LoadManifestService;

import java.util.List;

import static java.util.Arrays.asList;

@Scope("prototype")
@Component("loadManifestTable")
public class LoadManifestTableImpl // 
	extends AbstractBeverageBillableTable<LoadManifestService, LoadManifestDialog> //
	implements LoadManifestTable {

	@Override
	protected List<TableColumn<BillableDetail, ?>> addColumns() {
		super.addColumns();
		return asList(itemVendorId, name, quality, uom, price, qtyColumn(), subtotal);
	}
}
