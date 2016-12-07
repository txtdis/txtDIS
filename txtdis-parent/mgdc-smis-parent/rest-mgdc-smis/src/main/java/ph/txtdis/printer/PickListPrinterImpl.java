package ph.txtdis.printer;

import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.apache.commons.lang3.StringUtils.removeEnd;
import static ph.txtdis.util.NumberUtils.formatQuantity;
import static ph.txtdis.util.NumberUtils.isZero;
import static ph.txtdis.util.NumberUtils.printDecimal;
import static ph.txtdis.util.NumberUtils.printInteger;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import ph.txtdis.domain.BillableDetailEntity;
import ph.txtdis.domain.BillableEntity;
import ph.txtdis.domain.BomEntity;
import ph.txtdis.domain.CustomerDiscountEntity;

@Component("pickListPrinter")
public class PickListPrinterImpl extends AbstractPickListPrinter {

	@Override
	protected String itemQty(BomEntity b) {
		return leftPad(printInteger(b.getQty()) + " ", 5);
	}

	@Override
	protected String itemQtyAndUom(BillableDetailEntity d) {
		return itemQty(d) + uom(d);
	}

	private String itemQty(BillableDetailEntity d) {
		return formatQuantity(d.getQty());
	}

	private String uom(BillableDetailEntity d) {
		return d.getUom() + " ";
	}

	@Override
	protected void printBookingTotals(BillableEntity b) {
		super.printBookingTotals(b);
		println(discountText(b) + net(b));
	}

	private String discountText(BillableEntity b) {
		return StringUtils.rightPad("DISCOUNT(" + percent(b) + ") = " + discount(b), 20);
	}

	private String percent(BillableEntity b) {
		return percents("", b.getCustomerDiscounts());
	}

	private String percents(String s, List<CustomerDiscountEntity> c) {
		for (CustomerDiscountEntity d : c)
			s += d.getPercent() + "% * ";
		return removeEnd(s, " * ");
	}

	private String discount(BillableEntity b) {
		BigDecimal discountValue = b.getGrossValue().subtract(b.getTotalValue());
		return isZero(discountValue) ? "--" : printDecimal(discountValue);
	}

	private String net(BillableEntity b) {
		return StringUtils.leftPad("NET   = " + total(b), 20);
	}

	private String total(BillableEntity b) {
		return StringUtils.leftPad(printDecimal(b.getTotalValue()), 12);
	}
}
