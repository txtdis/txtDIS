package ph.txtdis.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.fx.table.BeverageBillingTable;
import ph.txtdis.service.BillingService;

@Scope("prototype")
@Component("billingApp")
public class BillingAppImpl //
		extends AbstractBillingApp<BillingService, BeverageBillingTable, String> //
		implements BillingApp {

	@Override
	protected void reset() {
		super.reset();
		if (service.isAnInvoice())
			idPrefixInput.requestFocus();
		else
			referenceIdInput.requestFocus();
	}

	@Override
	protected void setCustomerBoxBindings() {
		customerBox.disableIdInputIf(isPosted().or(referenceIdInput.isEmpty()));
		customerBox.setSearchButtonVisibleIfNot(isPosted());
	}

	@Override
	protected void setFocusAfterReferenceIdValidation() {
		if (referenceIdInput.isEmpty().get())
			referenceIdInput.requestFocus();
		else if (service.isAppendable())
			customerBox.requestFocus();
		else
			saveButton.requestFocus();
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		customerBox.setOnAction(e -> updateUponCustomerValidation());
	}

	private void updateUponCustomerValidation() {
		Long id = customerBox.getId();
		if (service.isNew() && id != null && id != 0)
			try {
				service.updateUponCustomerIdValidation(id);
			} catch (Exception e) {
				customerBox.handleError(e);
			} finally {
				refresh();
				customerBox.setFocusAfterCustomerValidation(table);
			}
	}
}
