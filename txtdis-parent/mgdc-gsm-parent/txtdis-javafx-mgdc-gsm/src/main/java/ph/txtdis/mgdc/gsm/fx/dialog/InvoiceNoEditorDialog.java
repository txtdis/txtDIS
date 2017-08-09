package ph.txtdis.mgdc.gsm.fx.dialog;

import javafx.beans.binding.BooleanBinding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.Billable;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.fx.control.LabeledField;
import ph.txtdis.fx.dialog.AbstractFieldDialog;
import ph.txtdis.info.Information;
import ph.txtdis.mgdc.gsm.service.GsmBillingService;

import java.util.List;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.CODE;
import static ph.txtdis.type.Type.ID;

@Scope("prototype")
@Component("invoiceNoEditorDialog")
public class InvoiceNoEditorDialog
	extends AbstractFieldDialog<Billable> {

	@Autowired
	private GsmBillingService service;

	@Autowired
	private LabeledField<String> prefixField, suffixField;

	@Autowired
	private LabeledField<Long> idField;

	@Override
	protected List<InputNode<?>> addNodes() {
		return asList(
			prefixField.name("S/I Code").build(CODE),
			idField.name("S/I No.").build(ID),
			suffixField());
	}

	private LabeledField<String> suffixField() {
		suffixField.name("S/I Series").width(40).build(CODE);
		suffixField.onAction(e -> updateUponInvoiceNoValidation());
		return suffixField;
	}

	private void updateUponInvoiceNoValidation() {
		try {
			service.setOrderNoAndRemarksBeforeInvoiceNoEdit();
			service.updateUponOrderNoValidation(prefixField.getValue(), idField.getValue(), suffixField.getValue());
		} catch (Exception e) {
			resetNodesOnError(e);
		}
	}

	@Override
	protected Billable createEntity() {
		try {
			service.updateRemarksAfterInvoiceNoEdit();
			service.save();
		} catch (Information e) {
			messageDialog().show(e).addParent(this).start();
		} catch (Exception e) {
			resetNodesOnError(e);
		}
		return null;
	}

	@Override
	protected BooleanBinding getAddButtonDisableBindings() {
		return idField.isEmpty();
	}

	@Override
	protected String headerText() {
		return "Input Changes";
	}
}
