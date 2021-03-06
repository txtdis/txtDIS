package ph.txtdis.mgdc.gsm.printer;

import ph.txtdis.mgdc.gsm.domain.BillableDetailEntity;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.mgdc.printer.AbstractPrinter;
import ph.txtdis.mgdc.printer.NotPrintedException;

import java.io.IOException;
import java.math.BigDecimal;

import static org.apache.commons.lang3.StringUtils.center;
import static org.apache.commons.lang3.StringUtils.rightPad;
import static ph.txtdis.util.NumberUtils.toQuantityText;

public abstract class AbstractReturnedMaterialPrinter //
	extends AbstractPrinter<BillableEntity> //
	implements ReturnedMaterialPrinter {

	@Override
	protected void print() throws Exception {
		try {
			printSubheader();
			printDetails();
			printFooter();
		} catch (IOException e) {
			e.printStackTrace();
			throw new NotPrintedException();
		}
	}

	@Override
	protected void printSubheader() throws IOException {
		printLarge();
		println(center("RETURN MATERIAL AUTHORIZATION", LARGE_FONT_PAPER_WIDTH));
		print(center("RMA #" + entity.getBookingId() + " - " + entity.getCustomer().getName(), LARGE_FONT_PAPER_WIDTH));
		printNormal();
		println("");
		println(entity.getCustomer().getAddress());
		printDashes();
	}

	@Override
	protected void printDetails() throws IOException {
		for (BillableDetailEntity d : entity.getDetails())
			println(printItemQty(d) + "____  ____");
	}

	@Override
	protected void printFooter() throws IOException {
		println("PICK-UP APPROVAL:");
		println("__________________  __________________");
		println("       MGDC              CUSTOMER");
		println("");
		println("RETURN MATERIAL RECEIPT:");
		println("__________________  __________________");
		println("      DRIVER              CHECKER");
		println("");
		printEndOfPage();
	}

	private void println(String s) {
		printer.println(s);
	}

	private void print(String s) {
		printer.print(s);
	}

	private String printItemQty(BillableDetailEntity d) {
		return rightPad(itemQty(d) + uom(d) + itemName(d), 30);
	}

	private String itemQty(BillableDetailEntity d) {
		return toQuantityText(initialQty(d));
	}

	private String uom(BillableDetailEntity d) {
		return d.getUom() + " ";
	}

	private String itemName(BillableDetailEntity d) {
		return d.getItem().getName();
	}

	protected abstract BigDecimal initialQty(BillableDetailEntity d);
}
