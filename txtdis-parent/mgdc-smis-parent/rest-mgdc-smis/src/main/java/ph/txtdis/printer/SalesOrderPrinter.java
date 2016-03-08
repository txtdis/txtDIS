package ph.txtdis.printer;

import static java.lang.Integer.valueOf;
import static ph.txtdis.util.NumberUtils.divide;
import static ph.txtdis.util.NumberUtils.formatQuantity;
import static ph.txtdis.util.NumberUtils.printDecimal;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.StringUtils.center;
import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.apache.commons.lang3.StringUtils.removeEnd;
import static org.apache.commons.lang3.StringUtils.rightPad;
import static org.apache.commons.lang3.StringUtils.substring;

import static java.math.BigDecimal.ZERO;

import static ph.txtdis.util.DateTimeUtils.toDateDisplay;

import ph.txtdis.domain.Billing;
import ph.txtdis.domain.BillingDetail;
import ph.txtdis.domain.Customer;
import ph.txtdis.domain.CustomerDiscount;
import ph.txtdis.util.NumberUtils;

@Component("salesOrderPrinter")
public class SalesOrderPrinter extends Printer<Billing> {

	private static final int HALF_COLUMN = (PAPER_WIDTH / 2) - 2;

	private static final int SUBHEADER_LABEL_WIDTH = 9;

	@Value("${invoice.line.item.count}")
	private String linesPerPage;

	@Value("${vat.percent}")
	private String vatValue;

	@Value("${client.initials}")
	private String clientInitials;

	private String barangay() {
		String s = customer().getBarangay() + ", " + customer().getCity();
		return substring(s, 0, PAPER_WIDTH - SUBHEADER_LABEL_WIDTH);
	}

	private Customer customer() {
		return entity.getCustomer();
	}

	private String customerName() {
		String s = customer().getName();
		return s.length() <= 21 ? s : substring(s, 0, 21);
	}

	private String discount() {
		return NumberUtils.isZero(discountValue()) ? "--" : printDecimal(discountValue());
	}

	private BigDecimal discountValue() {
		return entity.getGrossValue().subtract(entity.getTotalValue());
	}

	private String dueText() {
		LocalDate d = entity.getDueDate();
		return "DUE " + (d.isEqual(LocalDate.now()) ? "TODAY" : toDateDisplay(d));
	}

	private int getRemaingLines() {
		return linesPerPage() - entity.getDetails().size();
	}

	private String grossText() {
		return printDecimal(entity.getGrossValue());
	}

	private BigDecimal initialQty(BillingDetail d) {
		BigDecimal i = d.getInitialQty();
		return i == null ? ZERO : i;
	}

	private String itemName(BillingDetail d) {
		return rightPad(d.getItem().getName(), 19);
	}

	private String itemQty(BillingDetail d) {
		return leftPad(formatQuantity(netQty(d)), 3);
	}

	private int linesPerPage() {
		return valueOf(linesPerPage);
	}

	private BigDecimal netQty(BillingDetail d) {
		return initialQty(d).subtract(returnedQty(d));
	}

	private String percent() {
		return percents("", entity.getCustomerDiscounts());
	}

	private String percents(String s, List<CustomerDiscount> c) {
		for (CustomerDiscount d : c)
			s += d.getPercent() + "% * ";
		return removeEnd(s, " * ");
	}

	private String price(BillingDetail d) {
		return leftPad(priceText(d) + "@", 8);
	}

	private String priceText(BillingDetail d) {
		return printDecimal(d.getPriceValue());
	}

	private void println(String s) {
		printer.println(s);
	}

	private void printNothingFollowsFollowedByBlanks() {
		println(center("** NOTHING FOLLOWS **", PAPER_WIDTH));
		for (int i = 0; i < getRemaingLines(); i++)
			println("");
	}

	private BigDecimal returnedQty(BillingDetail d) {
		BigDecimal r = d.getReturnedQty();
		return r == null ? ZERO : r;
	}

	private String subtotalColumn(BillingDetail d) {
		return leftPad(subtotalText(d), 9);
	}

	private String subtotalText(BillingDetail d) {
		return printDecimal(subtotalValue(d));
	}

	private BigDecimal subtotalValue(BillingDetail d) {
		return d.getPriceValue().multiply(netQty(d));
	}

	private String total() {
		String s = printDecimal(entity.getTotalValue());
		return leftPad(s, 9);
	}

	private String uom(BillingDetail d) {
		return d.getUom() + " ";
	}

	private String vat() {
		BigDecimal vat = entity.getTotalValue().subtract(vatableValue());
		String s = printDecimal(vat);
		return leftPad(s, 9);
	}

	private String vatable() {
		String s = printDecimal(vatableValue());
		return leftPad(s, 9);
	}

	private BigDecimal vatableValue() {
		return divide(entity.getTotalValue(), new BigDecimal("1." + vatValue));
	}

	@Override
	protected void printDetails() {
		for (BillingDetail d : entity.getDetails())
			println(itemQty(d) + uom(d) + itemName(d) + price(d) + subtotalColumn(d));
		if (getRemaingLines() > 0)
			printNothingFollowsFollowedByBlanks();
	}

	@Override
	protected void printFooter() {
		println(leftPad("--------", PAPER_WIDTH));
		println(leftPad("TOTAL", 33) + leftPad(grossText(), 9));
		println(leftPad(percent() + " LESS", 24) + leftPad(discount(), 18));
		println(leftPad("VATABLE", 24) + vatable() + leftPad("--", 9));
		println(leftPad(vatValue + "% VAT", 24) + vat() + leftPad("--", 9));
		println(leftPad("--------", PAPER_WIDTH));
		println(leftPad("NET", 33) + total());
		println(leftPad("========", PAPER_WIDTH));
		println("");
		println(rightPad(center("PREPARED BY:", HALF_COLUMN), 4) + center("RECEIVED BY:", HALF_COLUMN));
		println(rightPad(leftPad("", HALF_COLUMN, "_"), 4) + leftPad("", HALF_COLUMN, "_"));
		println(rightPad(center(clientInitials, HALF_COLUMN), 4) + center(customerName(), HALF_COLUMN));
		println("");
		println("ORDER #" + entity.getId());
		printEndOfPage();
	}

	@Override
	protected void printSubheader() throws IOException {
		println("DATE   : " + toDateDisplay(entity.getOrderDate()));
		println("SOLD TO: " + customerName());
		println("ADDRESS: " + customer().getStreet());
		println("         " + barangay());
		printLarge();
		println(center(dueText(), HALF_COLUMN));
		printNormal();
		printDashes();
		println(center("PARTICULARS", PAPER_WIDTH));
		printDashes();
	}
}
