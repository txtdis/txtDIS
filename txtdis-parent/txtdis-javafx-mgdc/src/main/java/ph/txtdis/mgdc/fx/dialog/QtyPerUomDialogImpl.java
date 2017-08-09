package ph.txtdis.mgdc.fx.dialog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.QtyPerUom;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.fx.control.LabeledCheckBox;
import ph.txtdis.fx.control.LabeledCombo;
import ph.txtdis.fx.control.LabeledField;
import ph.txtdis.fx.dialog.AbstractFieldDialog;
import ph.txtdis.mgdc.service.ValidatedUomService;
import ph.txtdis.type.UomType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.QUANTITY;
import static ph.txtdis.util.NumberUtils.isZero;

@Scope("prototype")
@Component("qtyPerUomDialog")
public class QtyPerUomDialogImpl //
	extends AbstractFieldDialog<QtyPerUom> {

	@Autowired
	private ValidatedUomService service;

	@Autowired
	private LabeledCombo<UomType> uomCombo;

	@Autowired
	private LabeledField<BigDecimal> qtyField;

	@Autowired
	private LabeledCheckBox isPurchasedCheckBox, isSoldCheckBox, isReportedCheckBox;

	@Override
	protected List<InputNode<?>> addNodes() {
		List<InputNode<?>> l = new ArrayList<>(asList(//
			uomCombo.name("UOM").items(service.listUoms()).build(), //
			qtyField(), //
			isReportedCheckBox()));
		if (service.isSold())
			l.add(2, isSoldCheckBox.name("Sold").build());
		if (service.isPurchased())
			l.add(2, isPurchasedCheckBox());
		return l;
	}

	private LabeledField<BigDecimal> qtyField() {
		qtyField.name("Qty Relative to 'PC'").build(QUANTITY);
		qtyField.onAction(e -> nullIfZero());
		return qtyField;
	}

	private LabeledCheckBox isReportedCheckBox() {
		isReportedCheckBox.name("Reported").build();
		isReportedCheckBox.onAction(e -> verifyReportedUom());
		return isReportedCheckBox;
	}

	private LabeledCheckBox isPurchasedCheckBox() {
		isPurchasedCheckBox.name("Purchased").build();
		isPurchasedCheckBox.onAction(e -> verifyPurchasedUom());
		return isPurchasedCheckBox;
	}

	private void nullIfZero() {
		if (isZero(qtyField.getValue()))
			qtyField.clear();
	}

	private void verifyReportedUom() {
		try {
			service.validateReportedUom(isReportedCheckBox.getValue());
		} catch (Exception e) {
			resetNodesOnError(e);
		}
	}

	private void verifyPurchasedUom() {
		try {
			service.validatePurchasedUom(isPurchasedCheckBox.getValue());
		} catch (Exception e) {
			resetNodesOnError(e);
		}
	}

	@Override
	protected QtyPerUom createEntity() {
		return service.createQtyPerUom(//
			uomCombo.getValue(), //
			qtyField.getValue(), //
			nullIfFalse(isPurchasedCheckBox.getValue()), //
			nullIfFalse(isSoldCheckBox.getValue()), //
			nullIfFalse(isReportedCheckBox.getValue()));
	}

	private Boolean nullIfFalse(boolean b) {
		return b ? b : null;
	}

	@Override
	protected String headerText() {
		return "Add New Qty per UOM";
	}
}
