package ph.txtdis.service;

import java.time.LocalDate;
import java.util.List;

import ph.txtdis.dto.StockTakeVariance;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;
import ph.txtdis.info.SuccessfulSaveInfo;

public interface StockTakeVarianceService extends VarianceService<StockTakeVariance> {

	boolean canApprove();

	boolean canReject();

	LocalDate getLatestCountDate();

	LocalDate getPreviousCountDate();

	LocalDate getServerDate();

	String getUsername();

	void saveUponValidation(List<StockTakeVariance> items) throws SuccessfulSaveInfo, NoServerConnectionException,
			StoppedServerException, FailedAuthenticationException, InvalidException, RestException;
}
