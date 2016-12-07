package ph.txtdis.printer;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.center;
import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.apache.commons.lang3.StringUtils.rightPad;
import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.util.DateTimeUtils.toDateDisplay;
import static ph.txtdis.util.NumberUtils.divide;
import static ph.txtdis.util.NumberUtils.printDecimal;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import ph.txtdis.domain.BillableDetailEntity;
import ph.txtdis.domain.BillableEntity;
import ph.txtdis.domain.BomEntity;
import ph.txtdis.domain.CustomerEntity;
import ph.txtdis.domain.PickListEntity;
import ph.txtdis.domain.TruckEntity;
import ph.txtdis.repository.BillingRepository;
import ph.txtdis.service.PickListService;

public abstract class AbstractPickListPrinter extends AbstractPrinter<PickListEntity> implements PickListPrinter {

	private static Logger logger = getLogger(AbstractPickListPrinter.class);

	@Autowired
	private BillingRepository repository;

	@Autowired
	private PickListService pickListService;

	@Value("${vat.percent}")
	private String vatValue;

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

	private void printSummary() throws IOException {
		printDateAndTruck();
		printDashes();
		printPickList();
		printEndOfPage();
	}

	private void printDateAndTruck() throws IOException {
		printLarge();
		printHuge(pickDate() + " - " + truck());
		printLoadOrderNo();
		printNormal();
	}

	protected void printHuge(String s) {
		println(center(s, LARGE_FONT_PAPER_WIDTH));
	}

	protected void println(String s) {
		printer.println(s);
	}

	private String pickDate() {
		return toDateDisplay(entity.getPickDate());
	}

	private String truck() {
		TruckEntity t = entity.getTruck();
		return t == null ? "PICK-UP" : t.getName();
	}

	protected void printLoadOrderNo() {
	}

	protected void printPickList() {
		List<BomEntity> boms = pickListService.summaryOfQuantitiesPerItem(entity);
		logger.info("\n    BomEntityList = " + boms);
		for (BomEntity b : boms)
			println(itemName(b) + itemQty(b) + "____  ____");
	}

	private String itemName(BomEntity b) {
		return rightPad(b.getPart().getName(), 18);
	}

	protected abstract String itemQty(BomEntity b);

	private void printReturnOrders() throws IOException {
		for (BillableEntity b : returnOrders())
			printBookings(b);
	}

	private List<BillableEntity> returnOrders() {
		List<CustomerEntity> customers = entity.getBillings().stream().map(BillableEntity::getCustomer).distinct()
				.collect(toList());
		List<BillableEntity> billings = repository
				.findByPickingPrintedOnNullAndRmaNotNullAndIsValidTrueAndOrderDateNullAndCustomerIn(customers);
		return billings != null ? billings : emptyList();
	}

	protected void printBookings(BillableEntity b) throws IOException {
		printBookingHeader(b);
		printBookingDetail(b);
		printBookingFooter(b);
	}

	private void printBookingHeader(BillableEntity b) throws IOException {
		printFourLines();
		printDateAndTruck();
		println("S/O #" + b.getBookingId() + ": " + b.getCustomer().getName());
		println(b.getCustomer().getAddress());
		printReminder(b);
		printDashes();
	}

	private void printReminder(BillableEntity b) throws IOException {
		printLarge();
		if (b.getRma() != null)
			printReturnOrderReminder();
		else if (isCOD(b))
			printCashOnDeliveryReminder();
		printNormal();
	}

	private void printReturnOrderReminder() {
		printHuge("DI BABAYARAN KUNG");
		printHuge("IBA'NG IN-APPROVE");
		printHuge("SA ISINAULI");
	}

	private boolean isCOD(BillableEntity b) {
		return b.getDueDate().isEqual(b.getOrderDate());
	}

	private void printCashOnDeliveryReminder() {
		printHuge("NGAYON DIN ANG BAYAD");
	}

	private void printBookingDetail(BillableEntity b) {
		for (BillableDetailEntity d : b.getDetails()) {
			println(itemAndQty(d) + price(d) + subtotal(d));
			println(StringUtils.leftPad("____  ____ not4invoice", 6));
		}
	}

	private String itemAndQty(BillableDetailEntity d) {
		return rightPad(itemQtyAndUom(d) + itemName(d), 22);
	}

	protected abstract String itemQtyAndUom(BillableDetailEntity d);

	private String itemName(BillableDetailEntity d) {
		return d.getItem().getName();
	}

	private String price(BillableDetailEntity d) {
		return leftPad(printDecimal(d.getPriceValue()) + "@", 8);
	}

	private String subtotal(BillableDetailEntity d) {
		return leftPad(printDecimal(subtotalValue(d)), 10);
	}

	protected BigDecimal subtotalValue(BillableDetailEntity d) {
		return d.getPriceValue().multiply(d.getQty());
	}

	private void printBookingFooter(BillableEntity b) {
		printBookingTotals(b);
		println(vatableText(b) + vatText(b));
		printEndOfPage();
	}

	protected void printBookingTotals(BillableEntity b) {
		println(StringUtils.leftPad("TOTAL = " + grossText(b), 40));
	}

	private String grossText(BillableEntity b) {
		return StringUtils.leftPad(printDecimal(b.getGrossValue()), 10);
	}

	private String vatableText(BillableEntity b) {
		return StringUtils.rightPad("VATABLE = " + vatable(b), 22);
	}

	private String vatable(BillableEntity b) {
		return printDecimal(vatableValue(b));
	}

	private BigDecimal vatableValue(BillableEntity b) {
		return divide(b.getTotalValue(), new BigDecimal("1." + vatValue));
	}

	private String vatText(BillableEntity b) {
		return "VAT = " + vat(b);
	}

	private String vat(BillableEntity b) {
		return StringUtils.leftPad(printDecimal(vatValue(b)), 12);
	}

	private BigDecimal vatValue(BillableEntity b) {
		return b.getTotalValue().subtract(vatableValue(b));
	}

	private void printSalesOrders() throws IOException {
		for (BillableEntity b : entity.getBillings())
			printBookings(b);
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
