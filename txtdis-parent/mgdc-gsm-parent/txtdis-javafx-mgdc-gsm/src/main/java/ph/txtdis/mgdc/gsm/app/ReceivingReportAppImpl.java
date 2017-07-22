package ph.txtdis.mgdc.gsm.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.binding.BooleanBinding;
import ph.txtdis.mgdc.app.ReceivingReportApp;
import ph.txtdis.mgdc.gsm.fx.table.ReceivingReportTable;

@Scope("prototype")
@Component("receivingReportApp")
public class ReceivingReportAppImpl
		extends AbstractReceivingReportApp<ReceivingReportTable> //
		implements ReceivingReportApp {

	@Override
	protected BooleanBinding isPosted() {
		return receivedOnDisplay.isNotEmpty();
	}
}
