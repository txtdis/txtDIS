package ph.txtdis.mgdc.ccbpi.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import ph.txtdis.dto.SalesItemVariance;
import ph.txtdis.excel.ExcelReportWriter;
import ph.txtdis.fx.table.AppTable;
import ph.txtdis.service.RestClientService;
import ph.txtdis.util.ClientTypeMap;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.util.DateTimeUtils.toDateDisplay;
import static ph.txtdis.util.UserUtils.username;

public abstract class AbstractListService //
	implements ListService {

	private static Logger logger = getLogger(AbstractListService.class);

	protected List<SalesItemVariance> list;

	protected String item;

	@Autowired
	private BommedDiscountedPricedValidatedItemService itemService;

	@Autowired
	private RestClientService<SalesItemVariance> restClientService;

	@Autowired
	private ClientTypeMap typeMap;

	@Autowired
	private ExcelReportWriter excel;

	@Value("${prefix.module}")
	private String modulePrefix;

	private LocalDate start, end;

	private String route;

	public AbstractListService() {
		reset();
	}

	@Override
	public void reset() {
		list = null;
		start = null;
		end = null;
		item = null;
		route = null;
	}

	protected List<SalesItemVariance> getList(String endPt) throws Exception {
		return getRestClientServiceForLists().module(getModuleName()).getList(endPt);
	}

	@Override
	public RestClientService<SalesItemVariance> getRestClientServiceForLists() {
		return restClientService;
	}

	@Override
	public String getModuleName() {
		return "bookingVariance";
	}

	@Override
	public String getTitleName() {
		return username() + "@" + modulePrefix + " " + getHeaderName() + " : " + getSubhead();
	}

	@Override
	public String getSubhead() {
		try {
			return item + " for " + route + " on " + date();
		} catch (Exception e) {
			return "";
		}
	}

	protected String date() throws Exception {
		String d = toDateDisplay(start);
		if (!start.isEqual(end))
			d = d + " - " + toDateDisplay(end);
		return d;
	}

	@Override
	public List<BigDecimal> getTotals(List<SalesItemVariance> l) {
		return Arrays.asList(totalQty(l), totalValue(l));
	}

	private BigDecimal totalQty(List<SalesItemVariance> l) {
		return l.stream().map(SalesItemVariance::getVarianceQty).reduce(ZERO, BigDecimal::add);
	}

	private BigDecimal totalValue(List<SalesItemVariance> l) {
		return l.stream().map(SalesItemVariance::getValue).reduce(ZERO, BigDecimal::add);
	}

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public List<SalesItemVariance> list() {
		return list;
	}

	protected String item(String vendorNo) {
		try {
			return itemService.findByVendorNo(vendorNo).getName();
		} catch (Exception e) {
			return "";
		}
	}

	protected String itemVendorNo(String[] ids) {
		logger.info("\n    itemVendorNo = " + ids[0]);
		return ids[0];
	}

	protected String module(String[] ids) {
		String route = StringUtils.substringBefore(ids[2], "$");
		logger.info("\n    route = " + route);
		return this.route = route;
	}

	protected String route(String[] ids) {
		String route = StringUtils.substringBetween(ids[2], "$", "|");
		logger.info("\n    route = " + route);
		return this.route = route;
	}

	protected LocalDate startDate(String[] ids) {
		String date = StringUtils.substringBetween(ids[2], "|");
		logger.info("\n    startDate = " + date);
		return start = LocalDate.parse(date);
	}

	protected LocalDate endDate(String[] ids) {
		String date = StringUtils.substringAfterLast(ids[2], "|");
		logger.info("\n    endDate = " + date);
		return end = LocalDate.parse(date);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void saveAsExcel(AppTable<SalesItemVariance>... tables) throws IOException {
		excel.table(tables).filename(excelName()).sheetname(getHeaderName()).write();
	}

	private String excelName() {
		return getHeaderName().replace(" ", ".") + "." +
			getSubhead().replace(" ", "").replace(":", ".").replace("-", ".to.").replace("/", "-");
	}
}
