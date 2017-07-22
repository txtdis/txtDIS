package ph.txtdis.mgdc.gsm.fx.dialog;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.CURRENCY;
import static ph.txtdis.type.Type.ID;
import static ph.txtdis.type.Type.TEXT;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.Billable;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.fx.control.LabeledField;
import ph.txtdis.fx.dialog.AbstractFieldDialog;
import ph.txtdis.mgdc.gsm.dto.Customer;
import ph.txtdis.mgdc.gsm.service.GsmBillingService;

@Scope("prototype")
@Component("invoiceValueAdjustmentDialog")
public class InvoiceValueAdjustmentDialog //
		extends AbstractFieldDialog<Billable> {

	@Autowired
	private GsmBillingService service;

	@Autowired
	private LabeledField<BigDecimal> adjustmentField;

	@Autowired
	private LabeledField<Long> idField;

	@Autowired
	private LabeledField<String> nameDisplay;

	@Override
	protected List<InputNode<?>> addNodes() {
		return asList(//
				idField(), //
				nameDisplay.name("Employee").readOnly().build(TEXT), //
				adjustmentField.name("Adjustment").build(CURRENCY)); //
	}

	private LabeledField<Long> idField() {
		idField.name("ID No.").build(ID);
		idField.onAction(e -> updateIfVerifiedIsAnEmployee(idField.getValue()));
		return idField;
	}

	private void updateIfVerifiedIsAnEmployee(Long id) {
		if (id != 0)
			try {
				Customer e = service.verifyIsAnEmployee(id);
				nameDisplay.setValue(e.getName());
			} catch (Exception e) {
				resetNodesOnError(e);
			}
	}

	@Override
	protected Billable createEntity() {
		return service.setAdjustmentData(idField.getValue(), adjustmentField.getValue());
	}

	@Override
	protected String headerText() {
		return "Create Adjusting D/R";
	}
}
