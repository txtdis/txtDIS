package ph.txtdis.service;

import static java.math.BigDecimal.ZERO;
import static java.time.ZonedDateTime.now;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static ph.txtdis.util.DateTimeUtils.toTimestampFilename;
import static ph.txtdis.util.DateTimeUtils.toTimestampText;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Inventory;
import ph.txtdis.excel.ExcelReportWriter;
import ph.txtdis.fx.table.AppTable;
import ph.txtdis.util.ClientTypeMap;

@Service("inventoryService")
public class InventoryServiceImpl implements InventoryService {

	@Autowired
	private CredentialService credentialService;

	@Autowired
	private ExcelReportWriter excel;

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
	public String getHeaderName() {
		return "Inventory";
	}

	@Override
	public Inventory getInventory(Long itemId) throws Exception {
		return readOnlyService.module(getModuleName()).getOne("/item?id=" + itemId);
	}

	@Override
	public String getModuleName() {
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
	public String getTitleName() {
		return credentialService.username() + "@" + modulePrefix + " " + capitalize(getModuleName());
	}

	@Override
	public List<BigDecimal> getTotals(List<Inventory> l) {
		return asList(getTotalValue(l), getTotalObsolesenceValue(l));
	}

	@Override
	public void reset() {
		date = null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void saveAsExcel(AppTable<Inventory>... tables) throws Exception {
		excel.table(tables).filename(excelName()).sheetname(toTimestampFilename(now())).write();
	}

	@Override
	public void setDate(LocalDate date) {
		this.date = date;
	}

	private String excelName() {
		return getTitleName() + "." + toTimestampFilename(now());
	}

	private BigDecimal getTotalObsolesenceValue(List<Inventory> l) {
		return l.stream().filter(v -> v.getObsolesenceValue() != null).map(v -> v.getObsolesenceValue()).reduce(ZERO, BigDecimal::add);
	}

	private BigDecimal getTotalValue(List<Inventory> l) {
		return l.stream().filter(v -> v.getValue() != null).map(v -> v.getValue()).reduce(ZERO, BigDecimal::add);
	}
}
