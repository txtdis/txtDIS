package ph.txtdis.mgdc.ccbpi.printer;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.center;
import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.apache.commons.lang3.StringUtils.rightPad;
import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.type.DeliveryType.PICK_UP;
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

import ph.txtdis.domain.TruckEntity;
import ph.txtdis.mgdc.ccbpi.domain.BillableDetailEntity;
import ph.txtdis.mgdc.ccbpi.domain.BillableEntity;
import ph.txtdis.mgdc.ccbpi.domain.BomEntity;
import ph.txtdis.mgdc.ccbpi.domain.CustomerEntity;
import ph.txtdis.mgdc.ccbpi.domain.PickListEntity;
import ph.txtdis.mgdc.ccbpi.repository.BillableRepository;
import ph.txtdis.mgdc.ccbpi.service.server.PickListService;
import ph.txtdis.mgdc.printer.AbstractPrinter;
import ph.txtdis.mgdc.printer.NotPrintedException;

public abstract class AbstractPickListPrinter //
		extends AbstractPrinter<PickListEntity> //
		implements PickListPrinter {

	private static Logger logger = getLogger(AbstractPickListPrinter.class);

	@Autowired
	private BillableRepository repository;

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

	private String truck() {
		TruckEntity t = entity.getTruck();
		return t == null ? PICK_UP.toString() : t.getName();
	}

	private String pickDate() {
		return toDateDisplay(entity.getPickDate());
	}

	protected void printHuge(String s) {
		println(center(s, LARGE_FONT_PAPER_WIDTH));
	}

	protected void println(String s) {
		printer.println(s);
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
		logger.info("\n    ItemNameToPick = " + b.getPart().getName());
		return rightPad(b.getPart().getName(), 18);
	}

	protected abstract String itemQty(BomEntity b);

	private void printReturnOrders() throws IOException {
		for (BillableEntity b : returnOrders())
			printBookings(b);
	}

	private List<BillableEntity> returnOrders() {
		List<CustomerEntity> customers = entity.getBillings().stream().map(BillableEntity::getCustomer).distinct().collect(toList());
		List<BillableEntity> billings = repository.findByPickingPrintedOnNullAndRmaNotNullAndOrderDateNullAndCustomerIn(customers);
		return billings != null ? billings : emptyList();
	}

	private void printReminder(BillableEntity b) throws IOException {
		printLarge();
		if (b.getRma() != null)
			printReturnOrderReminder();
		else if (isCOD(b))
			printCashOnDeliveryReminder();
		printNormal();
	}

	private boolean isCOD(BillableEntity b) {
		return b.getDueDate().isEqual(b.getOrderDate());
	}

	private void printReturnOrderReminder() {
		printHuge("DI BABAYARAN KUNG");
		printHuge("IBA'NG IN-APPROVE");
		printHuge("SA ISINAULI");
	}

	private void printCashOnDeliveryReminder() {
		printHuge("NGAYON DIN ANG BAYAD");
	}

	private String itemAndQtyText(BillableDetailEntity d) {
		return rightPad(itemQtyAndUomText(d) + itemName(d), 22);
	}

	protected abstract String itemQtyAndUomText(BillableDetailEntity d);

	private String itemName(BillableDetailEntity d) {
		return d.getItem().getName();
	}

	protected void printBookingTotals(BillableEntity b) {
		println(StringUtils.leftPad("TOTAL = " + grossText(b), 40));
	}

	protected String grossText(BillableEntity b) {
		return StringUtils.leftPad(printDecimal(b.getTotalValue()), 10);
	}

	private String vatablePromptText(BillableEntity b) {
		return StringUtils.rightPad("VATABLE = " + vatableText(b), 22);
	}

	private String vatableText(BillableEntity b) {
		return printDecimal(vatableValue(b));
	}

	private BigDecimal vatableValue(BillableEntity b) {
		return divide(b.getTotalValue(), new BigDecimal("1." + vatValue));
	}

	private String vatPromptText(BillableEntity b) {
		return "VAT = " + vatText(b);
	}

	private String vatText(BillableEntity b) {
		return StringUtils.leftPad(printDecimal(vatValue(b)), 12);
	}

	private BigDecimal vatValue(BillableEntity b) {
		return b.getTotalValue().subtract(vatableValue(b));
	}

	private void printSalesOrders() throws IOException {
		for (BillableEntity b : entity.getBillings())
			printBookings(b);
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
		printReminder(b);
		printDashes();
	}

	private void printBookingDetail(BillableEntity b) {
		for (BillableDetailEntity d : b.getDetails()) {
			println(itemAndQtyText(d) + priceText(d) + subtotalText(d));
			println(leftPad("____  ____ not4invoice", 6));
		}
	}

	private String priceText(BillableDetailEntity d) {
		return leftPad(printDecimal(d.getPriceValue()) + "@", 8);
	}

	private String subtotalText(BillableDetailEntity d) {
		return leftPad(printDecimal(subtotalValue(d)), 10);
	}

	protected BigDecimal subtotalValue(BillableDetailEntity d) {
		return d.getPriceValue().multiply(d.getFinalQty());
	}

	private void printBookingFooter(BillableEntity b) {
		printBookingTotals(b);
		println(vatablePromptText(b) + vatPromptText(b));
		printEndOfPage();
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
