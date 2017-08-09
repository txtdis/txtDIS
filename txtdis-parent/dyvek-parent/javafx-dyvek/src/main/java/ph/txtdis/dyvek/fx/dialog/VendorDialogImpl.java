package ph.txtdis.dyvek.fx.dialog;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dyvek.service.VendorService;
import ph.txtdis.type.PartnerType;

@Scope("prototype")
@Component("vendorDialog")
public class VendorDialogImpl //
	extends AbstractCustomerDialog<VendorService> {

	@Override
	protected PartnerType partnerType() {
		return PartnerType.VENDOR;
	}

	@Override
	protected String name() {
		return "Supplier";
	}
}