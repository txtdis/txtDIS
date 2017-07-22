package ph.txtdis.mgdc.gsm.service;

import static java.util.Collections.emptyList;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.dto.StockTakeVariance;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.info.Information;
import ph.txtdis.info.SuccessfulSaveInfo;
import ph.txtdis.service.AbstractVarianceService;
import ph.txtdis.service.CredentialService;
import ph.txtdis.service.SavingService;
import ph.txtdis.service.StockTakeVarianceService;
import ph.txtdis.service.SyncService;
import ph.txtdis.type.UserType;
import ph.txtdis.util.DateTimeUtils;
import ph.txtdis.util.NumberUtils;

public abstract class AbstractStockTakeVarianceService ///
		extends AbstractVarianceService<StockTakeVariance> //
		implements StockTakeVarianceService {

	@Autowired
	private CredentialService credentialService;

	@Autowired
	private SavingService<List<StockTakeVariance>> savingService;

	@Autowired
	private StockTakeService stockTakeService;

	@Autowired
	private SyncService syncService;

	@Override
	public boolean canApprove() {
		return isAuditor();
	}

	private boolean isAuditor() {
		return credentialService.isUser(UserType.MANAGER) || credentialService.isUser(UserType.AUDITOR);
	}

	@Override
	public boolean canReject() {
		return isAuditor();
	}

	@Override
	public String getActualColumnName() {
		return "Per Count";
	}

	@Override
	public String getBeginningColumnName() {
		return super.getBeginningColumnName() + getPreviousCountDateText();
	}

	private String getPreviousCountDateText() {
		LocalDate d = getPreviousCountDate();
		return d == null ? "" : " " + DateTimeUtils.toDateDisplay(d);
	}

	@Override
	public String getExpectedColumnName() {
		return "Ending";
	}

	@Override
	public String getHeaderName() {
		return "Stock Take Variance";
	}

	@Override
	public LocalDate getLatestCountDate() {
		return stockTakeService.getLatestCountDate();
	}

	@Override
	public String getModuleName() {
		return "stockTakeVariance";
	}

	@Override
	public LocalDate getPreviousCountDate() {
		return stockTakeService.getPreviousCountDate();
	}

	@Override
	public LocalDate getServerDate() {
		return syncService.getServerDate();
	}

	@Override
	public String getUsername() {
		return credentialService.username();
	}

	@Override
	public List<StockTakeVariance> list() {
		try {
			return getListedReadOnlyService().module(getModuleName()).getList("/list?date=" + getLatestCountDate());
		} catch (Exception e) {
			e.printStackTrace();
			return emptyList();
		}
	}

	@Override
	public void saveUponValidation(List<StockTakeVariance> items) throws Information, Exception {
		verifyVariancesHaveFinalQtyAndJustications(items);
		savingService.module(getModuleName()).save(items);
		throw new SuccessfulSaveInfo(DateTimeUtils.toDateDisplay(getLatestCountDate()) + " Stock Take Variance\nAdjusted Qty And Justification");
	}

	private void verifyVariancesHaveFinalQtyAndJustications(List<StockTakeVariance> items) throws Exception {
		StockTakeVariance s = items.stream().filter(v -> isVarianceAdjustedAndJustified(v)).findFirst().orElse(null);
		if (s != null)
			throw new InvalidException("All variances must be adjusted and justified\n" + s.getQuality() + " " + s.getItem() + "'s is not.");
	}

	private boolean isVarianceAdjustedAndJustified(StockTakeVariance v) {
		return !NumberUtils.isZero(v.getVarianceQty()) && (v.getJustification() == null || v.getJustification().isEmpty());
	}

	@Override
	public List<BigDecimal> getTotals(List<StockTakeVariance> l) {
		return null;
	}
}
