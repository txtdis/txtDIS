package ph.txtdis.dyvek.fx.dialog;

import javafx.print.PageLayout;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppCombo;
import ph.txtdis.fx.control.AppLabel;
import ph.txtdis.fx.dialog.AbstractInputDialog;
import ph.txtdis.fx.pane.AppGridPane;

import java.util.List;

import static java.util.Arrays.asList;
import static javafx.collections.FXCollections.observableArrayList;
import static javafx.print.PageOrientation.PORTRAIT;
import static javafx.print.Paper.NA_LETTER;
import static javafx.print.Printer.MarginType.HARDWARE_MINIMUM;
import static javafx.print.Printer.getAllPrinters;
import static javafx.print.Printer.getDefaultPrinter;

@Scope("prototype")
@Component("voucherPrintingDialog")
public class VoucherPrintingDialog
	extends AbstractInputDialog {

	@Autowired
	private AppCombo<Printer> printerCombo;

	private AppLabel status;

	private WritableImage image;

	@Override
	protected List<AppButton> buttons() {
		return asList(printButton(), closeButton());
	}

	private AppButton printButton() {
		AppButton b = button.large("Print").build();
		b.onAction(event -> setOnClickedPrintButton());
		return b;
	}

	private void setOnClickedPrintButton() {
		Printer printer = printerCombo.getValue();
		PageLayout pageLayout = printer.createPageLayout(NA_LETTER, PORTRAIT, HARDWARE_MINIMUM);
		ImageView imageView = imageView(pageLayout);
		print(pageLayout, imageView);
	}

	private ImageView imageView(PageLayout pageLayout) {
		ImageView imageView = new ImageView(image);
		imageView.setFitWidth(pageLayout.getPrintableWidth());
		imageView.setPreserveRatio(true);
		return imageView;
	}

	private void print(PageLayout pageLayout, ImageView imageView) {
		PrinterJob job = PrinterJob.createPrinterJob();
		status.textProperty().bind(job.jobStatusProperty().asString());
		if (job.printPage(pageLayout, imageView))
			job.endJob();
	}

	@Override
	public void goToDefaultFocus() {
		printerCombo.requestFocus();
	}

	@Override
	protected String headerText() {
		return "Check Voucher";
	}

	@Override
	protected List<Node> nodes() {
		AppGridPane grid = pane.grid();
		grid.add(label.name("Printing Status"), 0, 0);
		grid.add(status(), 1, 0);
		grid.add(printerCombo(), 0, 1, 2, 1);
		return asList(header(), grid, buttonBox());
	}

	private AppLabel status() {
		status = label.name("PRINTER SELECTION");
		return status;
	}

	private Node printerCombo() {
		printerCombo.items(observableArrayList(getAllPrinters()));
		printerCombo.select(getDefaultPrinter());
		return printerCombo;
	}

	public AbstractInputDialog toPrint(WritableImage image) {
		this.image = image;
		return this;
	}
}
