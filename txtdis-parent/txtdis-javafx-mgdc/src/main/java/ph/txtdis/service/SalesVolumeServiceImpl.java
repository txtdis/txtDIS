package ph.txtdis.service;

import static java.math.BigDecimal.ZERO;
import static java.time.LocalDate.now;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static ph.txtdis.util.DateTimeUtils.toDateDisplay;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Billable;
import ph.txtdis.dto.SalesVolume;
import ph.txtdis.excel.ExcelWriter;
import ph.txtdis.fx.table.AppTable;
import ph.txtdis.type.SalesVolumeReportType;
import ph.txtdis.util.ClientTypeMap;

@Service("salesVolumeService")
public class SalesVolumeServiceImpl implements SalesVolumeService {

	@Autowired
	private CredentialService credentialService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private ReadOnlyService<SalesVolume> readOnlyService;

	@Autowired
	private ReadOnlyService<Billable> billableReadOnlyService;

	@Autowired
	private ExcelWriter excel;

	@Autowired
	public ClientTypeMap typeMap;

	@Value("${prefix.module}")
	private String modulePrefix;

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	private List<SalesVolume> list;

	private LocalDate start, end;

	private SalesVolumeReportType type;

	private String customer;

	@Override
	public List<SalesVolume> dataDump() {
		customer = null;
		if (list == null || list.isEmpty())
			list();
		return list;
	}

	@Override
	public LocalDate getEndDate() {
		if (end == null)
			end = yesterday();
		return end;
	}

	@Override
	public String getHeaderText() {
		return "Sales Volume";
	}

	@Override
	public String getModule() {
		return "salesVolume";
	}

	@Override
	public ReadOnlyService<SalesVolume> getListedReadOnlyService() {
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
		String s = "";
		if (customer != null)
			s = customer + ": ";
		s += toDateDisplay(getStartDate());
		if (!start.isEqual(getEndDate()))
			s += " - " + toDateDisplay(end);
		return s;
	}

	@Override
	public String getTitleText() {
		return credentialService.username() + "@" + modulePrefix + " " + sheetName() + ": " + getSubhead();
	}

	@Override
	public List<SalesVolume> list() {
		try {
			customer = null;
			list = readOnlyService.module(getModule()).getList("/list?start=" + getStartDate() + "&end=" + getEndDate());
			return listPer(list);
		} catch (Exception e) {
			e.printStackTrace();
			return list = emptyList();
		}
	}

	@Override
	public void setType(String t) {
		type = SalesVolumeReportType.valueOf(t);
	}

	@Override
	public void setType(SalesVolumeReportType t) {
		type = t;
	}

	private List<SalesVolume> listPer(List<SalesVolume> l) {
		return l.stream().collect(groupingBy(group(type)))//
				.entrySet().stream().map(d -> toSalesVolume(d))//
				.sorted(sort(type)).collect(toList());
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
		excel.filename(excelName()).sheetname(sheetName()).table(tables).write();
	}

	@Override
	public void setEndDate(LocalDate d) {
		end = d;
	}

	@Override
	public void setStartDate(LocalDate d) {
		start = d;
	}

	private String excelName() {
		return getHeaderText().replace(" ", ".") + "." + getSubhead().replace("-", ".to.").replace("/", "-");
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

	private BigDecimal quantity(Entry<String, List<SalesVolume>> d) {
		return d.getValue().stream().map(SalesVolume::getQty).reduce(ZERO, BigDecimal::add);
	}

	private SalesVolume salesVolume(Entry<String, List<SalesVolume>> d) {
		return d.getValue().get(0);
	}

	private String sheetName() {
		return "STT";
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

	private SalesVolume toSalesVolume(Entry<String, List<SalesVolume>> d) {
		SalesVolume sv = new SalesVolume();
		sv.setId(salesVolume(d).getId());
		sv.setSeller(salesVolume(d).getSeller());
		sv.setChannel(salesVolume(d).getChannel());
		sv.setCustomer(salesVolume(d).getCustomer());
		sv.setCategory(salesVolume(d).getCategory());
		sv.setProductLine(salesVolume(d).getProductLine());
		sv.setItem(salesVolume(d).getItem());
		sv.setVol(volume(d));
		sv.setUom(salesVolume(d).getUom());
		sv.setQty(quantity(d));
		return sv;
	}

	private BigDecimal volume(Entry<String, List<SalesVolume>> d) {
		return d.getValue().stream().map(SalesVolume::getVol).reduce(ZERO, BigDecimal::add);
	}

	private LocalDate yesterday() {
		return now().minusDays(1L);
	}

	@Override
	public List<SalesVolume> filterByCustomer(long id) throws Exception {
		customer = customerService.find(id).toString();
		List<SalesVolume> l = list.stream()//
				.filter(r -> customer.equals(r.getCustomer()))//
				.collect(toList());
		return listPer(l);
	}

	@Override
	public ReadOnlyService<Billable> getBillableReadOnlyService() {
		return billableReadOnlyService;
	}
}
