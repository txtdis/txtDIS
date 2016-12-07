package ph.txtdis.printer;

import static ph.txtdis.util.NumberUtils.formatQuantity;

import java.io.IOException;
import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import ph.txtdis.domain.BillableEntity;
import ph.txtdis.domain.BillableDetailEntity;

import static org.apache.commons.lang3.StringUtils.center;
import static org.apache.commons.lang3.StringUtils.rightPad;

import static java.math.BigDecimal.ZERO;

@Component("returnedMaterialPrinter")
public class ReturnedMaterialPrinter extends AbstractPrinter<BillableEntity> {

	private BigDecimal initialQty(BillableDetailEntity d) {
		BigDecimal i = d.getInitialQty();
		return i == null ? ZERO : i;
	}

	private String itemName(BillableDetailEntity d) {
		return d.getItem().getName();
	}

	private String itemQty(BillableDetailEntity d) {
		return formatQuantity(initialQty(d));
	}

	private void print(String s) {
		printer.print(s);
	}

	private String printItemQty(BillableDetailEntity d) {
		return rightPad(itemQty(d) + uom(d) + itemName(d), 30);
	}

	private void println(String s) {
		printer.println(s);
	}

	private String uom(BillableDetailEntity d) {
		return d.getUom() + " ";
	}

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
}
