package ph.txtdis.mgdc.app;

import ph.txtdis.app.BillingApp;
import ph.txtdis.app.MultiTyped;
import ph.txtdis.dto.Billable;

public interface MultiTypeBillingApp //
		extends BillingApp, MultiTyped, OnlyOneOpenApp {

	void show(Billable b);
}
