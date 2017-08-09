package ph.txtdis.mgdc.gsm.service;

import ph.txtdis.dto.StockTake;
import ph.txtdis.dto.StockTakeDetail;
import ph.txtdis.service.*;
import ph.txtdis.type.QualityType;

import java.time.LocalDate;
import java.util.List;

public interface StockTakeService
	extends ItemBasedService<StockTakeDetail>,
	ResettableService,
	RemarkedAndSpunAndSavedAndOpenDialogAndTitleAndHeaderAndIconAndModuleNamedAndResettableAndTypeMappedService<Long>,
	ItemInputtedService<StockTakeDetail>,
	QtyPerUomService,
	QuantityValidated {

	StockTake getByBooking(Long id) throws Exception;

	LocalDate getCountDate();

	@Override
	default String getItemName() {
		return ItemBasedService.super.getItemName();
	}

	StockTake getLatestCount();

	LocalDate getLatestCountDate();

	StockTake getPreviousCount();

	LocalDate getPreviousCountDate();

	boolean isCountToday();

	boolean isUserAStockTaker();

	List<String> listCheckers();

	List<String> listItemsOnStock() throws Exception;

	List<String> listTakers();

	List<String> listWarehouses();

	StockTake openByDate(String dateText) throws Exception;

	void setChecker(String checkerName);

	void setDetails(List<StockTakeDetail> details);

	void setQuality(QualityType quality);

	void setTaker(String takerName);

	void setWarehouseIfAllCountDateTransactionsAreCompleteAndNoStockTakeAlreadyMadeOnCountDate(String warehouse)
		throws Exception;
}