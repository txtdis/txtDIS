package ph.txtdis.mgdc.ccbpi.service;

import static org.apache.log4j.Logger.getLogger;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.SalesItemVariance;
import ph.txtdis.type.ModuleType;

@Service("orderConfirmationListService")
public class OrderConfirmationListServiceImpl //
		extends AbstractListService //
		implements OrderConfirmationListService {

	private static Logger logger = getLogger(OrderConfirmationListServiceImpl.class);

	private boolean isBookingVariance;

	private String status;

	@Override
	public String getHeaderName() {
		return "OCS";
	}

	@Override
	public String getSubhead() {
		try {
			logger.info("\n    IsBookingVariance@getSubhead = " + isBookingVariance);
			return isBookingVariance ? super.getSubhead() : status + " OCS on " + date();
		} catch (Exception e) {
			return "";
		}
	}

	@Override
	public void listOCS(String[] ids) throws Exception {
		if (isBookingVariance(ids))
			listForBookingVariance(ids);
		else
			listForRemittanceVariance(ids);
	}

	private boolean isBookingVariance(String[] ids) {
		logger.info("\n    ModuleFromIds@isBookingVariance = " + module(ids));
		return isBookingVariance = module(ids).equalsIgnoreCase(ModuleType.SALES_ORDER.toString());
	}

	private void listForBookingVariance(String[] ids) throws Exception {
		item = item(itemVendorNo(ids));
		list = getList("/ocsList?itemVendorNo=" + itemVendorNo(ids) + "&route=" + route(ids) + "&start=" + startDate(ids) + "&end=" + endDate(ids));
	}

	private void listForRemittanceVariance(String[] ids) throws Exception {
		logger.info("\n    ColumnIdx@listForRemittanceVariance = " + columnIdx(ids));
		if (isUnpickedQty(ids))
			unpickedList(ids);
		else if (isLoadedQty(ids))
			loadedList(ids);
		else if (isDeliveredQty(ids))
			deliveredList(ids);
	}

	private boolean isUnpickedQty(String[] ids) {
		return columnIdx(ids) == 2;
	}

	private Integer columnIdx(String[] ids) {
		return Integer.valueOf(ids[1]);
	}

	private void unpickedList(String[] ids) throws Exception {
		status = "Unpicked";
		list = list("/unpickedList?route=" + route(ids) + "&start=" + startDate(ids) + "&end=" + endDate(ids));
		logger.info("\n    Route@unpickedList = " + route(ids));
		logger.info("\n    StartDate@unpickedList = " + startDate(ids));
		logger.info("\n    EndDate@unpickedList = " + endDate(ids));
		logger.info("\n    UnpickedList@unpickedList = " + list);
	}

	private List<SalesItemVariance> list(String endPt) throws Exception {
		return getListedReadOnlyService().module("remittanceVariance").getList(endPt);
	}

	private boolean isLoadedQty(String[] ids) {
		return columnIdx(ids) == 3;
	}

	private void loadedList(String[] ids) throws Exception {
		status = "Loaded-out";
		list = list("/loadedList?route=" + route(ids) + "&start=" + startDate(ids) + "&end=" + endDate(ids));
		logger.info("\n    Route@loadedList = " + route(ids));
		logger.info("\n    StartDate@loadedList = " + startDate(ids));
		logger.info("\n    EndDate@loadedList = " + endDate(ids));
		logger.info("\n    LoadedList@loadedList = " + list);
	}

	private boolean isDeliveredQty(String[] ids) {
		return columnIdx(ids) == 4;
	}

	private void deliveredList(String[] ids) throws Exception {
		status = "Delivered";
		list = list("/deliveredList?route=" + route(ids) + "&start=" + startDate(ids) + "&end=" + endDate(ids));
		logger.info("\n    Route@deliveredList = " + route(ids));
		logger.info("\n    StartDate@deliveredList = " + startDate(ids));
		logger.info("\n    EndDate@deliveredList = " + endDate(ids));
		logger.info("\n    DeliveredList@loadedList = " + list);
	}
}
