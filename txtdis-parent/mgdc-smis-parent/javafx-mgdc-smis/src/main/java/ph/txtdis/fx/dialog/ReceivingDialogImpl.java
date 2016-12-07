package ph.txtdis.fx.dialog;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.ENUM;
import static ph.txtdis.type.Type.QUANTITY;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.BillableDetail;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.fx.control.LabeledCombo;
import ph.txtdis.fx.control.LabeledField;
import ph.txtdis.service.ReceivingService;
import ph.txtdis.type.UomType;

@Scope("prototype")
@Component("receivingDialog")
public class ReceivingDialogImpl extends AbstractReceivingDialog<ReceivingService, BillableDetail> {

	@Autowired
	private LabeledField<UomType> uomDisplay;

	@Autowired
	private LabeledField<BigDecimal> qtyField;

	@Override
	protected LabeledCombo<String> itemCombo() {
		super.itemCombo();
		itemCombo.setOnAction(e -> setUom());
		return itemCombo;
	}

	private void setUom() {
		String i = itemCombo.getValue();
		if (i == null)
			return;
		//		UomType u = service.getUomOfSelectedItem(i);
		//		uomDisplay.setValue(u);
	}

	private LabeledField<BigDecimal> qtyField() {
		return qtyField.name("Quantity").build(QUANTITY);
	}

	private LabeledField<UomType> uomDisplay() {
		return uomDisplay.name("UOM").readOnly().build(ENUM);
	}

	@Override
	protected List<InputNode<?>> addNodes() {
		List<InputNode<?>> l = super.addNodes();
		l.addAll(asList(uomDisplay(), qtyField()));
		return l;
	}

	@Override
	protected BillableDetail createEntity() {
		return service.updateReceivingDetailReturnedQty(qtyField.getValue());
	}
}
