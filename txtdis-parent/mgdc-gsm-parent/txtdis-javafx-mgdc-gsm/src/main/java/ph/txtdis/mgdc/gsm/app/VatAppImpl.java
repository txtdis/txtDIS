package ph.txtdis.mgdc.gsm.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.app.AbstractTotaledReportApp;
import ph.txtdis.fx.dialog.OpenByDateDialog;
import ph.txtdis.mgdc.gsm.dto.Vat;
import ph.txtdis.mgdc.gsm.fx.table.VatTable;
import ph.txtdis.mgdc.gsm.service.VatService;

@Scope("prototype")
@Component("vatApp")
public class VatAppImpl //
	extends AbstractTotaledReportApp<VatTable, VatService, Vat> //
	implements VatApp {

	@Autowired
	private OpenByDateDialog openDialog;

	@Override
	protected void displayOpenByDateDialog() {
		openDialog.header("List a Month's VAT");
		openDialog.prompt("Enter a date of the desired month");
		openDialog.addParent(this).start();
	}

	@Override
	protected int noOfTotalDisplays() {
		return 3;
	}

	@Override
	protected void setDates() {
		service.setStartDate(openDialog.getDate());
	}
}
