package ph.txtdis.fx.table;

import static ph.txtdis.type.Type.CURRENCY;
import static ph.txtdis.type.Type.ENUM;
import static ph.txtdis.type.Type.QUANTITY;
import static ph.txtdis.type.Type.TEXT;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.CustomerVolumeDiscount;
import ph.txtdis.fx.dialog.CustomerVolumeDiscountDialog;
import ph.txtdis.type.UomType;

@Scope("prototype")
@Component("customerVolumeDiscountTable")
public class CustomerVolumeDiscountTable extends AbstractTableView<CustomerVolumeDiscount> {

	@Autowired
	private Column<CustomerVolumeDiscount, String> item;

	@Autowired
	private Column<CustomerVolumeDiscount, UomType> uom;

	@Autowired
	private Column<CustomerVolumeDiscount, BigDecimal> targetQty, discount;

	@Autowired
	private DecisionNeededTableControls<CustomerVolumeDiscount> decisionNeeded;

	@Autowired
	private CustomerVolumeDiscountDialog dialog;

	@Override
	@SuppressWarnings("unchecked")
	protected void addColumns() {
		getColumns().setAll(//
				item.ofType(TEXT).width(120).build("Item", "item"), //
				uom.ofType(ENUM).width(80).build("UOM", "uom"), //
				targetQty.ofType(QUANTITY).width(80).build("Target\nQuantity", "targetQty"),
				discount.ofType(CURRENCY).build("Discount", "discountValue"));
		getColumns().addAll(decisionNeeded.addColumns());
	}

	@Override
	protected void addProperties() {
		decisionNeeded.addContextMenu(this, dialog);
	}
}
