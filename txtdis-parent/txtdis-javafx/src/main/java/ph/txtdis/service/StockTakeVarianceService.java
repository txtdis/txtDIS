package ph.txtdis.service;

import java.time.LocalDate;
import java.util.List;

import ph.txtdis.dto.StockTakeVariance;
import ph.txtdis.info.Information;

public interface StockTakeVarianceService extends VarianceService<StockTakeVariance> {

	boolean canApprove();

	boolean canReject();

	LocalDate getLatestCountDate();

	LocalDate getPreviousCountDate();

	LocalDate getServerDate();

	String getUsername();

	void saveUponValidation(List<StockTakeVariance> items) throws Information, Exception;
}
