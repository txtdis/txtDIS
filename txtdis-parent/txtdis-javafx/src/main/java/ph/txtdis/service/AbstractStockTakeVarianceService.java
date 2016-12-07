package ph.txtdis.service;

import static java.util.Collections.emptyList;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.dto.StockTakeVariance;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;
import ph.txtdis.info.SuccessfulSaveInfo;
import ph.txtdis.type.ScriptType;
import ph.txtdis.type.UserType;
import ph.txtdis.util.DateTimeUtils;
import ph.txtdis.util.NumberUtils;

public abstract class AbstractStockTakeVarianceService extends AbstractVarianceService<StockTakeVariance>
		implements StockTakeVarianceService {

	@Autowired
	private CredentialService credentialService;

	@Autowired
	private SavingService<List<StockTakeVariance>> savingService;

	@Autowired
	private ScriptService scriptService;

	@Autowired
	private RestServerService serverService;

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
	public String getActualHeader() {
		return "Per Count";
	}

	@Override
	public String getBeginningHeader() {
		return super.getBeginningHeader() + getPreviousCountDateText();
	}

	private String getPreviousCountDateText() {
		LocalDate d = getPreviousCountDate();
		return d == null ? "" : " " + DateTimeUtils.toDateDisplay(d);
	}

	@Override
	public String getExpectedHeader() {
		return "Ending";
	}

	@Override
	public String getHeaderText() {
		return "Stock Take Variance";
	}

	@Override
	public LocalDate getLatestCountDate() {
		return stockTakeService.getLatestCountDate();
	}

	@Override
	public String getModule() {
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
			return readOnlyService.module(getModule()).getList("/list?date=" + getLatestCountDate());
		} catch (Exception e) {
			e.printStackTrace();
			return emptyList();
		}
	}

	@Override
	public void saveUponValidation(List<StockTakeVariance> items) throws SuccessfulSaveInfo, NoServerConnectionException,
			StoppedServerException, FailedAuthenticationException, InvalidException, RestException {
		verifyVariancesHaveFinalQtyAndJustications(items);
		savingService.module(getModule()).save(items);
		saveScript(items);
		throw new SuccessfulSaveInfo(
				DateTimeUtils.toDateDisplay(getLatestCountDate()) + " Stock Take Variance\nAdjusted Qty And Justification");
	}

	private void verifyVariancesHaveFinalQtyAndJustications(List<StockTakeVariance> items) throws InvalidException {
		StockTakeVariance s = items.stream().filter(v -> isVarianceAdjustedAndJustified(v)).findFirst().orElse(null);
		if (s != null)
			throw new InvalidException(
					"All variances must be adjusted and justified\n" + s.getQuality() + " " + s.getItem() + "'s is not.");
	}

	private boolean isVarianceAdjustedAndJustified(StockTakeVariance v) {
		return !NumberUtils.isZero(v.getVarianceQty())
				&& (v.getJustification() == null || v.getJustification().isEmpty());
	}

	private void saveScript(List<StockTakeVariance> variances) throws NoServerConnectionException,
			StoppedServerException, FailedAuthenticationException, InvalidException, SuccessfulSaveInfo {
		if (serverService.isOffSite() && isDecided(variances)) {
			variances.forEach(v -> saveScript(v));
			scriptService.saveScripts();
		}
	}

	private void saveScript(StockTakeVariance v) {
		String script = v.getId() + "|" + v.getIsValid() + "|" + v.getJustification().replace("\n", "@");
		scriptService.set(ScriptType.STOCK_TAKE_ADJUSTMENT_APPROVAL, script);
	}

	private boolean isDecided(List<StockTakeVariance> items) {
		return items != null && !items.isEmpty() && items.get(0).getIsValid() != null;
	}

	@Override
	public List<BigDecimal> getTotals(List<StockTakeVariance> l) {
		return null;
	}
}
