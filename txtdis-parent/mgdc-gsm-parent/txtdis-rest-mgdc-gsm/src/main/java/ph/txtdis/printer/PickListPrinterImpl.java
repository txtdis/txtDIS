package ph.txtdis.printer;

import java.io.IOException;
import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ph.txtdis.domain.BillableDetailEntity;
import ph.txtdis.domain.BillableEntity;
import ph.txtdis.domain.BomEntity;
import ph.txtdis.dto.PartnerType;
import ph.txtdis.service.ItemService;
import ph.txtdis.util.NumberUtils;

@Component("pickListPrinter")
public class PickListPrinterImpl extends AbstractPickListPrinter implements QtyInFraction {

	@Autowired
	private ItemService itemService;

	@Override
	protected String itemQty(BomEntity b) {
		return qtyInFraction(itemService, b.getPart(), b.getQty().intValue());
	}

	@Override
	protected String itemQtyAndUom(BillableDetailEntity d) {
		return qtyInFraction(itemService, d.getItem(), d.getFinalQty().intValue());
	}

	@Override
	protected void printBookings(BillableEntity b) throws IOException {
		if (isAnExTruckLoadOrder(b))
			super.printBookings(b);
	}

	private boolean isAnExTruckLoadOrder(BillableEntity b) {
		return b.getCustomer().getType() != PartnerType.EX_TRUCK;
	}

	@Override
	protected void printLoadOrderNo() {
		BillableEntity b = entity.getBillings().get(0);
		if (isAnExTruckLoadOrder(b))
			printHuge("L/O #" + b.getBookingId());
	}

	@Override
	protected BigDecimal subtotalValue(BillableDetailEntity d) {
		return NumberUtils.divide(super.subtotalValue(d), new BigDecimal(d.getQtyPerCase()));
	}
}
