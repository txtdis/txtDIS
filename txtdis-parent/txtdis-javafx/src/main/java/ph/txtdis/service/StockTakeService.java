package ph.txtdis.service;

import java.time.LocalDate;
import java.util.List;

import ph.txtdis.dto.StockTake;
import ph.txtdis.dto.StockTakeDetail;
import ph.txtdis.type.QualityType;

public interface StockTakeService extends ItemBasedService<StockTakeDetail>, Reset, Serviced<Long>,
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

	boolean isOffSite();

	boolean isUserAStockTaker();

	List<String> listCheckers();

	List<String> listItemsOnStock() throws Exception;

	List<String> listTakers();

	List<String> listWarehouses();

	void setChecker(String checkerName);

	void setDetails(List<StockTakeDetail> details);

	void setQuality(QualityType quality);

	void setTaker(String takerName);

	void setWarehouseIfAllCountDateTransactionsAreCompleteAndNoStockTakeAlreadyMadeOnCountDate(String warehouse)
			throws Exception;
}