package ph.txtdis.mgdc.gsm.service;

import java.time.LocalDate;
import java.util.List;

import ph.txtdis.dto.StockTake;
import ph.txtdis.dto.StockTakeDetail;
import ph.txtdis.service.ItemInputtedService;
import ph.txtdis.service.QtyPerUomService;
import ph.txtdis.service.QuantityValidated;
import ph.txtdis.service.RemarkedAndSpunAndSavedAndOpenDialogAndTitleAndHeaderAndIconAndModuleNamedAndResettableAndTypeMappedService;
import ph.txtdis.service.ResettableService;
import ph.txtdis.type.QualityType;

public interface StockTakeService extends ItemBasedService<StockTakeDetail>, ResettableService,
		RemarkedAndSpunAndSavedAndOpenDialogAndTitleAndHeaderAndIconAndModuleNamedAndResettableAndTypeMappedService<Long>,
		ItemInputtedService<StockTakeDetail>, QtyPerUomService, QuantityValidated {

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

	void setWarehouseIfAllCountDateTransactionsAreCompleteAndNoStockTakeAlreadyMadeOnCountDate(String warehouse) throws Exception;
}