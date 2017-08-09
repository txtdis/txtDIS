package ph.txtdis.mgdc.fx.dialog;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.PricingType;
import ph.txtdis.fx.dialog.AbstractNameListDialog;
import ph.txtdis.mgdc.service.PricingTypeService;

@Scope("prototype")
@Component("pricingTypeDialog")
public class PricingTypeDialog
	extends AbstractNameListDialog<PricingType, PricingTypeService> {

	@Override
	protected String headerText() {
		return "Add New Pricing Type";
	}
}
