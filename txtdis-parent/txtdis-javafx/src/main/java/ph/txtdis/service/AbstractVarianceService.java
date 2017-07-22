package ph.txtdis.service;

import static java.util.Collections.emptyList;
import static ph.txtdis.util.DateTimeUtils.toDateDisplay;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import ph.txtdis.dto.Keyed;
import ph.txtdis.excel.ExcelReportWriter;
import ph.txtdis.fx.table.AppTable;
import ph.txtdis.util.ClientTypeMap;

public abstract class AbstractVarianceService<T extends Keyed<Long>> //
		implements VarianceService<T> {

	@Autowired
	private CredentialService credentialService;

	@Autowired
	private ReadOnlyService<T> readOnlyService;

	@Autowired
	private SyncService syncService;

	@Autowired
	private ClientTypeMap typeMap;

	@Autowired
	private ExcelReportWriter excel;

	@Value("${prefix.module}")
	private String modulePrefix;

	private LocalDate end, start;

	public AbstractVarianceService() {
		reset();
	}

	@Override
	public LocalDate getEndDate() {
		if (end == null)
			end = today();
		return end;
	}

	protected LocalDate today() {
		return syncService.getServerDate();
	}

	@Override
	public String getHeaderName() {
		return getActualColumnName() + " Variance";
	}

	@Override
	public ReadOnlyService<T> getListedReadOnlyService() {
		return readOnlyService;
	}

	@Override
	public LocalDate getStartDate() {
		if (start == null)
			start = today();
		return start;
	}

	@Override
	public String getSubhead() {
		String d = toDateDisplay(getStartDate());
		if (!start.isEqual(getEndDate()))
			d = d + " - " + toDateDisplay(end);
		return d;
	}

	@Override
	public String getTitleName() {
		return credentialService.username() + "@" + modulePrefix + " " + getHeaderName() + " : " + getSubhead();
	}

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public List<T> list() {
		try {
			return readOnlyService.module(getModuleName()).getList("/list?start=" + getStartDate() + "&end=" + getEndDate());
		} catch (Exception e) {
			e.printStackTrace();
			return emptyList();
		}
	}

	@Override
	public void next() {
		if (getEndDate().isBefore(today()))
			start = end = getEndDate().plusDays(1L);
	}

	@Override
	public void previous() {
		end = start = getStartDate().minusDays(1L);
	}

	@Override
	public void reset() {
		end = null;
		start = null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void saveAsExcel(AppTable<T>... tables) throws IOException {
		excel.table(tables).filename(excelName()).sheetname(getHeaderName()).write();
	}

	private String excelName() {
		return getHeaderName().replace(" ", ".") + "." + getSubhead().replace(" ", "").replace(":", ".").replace(" - ", ".to.").replace("/", "-");
	}

	@Override
	public void setEndDate(LocalDate d) {
		end = d;
	}

	@Override
	public void setStartDate(LocalDate d) {
		start = d;
	}
}
