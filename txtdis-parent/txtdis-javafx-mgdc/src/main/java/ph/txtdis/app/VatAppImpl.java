package ph.txtdis.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.Vat;
import ph.txtdis.fx.dialog.OpenByDateDialog;
import ph.txtdis.fx.table.VatTable;
import ph.txtdis.service.VatService;

@Lazy
@Component("vatApp")
public class VatAppImpl extends AbstractTotaledReportApp<VatTable, VatService, Vat> implements VatApp {

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
		return 2;
	}

	@Override
	protected void setDates() {
		service.setStartDate(openDialog.getDate());
	}
}
