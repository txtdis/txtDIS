package ph.txtdis.dyvek.fx.dialog;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.CURRENCY;
import static ph.txtdis.type.Type.QUANTITY;
import static ph.txtdis.type.Type.TEXT;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.fx.control.InputNode;
import ph.txtdis.fx.control.LabeledDatePicker;
import ph.txtdis.fx.control.LabeledField;
import ph.txtdis.fx.dialog.AbstractFieldDialog;

@Scope("prototype")
@Component("vendorBillDialog")
public class VendorBillDialogImpl //
		extends AbstractFieldDialog<String> {

	@Autowired
	private LabeledField<String> billNoField;

	@Autowired
	private LabeledDatePicker billDatePicker;

	@Autowired
	private LabeledField<BigDecimal> adjustmentQtyField, adjustmentPriceField;

	private String billNo, billDate, adjustmentQty, adjustmentPrice;

	@Override
	protected List<InputNode<?>> addNodes() {
		return asList( //
				billNoField.name("Bill No.").width(110).build(TEXT), //
				billDatePicker.name("Bill Date"), //
				adjustmentQtyField.name("Adjusting Qty").build(QUANTITY), //
				adjustmentPriceField.name("Unit Price ").build(CURRENCY));
	}

	@Override
	protected String createEntity() {
		billNo = billNoField.getValue();
		billDate = billDatePicker.getValue().toString();
		adjustmentQty = adjustmentQtyField.getValue().toPlainString();
		adjustmentPrice = adjustmentPriceField.getValue().toPlainString();
		return entity;
	}

	@Override
	public List<String> getAddedItems() {
		return billNo == null ? null : asList(billNo, billDate, adjustmentQty, adjustmentPrice);
	}

	@Override
	protected String headerText() {
		return "Input Bill Data";
	}

	@Override
	protected void nullData() {
		billNo = null;
		billDate = null;
		adjustmentQty = null;
		adjustmentPrice = null;
		super.nullData();
	}
}