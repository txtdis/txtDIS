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
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Billable;
import ph.txtdis.dto.SalesVolume;
import ph.txtdis.excel.ExcelWriter;
import ph.txtdis.excel.Tabular;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;
import ph.txtdis.type.SalesVolumeReportType;
import ph.txtdis.util.TypeMap;

@Service("salesVolumeService")
public class SalesVolumeService implements BilledAllPickedSalesOrder, Iconed, Excel<SalesVolume>, Spun {

	@Autowired
	private ExcelWriter excel;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private ReadOnlyService<SalesVolume> readOnlyService;

	@Autowired
	private ReadOnlyService<Billable> billableReadOnlyService;

	@Autowired
	public TypeMap typeMap;

	@Override
	public TypeMap getTypeMap() {
		return typeMap;
	}

	private List<SalesVolume> list;

	private LocalDate start, end;

	private SalesVolumeReportType type;

	private String customer;

	public List<SalesVolume> dataDump() {
		customer = null;
		if (list == null || list.isEmpty())
			list();
		System.out.println("List @ dataDump() = " + list.size());
		return list;
	}

	@Override
	public ReadOnlyService<Billable> getBillableReadOnlyService() {
		return billableReadOnlyService;
	}

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
	public ReadOnlyService<SalesVolume> getReadOnlyService() {
		return readOnlyService;
	}

	public LocalDate getStartDate() {
		if (start == null)
			start = yesterday();
		return start;
	}

	public String getSubhead() {
		String s = "";
		if (customer != null)
			s = customer + ": ";
		s += toDateDisplay(getStartDate());
		if (!start.isEqual(getEndDate()))
			s += (" - " + toDateDisplay(end));
		return s;
	}

	public String getTitleText() {
		return sheetName() + ": " + getSubhead();
	}

	@Override
	public List<SalesVolume> list() {
		try {
			customer = null;
			list = readOnlyService.module(getModule()).getList("/list?start=" + getStartDate() + "&end=" + getEndDate());
			System.out.println("List @ list() = " + list.size());
			return listPer(list);
		} catch (Exception e) {
			e.printStackTrace();
			return list = emptyList();
		}
	}

	public void setType(String t) {
		type = SalesVolumeReportType.valueOf(t);
	}

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
	public void saveAsExcel(Tabular... tables) throws IOException {
		excel.filename(excelName()).sheetname(sheetName()).table(tables).write();
	}

	public void setEndDate(LocalDate d) {
		end = d;
	}

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

	public List<SalesVolume> filterByCustomer(long id) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException, NotFoundException {
		customer = customerService.find(id).toString();
		List<SalesVolume> l = list.stream()//
				.filter(r -> customer.equals(r.getCustomer()))//
				.collect(toList());
		return listPer(l);
	}
}
