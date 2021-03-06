package ph.txtdis.mgdc.gsm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ph.txtdis.dto.SalesVolume;
import ph.txtdis.excel.ExcelReportWriter;
import ph.txtdis.fx.table.AppTable;
import ph.txtdis.mgdc.service.HolidayService;
import ph.txtdis.mgdc.service.SalesVolumeService;
import ph.txtdis.mgdc.type.SalesVolumeReportType;
import ph.txtdis.service.RestClientService;
import ph.txtdis.util.ClientTypeMap;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;

import static java.math.BigDecimal.ZERO;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static ph.txtdis.util.DateTimeUtils.getServerDate;
import static ph.txtdis.util.DateTimeUtils.toDateDisplay;
import static ph.txtdis.util.UserUtils.username;

@Service("salesVolumeService")
public class SalesVolumeServiceImpl
	implements SalesVolumeService {

	@Autowired
	public ClientTypeMap typeMap;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private HolidayService holidayService;

	@Autowired
	private RestClientService<SalesVolume> restClientService;

	@Autowired
	private ExcelReportWriter excel;

	@Value("${prefix.module}")
	private String modulePrefix;

	private List<SalesVolume> list;

	private LocalDate start, end;

	private SalesVolumeReportType type;

	private String customer;

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public List<SalesVolume> dataDump() {
		customer = null;
		if (list == null || list.isEmpty())
			list();
		return list;
	}

	@Override
	public List<SalesVolume> list() {
		try {
			customer = null;
			list =
				restClientService.module(getModuleName()).getList("/list?start=" + getStartDate() + "&end=" + getEndDate
					());
			return listPerType(list);
		} catch (Exception e) {
			return list = emptyList();
		}
	}

	@Override
	public String getModuleName() {
		return "salesVolume";
	}

	@Override
	public LocalDate getStartDate() {
		if (start == null)
			start = yesterday();
		return start;
	}

	@Override
	public LocalDate getEndDate() {
		if (end == null)
			end = yesterday();
		return end;
	}

	private List<SalesVolume> listPerType(List<SalesVolume> l) {
		return l.stream().collect(groupingBy(group(type)))//
			.entrySet().stream().map(d -> toSalesVolume(d))//
			.sorted(sort(type)).collect(toList());
	}

	private LocalDate yesterday() {
		return holidayService.previousWorkDay(getServerDate());
	}

	private Function<SalesVolume, String> group(SalesVolumeReportType t) {
		switch (t) {
			case CATEGORY:
				return SalesVolume::getCategory;
			case PRODUCT_LINE:
				return SalesVolume::getProductLine;
			case ITEM:
			default:
				return SalesVolume::getItem;
		}
	}

	private SalesVolume toSalesVolume(Entry<String, List<SalesVolume>> l) {
		SalesVolume s = l.getValue().get(0);
		SalesVolume v = new SalesVolume();
		v.setId(s.getId());
		v.setSeller(s.getSeller());
		v.setChannel(s.getChannel());
		v.setCustomer(s.getCustomer());
		v.setCategory(s.getCategory());
		v.setProductLine(s.getProductLine());
		v.setItem(s.getItem());
		v.setVol(volume(l));
		v.setUom(s.getUom());
		v.setQty(quantity(l));
		return v;
	}

	private Comparator<SalesVolume> sort(SalesVolumeReportType t) {
		switch (t) {
			case CATEGORY:
			case PRODUCT_LINE:
				return (a, b) -> a.getId().compareTo(b.getId());
			case ITEM:
			default:
				return (a, b) -> a.getItem().compareTo(b.getItem());
		}
	}

	private BigDecimal volume(Entry<String, List<SalesVolume>> d) {
		return d.getValue().stream().map(SalesVolume::getVol).reduce(ZERO, BigDecimal::add);
	}

	private BigDecimal quantity(Entry<String, List<SalesVolume>> d) {
		return d.getValue().stream().map(SalesVolume::getQty).reduce(ZERO, BigDecimal::add);
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
	public List<SalesVolume> filterByCustomer(long id) throws Exception {
		customer = customerService.findById(id).toString();
		List<SalesVolume> l = list.stream()//
			.filter(r -> customer.equals(r.getCustomer()))//
			.collect(toList());
		return listPerType(l);
	}

	@Override
	public RestClientService<SalesVolume> getRestClientServiceForLists() {
		return restClientService;
	}

	@Override
	public String getTitleText() {
		return username() + "@" + modulePrefix + " " + sheetname() + ": " + getSubhead();
	}

	private String sheetname() {
		return "STT";
	}

	@Override
	public String getSubhead() {
		String s = "";
		if (customer != null)
			s = customer + ": ";
		s += toDateDisplay(getStartDate());
		if (!start.isEqual(getEndDate()))
			s += " - " + toDateDisplay(end);
		return s;
	}

	@Override
	public void reset() {
		list = null;
		start = null;
		end = null;
		type = null;
		customer = null;
	}

	@Override
	public void setType(String t) {
		type = SalesVolumeReportType.valueOf(t);
	}

	@Override
	public void setType(SalesVolumeReportType t) {
		type = t;
	}

	@Override
	public void next() {
		customer = null;
		if (getEndDate().isBefore(yesterday()))
			start = end = getEndDate().plusDays(1L);
	}

	@Override
	public void previous() {
		customer = null;
		end = start = getStartDate().minusDays(1L);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void saveAsExcel(AppTable<SalesVolume>... tables) throws IOException {
		excel.table(tables).filename(filename()).sheetname(sheetname()).write();
	}

	private String filename() {
		return getHeaderName().replace(" ", ".") + "." +
			getSubhead().replace(" ", "").replace(":", ".").replace("-", ".to.").replace("/", "-");
	}

	@Override
	public String getHeaderName() {
		return "Sales Volume";
	}
}
