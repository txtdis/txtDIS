package ph.txtdis.printer;

import static org.apache.commons.lang3.StringUtils.rightPad;
import static org.apache.log4j.Logger.getLogger;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ph.txtdis.domain.BillableDetailEntity;
import ph.txtdis.domain.BillableEntity;
import ph.txtdis.domain.BomEntity;
import ph.txtdis.domain.PickListDetailEntity;
import ph.txtdis.service.ItemService;

@Component("pickListPrinter")
public class PickListPrinterImpl extends AbstractPickListPrinter implements QtyInFraction {

	private static Logger logger = getLogger(PickListPrinterImpl.class);

	@Autowired
	private ItemService itemService;

	@Override
	protected String itemQty(BomEntity b) {
		return "";
	}

	@Override
	protected String itemQtyAndUom(BillableDetailEntity d) {
		return "";
	}

	@Override
	protected void printBookings(BillableEntity b) throws IOException {
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
		return qtyInFraction(itemService, b.getItem(), b.getPickedQty());
	}
}
