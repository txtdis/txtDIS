package ph.txtdis.service;

import ph.txtdis.dto.StockTakeVariance;
import ph.txtdis.info.Information;

import java.time.LocalDate;
import java.util.List;

public interface StockTakeVarianceService
	extends VarianceService<StockTakeVariance> {

	boolean canApprove();

	boolean canReject();

	LocalDate getLatestCountDate();

	LocalDate getPreviousCountDate();

	String getUsername();

	void saveUponValidation(List<StockTakeVariance> items) throws Information, Exception;
}
