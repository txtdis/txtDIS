package ph.txtdis.fx.dialog;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component("pricingDialog")
public class PricingDialogImpl extends AbstractPricingDialog {

	@Override
	protected void createPricingUponValidation() {
		if (startDatePicker.getValue() != null)
			try {
				price = itemService.createPricingUponValidation(//
						typeCombo.getValue(), //
						priceField.getValue(), //
						null, //
						startDatePicker.getValue());
			} catch (Exception e) {
				resetNodesOnError(e);
			}
	}
}