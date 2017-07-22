package ph.txtdis.mgdc.gsm.printer;

import static ph.txtdis.util.NumberUtils.divide;

import java.io.IOException;
import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ph.txtdis.mgdc.gsm.domain.BillableDetailEntity;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.mgdc.gsm.domain.BomEntity;
import ph.txtdis.mgdc.gsm.service.server.ItemService;
import ph.txtdis.type.PartnerType;

@Component("pickListPrinter")
public class PickListPrinterImpl //
		extends AbstractPickListPrinter //
		implements QtyInFraction {

	@Autowired
	private ItemService itemService;

	@Override
	protected String itemQty(BomEntity b) {
		return qtyInFraction(itemService, b.getPart(), b.getQty().intValue());
	}

	@Override
	protected String itemQtyAndUomText(BillableDetailEntity d) {
		return qtyInFraction(itemService, d.getItem(), d.getFinalQty().intValue());
	}

	@Override
	protected void printBookings(BillableEntity b) throws IOException {
		if (!isAnExTruckLoadOrder(b))
			super.printBookings(b);
	}

	private boolean isAnExTruckLoadOrder(BillableEntity b) {
		return b.getCustomer().getType() == PartnerType.EX_TRUCK;
	}

	@Override
	protected void printLoadOrderNo() {
		BillableEntity b = entity.getBillings().get(0);
		if (isAnExTruckLoadOrder(b))
			printHuge("L/O #" + b.getBookingId());
	}

	@Override
	protected BigDecimal subtotalValue(BillableDetailEntity d) {
		return divide(super.subtotalValue(d), itemService.getQtyPerCase(d.getItem()));
	}
}
