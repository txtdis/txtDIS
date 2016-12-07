package ph.txtdis.fx.table;

import static ph.txtdis.type.Type.ENUM;
import static ph.txtdis.type.Type.QUANTITY;
import static ph.txtdis.type.Type.TEXT;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.CustomerVolumePromo;
import ph.txtdis.fx.dialog.CustomerVolumePromoDialog;
import ph.txtdis.type.UomType;

@Scope("prototype")
@Component("customerVolumePromoTable")
public class CustomerVolumePromoTable extends AbstractTableView<CustomerVolumePromo> {

	@Autowired
	private Column<CustomerVolumePromo, String> item;

	@Autowired
	private Column<CustomerVolumePromo, UomType> uom;

	@Autowired
	private Column<CustomerVolumePromo, BigDecimal> targetQty, freeQty;

	@Autowired
	private DecisionNeededTableControls<CustomerVolumePromo> decisionNeeded;

	@Autowired
	private CustomerVolumePromoDialog dialog;

	@Override
	@SuppressWarnings("unchecked")
	protected void addColumns() {
		getColumns().setAll(//
				item.ofType(TEXT).width(120).build("Item", "item"), //
				uom.ofType(ENUM).width(80).build("UOM", "uom"), //
				targetQty.ofType(QUANTITY).width(80).build("Target\nQuantity", "targetQty"),
				freeQty.ofType(QUANTITY).build("Free\nQuantity", "freeQty"));
		getColumns().addAll(decisionNeeded.addColumns());
	}

	@Override
	protected void addProperties() {
		decisionNeeded.addContextMenu(this, dialog);
	}
}
