package ph.txtdis.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javafx.stage.Stage;
import ph.txtdis.dto.InvoiceBooklet;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.table.InvoiceBookletTable;
import ph.txtdis.service.InvoiceBookletService;

@Lazy
@Component("invoiceBookletApp")
public class InvoiceBookletApp extends AbstractTableApp<InvoiceBookletTable, InvoiceBookletService, InvoiceBooklet> {

	@Autowired
	private AppButton invoiceBookletButton;

	public AppButton addButton(Stage stage) {
		invoiceBookletButton.icon("invoiceBooklet").tooltip("Issue booklet...").build();
		invoiceBookletButton.setOnAction(e -> addParent(stage).start());
		return invoiceBookletButton;
	}
}
