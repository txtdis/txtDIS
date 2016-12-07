package ph.txtdis.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.binding.BooleanBinding;
import ph.txtdis.fx.table.ReceivingReportTable;

@Scope("prototype")
@Component("receivingReportApp")
public class ReceivingReportAppImpl //
		extends AbstractReceivingReportApp<ReceivingReportTable> //
		implements ReceivingReportApp {

	@Override
	protected BooleanBinding isPosted() {
		return receivedOnDisplay.isNotEmpty();
	}
}
