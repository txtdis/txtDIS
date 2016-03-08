package ph.txtdis.printer;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.toList;
import static ph.txtdis.util.NumberUtils.divide;
import static ph.txtdis.util.NumberUtils.formatQuantity;
import static ph.txtdis.util.NumberUtils.isZero;
import static ph.txtdis.util.NumberUtils.printDecimal;
import static ph.txtdis.util.NumberUtils.printInteger;
import static ph.txtdis.util.SpringUtils.username;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.StringUtils.center;
import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.apache.commons.lang3.StringUtils.removeEnd;
import static org.apache.commons.lang3.StringUtils.rightPad;

import static java.math.BigDecimal.ZERO;

import static ph.txtdis.util.DateTimeUtils.toDateDisplay;

import ph.txtdis.domain.Billing;
import ph.txtdis.domain.BillingDetail;
import ph.txtdis.domain.Bom;
import ph.txtdis.domain.Customer;
import ph.txtdis.domain.CustomerDiscount;
import ph.txtdis.domain.Item;
import ph.txtdis.domain.Picking;
import ph.txtdis.domain.Truck;
import ph.txtdis.repository.BillingRepository;

@Component("pickListPrinter")
public class PickListPrinter extends Printer<Picking> {

	@Autowired
	private BillingRepository repository;

	@Value("${vat.percent}")
	private String vatValue;

	private Customer customer(Billing b) {
		return b.getCustomer();
	}

	private String discount(Billing b) {
		return isZero(discountValue(b)) ? "--" : printDecimal(discountValue(b));
	}

	private BigDecimal discountValue(Billing b) {
		return b.getGrossValue().subtract(b.getTotalValue());
	}

	private Long familyId(Bom a) {
		return a.getPart().getFamily().getId();
	}

	private String grossText(Billing b) {
		return printDecimal(b.getGrossValue());
	}

	private BigDecimal initialQty(BillingDetail d) {
		BigDecimal i = d.getInitialQty();
		return i == null ? ZERO : i;
	}

	private boolean isCOD(Billing b) {
		return b.getDueDate().isEqual(b.getOrderDate());
	}

	private String itemName(BillingDetail d) {
		return d.getItem().getName();
	}

	private String itemName(Bom b) {
		return rightPad(b.getPart().getName(), 25);
	}

	private String itemQty(BillingDetail d) {
		return formatQuantity(netQty(d));
	}

	private String itemQty(Bom b) {
		return leftPad(printInteger(b.getQty()) + " ", 5);
	}

	private BigDecimal netQty(BillingDetail d) {
		return initialQty(d).subtract(returnedQty(d));
	}

	private String percent(Billing b) {
		return percents("", b.getCustomerDiscounts());
	}

	private String percents(String s, List<CustomerDiscount> c) {
		for (CustomerDiscount d : c)
			s += d.getPercent() + "% * ";
		return removeEnd(s, " * ");
	}

	private String pickDate() {
		return toDateDisplay(entity.getPickDate());
	}

	private String priceText(BillingDetail d) {
		return printDecimal(d.getPriceValue()) + "@";
	}

	private void printBookingDetail(Billing b) {
		for (BillingDetail d : b.getDetails()) {
			println(printItemQty(d) + printPrice(d) + printSubtotal(d));
			println(StringUtils.leftPad("____  ____ not4invoice", 6));
		}
	}

	private void printBookingFooter(Billing b) {
		println("TOTAL=" + grossText(b) + "  " + "DISCOUNT(" + percent(b) + ")=" + discount(b));
		println("VATABLE=" + vatable(b) + "  " + "VAT=" + vat(b) + "  " + "NET=" + total(b));
		printEndOfPage();
	}

	private void printBookingHeader(Billing b) throws IOException {
		printFourLines();
		printDateAndTruck();
		println("S/O #" + b.getBookingId() + ": " + customer(b).getName());
		println(customer(b).getAddress());
		printReminder(b);
		printDashes();
	}

	private void printBookings(Billing b) throws IOException {
		printBookingHeader(b);
		printBookingDetail(b);
		printBookingFooter(b);
		savePrintData(b);
	}

	private void printCashOnDeliveryReminder() {
		printHuge("NGAYON DIN ANG BAYAD");
	}

	private void printDateAndTruck() throws IOException {
		printLarge();
		printHuge(pickDate() + " - " + truck());
		printNormal();
	}

	private void printHuge(String s) {
		println(center(s, LARGE_FONT_PAPER_WIDTH));
	}

	private String printItemQty(BillingDetail d) {
		return rightPad(itemQty(d) + uom(d) + itemName(d), 22);
	}

	private void println(String s) {
		printer.println(s);
	}

	private void printPickList() {
		for (Bom b : summaryOfQuantitiesPerItem()) {
			println(itemName(b) + itemQty(b) + "____  ____");
		}
	}

	private String printPrice(BillingDetail d) {
		return leftPad(priceText(d), 8);
	}

	private void printReminder(Billing b) throws IOException {
		printLarge();
		if (b.getRma() != null)
			printReturnOrderReminder();
		else if (isCOD(b))
			printCashOnDeliveryReminder();
		else
			println("");
		printNormal();
	}

	private void printReturnOrderReminder() {
		printHuge("DI BABAYARAN KUNG");
		printHuge("IBA'NG IN-APPROVE");
		printHuge("SA ISINAULI");
	}

	private void printReturnOrders() throws IOException {
		for (Billing b : returnOrders())
			printBookings(b);
	}

	private void printSalesOrders() throws IOException {
		for (Billing b : entity.getBillings())
			printBookings(b);
	}

	private String printSubtotal(BillingDetail d) {
		return leftPad(subtotalText(d), 9);
	}

	private void printSummary() throws IOException {
		printDateAndTruck();
		printDashes();
		printPickList();
		printEndOfPage();
	}

	private BigDecimal returnedQty(BillingDetail d) {
		BigDecimal r = d.getReturnedQty();
		return r == null ? ZERO : r;
	}

	private List<Billing> returnOrders() {
		List<Customer> customers = entity.getBillings().stream().map(Billing::getCustomer).distinct().collect(toList());
		List<Billing> billings = repository
				.findByPrintedOnNullAndRmaNotNullAndIsValidTrueAndOrderDateNullAndCustomerIn(customers);
		return billings != null ? billings : emptyList();
	}

	private void savePrintData(Billing b) {
		b.setPrintedBy(username());
		b.setPrintedOn(ZonedDateTime.now());
		repository.save(b);
	}

	private String subtotalText(BillingDetail d) {
		return printDecimal(subtotalValue(d));
	}

	private BigDecimal subtotalValue(BillingDetail d) {
		return d.getPriceValue().multiply(netQty(d));
	}

	private List<Bom> summaryOfQuantitiesPerItem() {
		return entity.getBillings().stream()//
				.map(Billing::getDetails).flatMap(List::stream)//
				.map(d -> toBoms(d)).flatMap(List::stream)//
				.collect(groupingBy(Bom::getPart, mapping(Bom::getQty, reducing(ZERO, BigDecimal::add)))) //
				.entrySet().stream().map(e -> toBom(e))//
				.sorted((a, b) -> familyId(a).compareTo(familyId(b)))//
				.collect(toList());
	}

	private Bom toBom(BillingDetail d) {
		return toBom(d.getItem(), d.getUnitQty());
	}

	private Bom toBom(Entry<Item, BigDecimal> e) {
		return toBom(e.getKey(), e.getValue());
	}

	private Bom toBom(Item i, BigDecimal qty) {
		Bom b = new Bom();
		b.setPart(i);
		b.setQty(qty);
		return b;
	}

	private List<Bom> toBoms(BillingDetail d) {
		List<Bom> boms = d.getItem().getBoms();
		return boms.isEmpty() ? asList(toBom(d)) : toQuantifiedBoms(boms, d.getUnitQty());
	}

	private List<Bom> toQuantifiedBoms(List<Bom> boms, BigDecimal qty) {
		boms.forEach(b -> b.setQty(b.getQty().multiply(qty)));
		return boms;
	}

	private String total(Billing b) {
		return printDecimal(b.getTotalValue());
	}

	private String truck() {
		Truck t = entity.getTruck();
		return t == null ? "PICK-UP" : t.getName();
	}

	private String uom(BillingDetail d) {
		return d.getUom() + " ";
	}

	private String vat(Billing b) {
		BigDecimal vat = b.getTotalValue().subtract(vatableValue(b));
		return printDecimal(vat);
	}

	private String vatable(Billing b) {
		return printDecimal(vatableValue(b));
	}

	private BigDecimal vatableValue(Billing b) {
		return divide(b.getTotalValue(), new BigDecimal("1." + vatValue));
	}

	@Override
	protected void print() throws Exception {
		try {
			printSummary();
			printReturnOrders();
			printSalesOrders();
			printFourLines();
		} catch (IOException e) {
			e.printStackTrace();
			throw new NotPrintedException();
		}
	}

	@Override
	protected void printDetails() throws IOException {
	}

	@Override
	protected void printFooter() throws IOException {
	}

	@Override
	protected void printSubheader() throws IOException {
	}
}
