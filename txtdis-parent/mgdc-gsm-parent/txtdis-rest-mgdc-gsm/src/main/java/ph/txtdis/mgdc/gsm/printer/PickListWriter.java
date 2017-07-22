package ph.txtdis.mgdc.gsm.printer;

import static java.math.BigDecimal.ZERO;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.apache.commons.lang3.StringUtils.removeEnd;
import static org.apache.commons.lang3.StringUtils.rightPad;
import static ph.txtdis.type.DeliveryType.PICK_UP;
import static ph.txtdis.util.DateTimeUtils.toDateDisplay;
import static ph.txtdis.util.NumberUtils.divide;
import static ph.txtdis.util.NumberUtils.isZero;
import static ph.txtdis.util.NumberUtils.printDecimal;
import static ph.txtdis.util.NumberUtils.printInteger;
import static ph.txtdis.util.NumberUtils.toQuantityText;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ph.txtdis.domain.TruckEntity;
import ph.txtdis.mgdc.gsm.domain.BillableDetailEntity;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.mgdc.gsm.domain.BomEntity;
import ph.txtdis.mgdc.gsm.domain.CustomerDiscountEntity;
import ph.txtdis.mgdc.gsm.domain.CustomerEntity;
import ph.txtdis.mgdc.gsm.domain.ItemEntity;
import ph.txtdis.mgdc.gsm.domain.PickListEntity;
import ph.txtdis.mgdc.printer.NotPrintedException;

@Component("pickListWriter")
public class PickListWriter {

	private PickListEntity entity;

	private FileWriter writer;

	@Value("${vat.percent}")
	private String vatValue;

	public void print(PickListEntity entity) throws Exception {
		this.entity = entity;
		writer = new FileWriter("/Users/MGDC/Desktop/pick.txt");
		print();
		writer.flush();
		writer.close();
	}

	private CustomerEntity customer(BillableEntity b) {
		return b.getCustomer();
	}

	private String discount(BillableEntity b) {
		return isZero(discountValue(b)) ? "--" : printDecimal(discountValue(b));
	}

	private BigDecimal discountValue(BillableEntity b) {
		return b.getGrossValue().subtract(b.getTotalValue());
	}

	private String grossText(BillableEntity b) {
		return printDecimal(b.getGrossValue());
	}

	private BigDecimal initialQty(BillableDetailEntity d) {
		BigDecimal i = d.getInitialQty();
		return i == null ? ZERO : i;
	}

	private String itemName(BillableDetailEntity d) {
		return d.getItem().getName();
	}

	private String itemName(BomEntity b) {
		return rightPad(b.getPart().getName(), 25);
	}

	private String itemQty(BillableDetailEntity d) {
		return toQuantityText(netQty(d));
	}

	private String itemQty(BomEntity b) {
		return leftPad(printInteger(b.getQty()) + " ", 5);
	}

	private BigDecimal netQty(BillableDetailEntity d) {
		return initialQty(d).subtract(returnedQty(d));
	}

	private String percent(BillableEntity b) {
		return percents("", b.getCustomerDiscounts());
	}

	private String percents(String s, List<CustomerDiscountEntity> c) {
		for (CustomerDiscountEntity d : c)
			s += d.getValue() + "% * ";
		return removeEnd(s, " * ");
	}

	private String priceText(BillableDetailEntity d) {
		return printDecimal(d.getPriceValue()) + "@";
	}

	private void printBookings(BillableEntity b) throws IOException {
		printDateAndTruck();
		writer.append("S/O #" + b.getBookingId() + ": " + customer(b).getName());
		writer.append("\r\n");
		writer.append(customer(b).getAddress());
		writer.append("\r\n");
		printDueToday(b);
		for (BillableDetailEntity d : b.getDetails()) {
			writer.append(printItemQty(d) + printPrice(d) + printSubtotal(d));
			writer.append("\r\n");
			writer.append(StringUtils.leftPad("____  ____ not4invoice", 6));
			writer.append("\r\n");
		}
		writer.append("TOTAL=" + grossText(b) + "  " + "DISCOUNT(" + percent(b) + ")=" + discount(b));
		writer.append("\r\n");
		writer.append("VATABLE=" + vatable(b) + "  " + "VAT=" + vat(b) + "  " + "NET=" + total(b));
		writer.append("\r\n");
	}

	private void printDateAndTruck() throws IOException {
		writer.append(toDateDisplay(entity.getPickDate()) + " - " + truck());
		writer.append("\r\n");
	}

	private void printDueToday(BillableEntity b) throws IOException {
		if (b.getDueDate().isEqual(b.getOrderDate())) {
			writer.append("NGAYON DIN ANG BAYAD");
		} else {
			writer.append("");
		}
		writer.append("\r\n");
	}

	private String printItemQty(BillableDetailEntity d) {
		return rightPad(itemQty(d) + uom(d) + itemName(d), 22);
	}

	private String printPrice(BillableDetailEntity d) {
		return leftPad(priceText(d), 8);
	}

	private void printSalesOrders() throws IOException {
		for (BillableEntity b : entity.getBillings())
			printBookings(b);
	}

	private String printSubtotal(BillableDetailEntity d) {
		return leftPad(subtotalText(d), 9);
	}

	private void printSummary() throws IOException {
		printDateAndTruck();
		for (BomEntity b : summaryOfQuantitiesPerItem()) {
			writer.append(itemName(b) + itemQty(b) + "____  ____");
			writer.append("\r\n");
		}
	}

	private BigDecimal returnedQty(BillableDetailEntity d) {
		BigDecimal r = d.getReturnedQty();
		return r == null ? ZERO : r;
	}

	private String subtotalText(BillableDetailEntity d) {
		return printDecimal(subtotalValue(d));
	}

	private BigDecimal subtotalValue(BillableDetailEntity d) {
		return d.getPriceValue().multiply(netQty(d));
	}

	private List<BomEntity> summaryOfQuantitiesPerItem() {
		return entity.getBillings().stream()//
				.map(BillableEntity::getDetails).flatMap(List::stream)//
				.map(d -> toBoms(d)).flatMap(List::stream)//
				.collect(groupingBy(BomEntity::getPart, mapping(BomEntity::getQty, reducing(ZERO, BigDecimal::add)))) //
				.entrySet().stream()//
				.map(e -> toBom(e)).collect(toList());
	}

	private BomEntity toBom(BillableDetailEntity d) {
		return toBom(d.getItem(), d.getInitialQty());
	}

	private BomEntity toBom(Entry<ItemEntity, BigDecimal> e) {
		return toBom(e.getKey(), e.getValue());
	}

	private BomEntity toBom(ItemEntity i, BigDecimal qty) {
		BomEntity b = new BomEntity();
		b.setPart(i);
		b.setQty(qty);
		return b;
	}

	private List<BomEntity> toBoms(BillableDetailEntity d) {
		List<BomEntity> boms = d.getItem().getBoms();
		return boms.isEmpty() ? asList(toBom(d)) : toQuantifiedBoms(boms, d.getInitialQty());
	}

	private List<BomEntity> toQuantifiedBoms(List<BomEntity> boms, BigDecimal qty) {
		boms.forEach(b -> b.setQty(b.getQty().multiply(qty)));
		return boms;
	}

	private String total(BillableEntity b) {
		return printDecimal(b.getTotalValue());
	}

	private String truck() {
		TruckEntity t = entity.getTruck();
		return t == null ? PICK_UP.toString() : t.getName();
	}

	private String uom(BillableDetailEntity d) {
		return d.getUom() + " ";
	}

	private String vat(BillableEntity b) {
		BigDecimal vat = b.getTotalValue().subtract(vatableValue(b));
		return printDecimal(vat);
	}

	private String vatable(BillableEntity b) {
		return printDecimal(vatableValue(b));
	}

	private BigDecimal vatableValue(BillableEntity b) {
		return divide(b.getTotalValue(), new BigDecimal("1." + vatValue));
	}

	protected void print() throws Exception {
		try {
			printSummary();
			printSalesOrders();
		} catch (IOException e) {
			e.printStackTrace();
			throw new NotPrintedException();
		}
	}
}
