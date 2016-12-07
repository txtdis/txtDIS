package ph.txtdis.service;

import static java.util.Collections.emptyList;
import static ph.txtdis.util.DateTimeUtils.toDateDisplay;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import ph.txtdis.excel.ExcelWriter;
import ph.txtdis.fx.table.AppTable;
import ph.txtdis.util.ClientTypeMap;

public abstract class AbstractVarianceService<T> implements VarianceService<T> {

	@Autowired
	private ClientTypeMap typeMap;

	@Autowired
	private ExcelWriter excel;

	@Autowired
	protected CredentialService credentialService;

	@Autowired
	protected ReadOnlyService<T> readOnlyService;

	@Autowired
	protected SyncService syncService;

	@Value("${prefix.module}")
	private String modulePrefix;

	protected LocalDate end, start;

	@Override
	public LocalDate getEndDate() {
		if (end == null)
			end = yesterday();
		return end;
	}

	private LocalDate yesterday() {
		return today().minusDays(1L);
	}

	protected LocalDate today() {
		return syncService.getServerDate();
	}

	@Override
	public String getHeaderText() {
		return getActualHeader() + " Variance";
	}

	@Override
	public ReadOnlyService<T> getListedReadOnlyService() {
		return readOnlyService;
	}

	@Override
	public LocalDate getStartDate() {
		if (start == null)
			start = yesterday();
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
	public String getTitleText() {
		return credentialService.username() + "@" + modulePrefix + " " + getHeaderText() + " : " + getSubhead();
	}

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public List<T> list() {
		try {
			return readOnlyService.module(getModule()).getList("/list?start=" + getStartDate() + "&end=" + getEndDate());
		} catch (Exception e) {
			e.printStackTrace();
			return emptyList();
		}
	}

	@Override
	public void next() {
		if (getEndDate().isBefore(yesterday()))
			start = end = getEndDate().plusDays(1L);
	}

	@Override
	public void previous() {
		end = start = getStartDate().minusDays(1L);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void saveAsExcel(AppTable<T>... tables) throws IOException {
		excel.filename(excelName()).sheetname(getHeaderText()).table(tables).write();
	}

	private String excelName() {
		return getHeaderText().replace(" ", ".") + "." + getSubhead().replace("-", ".to.").replace("/", "-");
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
