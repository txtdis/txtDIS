package ph.txtdis.fx.dialog;

import static java.math.BigDecimal.ZERO;
import static java.util.Arrays.asList;
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
import ph.txtdis.service.BillableService;
import ph.txtdis.type.UomType;

@Scope("prototye")
@Component("billableDialog")
public class BillableDialogImpl extends AbstractAllItemInputDialog<BillableService, BillableDetail> {

	@Autowired
	private LabeledCombo<UomType> uomCombo;

	@Autowired
	private LabeledField<BigDecimal> qtyField;

	@Override
	protected List<InputNode<?>> addNodes() {
		List<InputNode<?>> l = super.addNodes();
		l.addAll(asList(uomCombo(), qtyField()));
		return l;
	}

	private LabeledCombo<UomType> uomCombo() {
		uomCombo.name("UOM").build();
		uomCombo.setOnAction(e -> qtyField.setValue(ZERO));
		return uomCombo;
	}

	private LabeledField<BigDecimal> qtyField() {
		qtyField.name("Quantity").build(QUANTITY);
		qtyField.setOnAction(e -> setUomAndQtyUponValidation());
		return qtyField;
	}

	@Override
	protected void updateIfItemIdIsValid() throws Exception {
		super.updateIfItemIdIsValid();
		//uomCombo.items(service.getSellingUoms());
	}

	@Override
	protected void verifyQty() throws Exception {
		//service.setQtyUponValidation(uomCombo.getValue(), qtyField.getValue());
	}
}
