package ph.txtdis.service;

import static java.math.BigDecimal.ZERO;
import static java.time.ZonedDateTime.now;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static ph.txtdis.util.DateTimeUtils.toTimestampFilename;
import static ph.txtdis.util.DateTimeUtils.toTimestampText;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Inventory;
import ph.txtdis.excel.ExcelWriter;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;
import ph.txtdis.fx.table.AppTable;
import ph.txtdis.util.ClientTypeMap;

@Service("inventoryService")
public class InventoryServiceImpl implements InventoryService {

	@Autowired
	private CredentialService credentialService;

	@Autowired
	private ExcelWriter excel;

	@Autowired
	private ReadOnlyService<Inventory> readOnlyService;

	@Autowired
	private SyncService syncService;

	@Autowired
	private ClientTypeMap typeMap;

	@Value("${prefix.module}")
	private String modulePrefix;

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	private LocalDate date;

	@Override
	public LocalDate getDate() {
		if (date == null)
			date = syncService.getServerDate();
		return date;
	}

	@Override
	public String getHeaderText() {
		return "Inventory";
	}

	@Override
	public Inventory getInventory(Long itemId) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		return readOnlyService.module(getModule()).getOne("/item?id=" + itemId);
	}

	@Override
	public String getModule() {
		return "inventory";
	}

	@Override
	public ReadOnlyService<Inventory> getListedReadOnlyService() {
		return readOnlyService;
	}

	@Override
	public String getSubhead() {
		return "As of " + toTimestampText(now());
	}

	@Override
	public String getTitleText() {
		return credentialService.username() + "@" + modulePrefix + " " + capitalize(getModule());
	}

	@Override
	public List<BigDecimal> getTotals(List<Inventory> l) {
		return asList(getTotalValue(l), getTotalObsolesenceValue(l));
	}

	@Override
	@SuppressWarnings("unchecked")
	public void saveAsExcel(AppTable<Inventory>... tables) throws IOException {
		excel.filename(excelName()).sheetname(toTimestampFilename(now())).table(tables).write();
	}

	@Override
	public void setDate(LocalDate date) {
		this.date = date;
	}

	private String excelName() {
		return getTitleText() + "." + toTimestampFilename(now());
	}

	private BigDecimal getTotalObsolesenceValue(List<Inventory> l) {
		return l.stream().filter(v -> v.getObsolesenceValue() != null).map(v -> v.getObsolesenceValue()).reduce(ZERO,
				BigDecimal::add);
	}

	private BigDecimal getTotalValue(List<Inventory> l) {
		return l.stream().filter(v -> v.getValue() != null).map(v -> v.getValue()).reduce(ZERO, BigDecimal::add);
	}
}
