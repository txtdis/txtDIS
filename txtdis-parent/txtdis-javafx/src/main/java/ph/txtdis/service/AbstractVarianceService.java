package ph.txtdis.service;

import static java.util.Collections.emptyList;
import static ph.txtdis.util.DateTimeUtils.*;
import static ph.txtdis.util.DateTimeUtils.toDateDisplay;
import static ph.txtdis.util.UserUtils.*;

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
	private RestClientService<T> restClientService;

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
	public void reset() {
		end = null;
		start = null;
	}

	@Override
	public RestClientService<T> getRestClientServiceForLists() {
		return restClientService;
	}

	@Override
	public String getTitleName() {
		return username() + "@" + modulePrefix + " " + getHeaderName() + " : " + getSubhead();
	}

	@Override
	public String getHeaderName() {
		return getActualColumnName() + " Variance";
	}

	@Override
	public String getSubhead() {
		String d = toDateDisplay(getStartDate());
		if (!start.isEqual(getEndDate()))
			d = d + " - " + toDateDisplay(end);
		return d;
	}

	@Override
	public LocalDate getStartDate() {
		if (start == null)
			start = today();
		return start;
	}

	@Override
	public LocalDate getEndDate() {
		if (end == null)
			end = today();
		return end;
	}

	protected LocalDate today() {
		return getServerDate();
	}

	@Override
	public void setEndDate(LocalDate d) {
		end = d;
	}

	@Override
	public void setStartDate(LocalDate d) {
		start = d;
	}

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public List<T> list() {
		try {
			return restClientService.module(getModuleName())
				.getList("/list?start=" + getStartDate() + "&end=" + getEndDate());
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
	@SuppressWarnings("unchecked")
	public void saveAsExcel(AppTable<T>... tables) throws IOException {
		excel.table(tables).filename(excelName()).sheetname(getHeaderName()).write();
	}

	private String excelName() {
		return getHeaderName().replace(" ", ".") + "." +
			getSubhead().replace(" ", "").replace(":", ".").replace(" - ", ".to.").replace("/", "-");
	}
}
