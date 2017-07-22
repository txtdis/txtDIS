package ph.txtdis.mgdc.ccbpi.printer;

import static org.apache.commons.lang3.StringUtils.rightPad;
import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.type.OrderConfirmationType.MANUAL;
import static ph.txtdis.type.OrderConfirmationType.UNDELIVERED;
import static ph.txtdis.type.OrderConfirmationType.WAREHOUSE;
import static ph.txtdis.util.NumberUtils.printDecimal;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ph.txtdis.mgdc.ccbpi.domain.BillableDetailEntity;
import ph.txtdis.mgdc.ccbpi.domain.BillableEntity;
import ph.txtdis.mgdc.ccbpi.domain.BomEntity;
import ph.txtdis.mgdc.ccbpi.domain.PickListDetailEntity;
import ph.txtdis.mgdc.ccbpi.service.server.ItemService;
import ph.txtdis.type.OrderConfirmationType;
import ph.txtdis.util.NumberUtils;

@Component("pickListPrinter")
public class PickListPrinterImpl //
		extends AbstractPickListPrinter //
		implements QtyInFraction {

	private static Logger logger = getLogger(PickListPrinterImpl.class);

	@Autowired
	private ItemService itemService;

	@Override
	protected String grossText(BillableEntity b) {
		return StringUtils.leftPad(printDecimal(b.getTotalValue()), 10);
	}

	@Override
	protected String itemQty(BomEntity b) {
		return "";
	}

	@Override
	protected String itemQtyAndUomText(BillableDetailEntity d) {
		return "";
	}

	@Override
	protected void printBookings(BillableEntity b) throws IOException {
		if (isBillingNeeded(b))
			super.printBookings(b);
	}

	private boolean isBillingNeeded(BillableEntity b) {
		return isType(b, WAREHOUSE) || isType(b, UNDELIVERED) || isType(b, MANUAL) || isPickUp(b);
	}

	private boolean isType(BillableEntity b, OrderConfirmationType type) {
		return b.getPrefix().equalsIgnoreCase(type.toString());
	}

	private boolean isPickUp(BillableEntity b) {
		return b.getPicking().getTruck() == null;
	}

	@Override
	protected void printLoadOrderNo() {
		printHuge("P/L #" + entity.getId());
	}

	@Override
	protected void printPickList() {
		List<PickListDetailEntity> d = entity.getDetails();
		logger.info("\n    PickListDetails = " + d);
		for (PickListDetailEntity b : d)
			println(itemName(b) + itemQty(b) + "____  ____");
	}

	private String itemName(PickListDetailEntity b) {
		return rightPad(b.getItem().getName(), 18);
	}

	private String itemQty(PickListDetailEntity b) {
		return qtyInFraction(itemService, b.getItem(), b.getInitialQty().intValue());
	}

	@Override
	protected BigDecimal subtotalValue(BillableDetailEntity d) {
		return d.getPriceValue().multiply(qtyPerCase(d));
	}

	private BigDecimal qtyPerCase(BillableDetailEntity d) {
		return NumberUtils.divide(d.getFinalQty(), itemService.getQtyPerCase(d.getItem()));
	}
}
