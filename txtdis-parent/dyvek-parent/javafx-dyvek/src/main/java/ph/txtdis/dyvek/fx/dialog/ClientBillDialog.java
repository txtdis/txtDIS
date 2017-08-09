package ph.txtdis.dyvek.fx.dialog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.fx.control.LabeledField;
import ph.txtdis.fx.dialog.AbstractFieldDialog;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.CURRENCY;
import static ph.txtdis.type.Type.QUANTITY;

@Scope("prototype")
@Component("clientBillDialog")
public class ClientBillDialog
	extends AbstractFieldDialog<BigDecimal> {

	@Autowired
	private LabeledField<BigDecimal> adjustmentQtyField, adjustmentPriceField;

	private BigDecimal adjustmentQty, adjustmentPrice;

	@Override
	protected List<InputNode<?>> addNodes() {
		return asList(
			adjustmentQtyField.name("Adjusting Qty").build(QUANTITY),
			adjustmentPriceField.name("Unit Price ").build(CURRENCY));
	}

	@Override
	protected BigDecimal createEntity() {
		adjustmentQty = adjustmentQtyField.getValue();
		adjustmentPrice = adjustmentPriceField.getValue();
		return null;
	}

	@Override
	public List<BigDecimal> getAddedItems() {
		return adjustmentQty == null ? null : asList(adjustmentQty, adjustmentPrice);
	}

	@Override
	protected String headerText() {
		return "Add Adjustments";
	}

	@Override
	protected void nullData() {
		adjustmentQty = null;
		adjustmentPrice = null;
		super.nullData();
	}
}