package ph.txtdis.mgdc.ccbpi.service;

import static ph.txtdis.type.DeliveryType.PICK_UP;
import static ph.txtdis.type.RouteType.PRE_SELL;
import static ph.txtdis.util.DateTimeUtils.toHypenatedYearMonthDay;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.dto.Route;
import ph.txtdis.dto.Routing;
import ph.txtdis.excel.ExcelReportWriter;
import ph.txtdis.exception.DeactivatedException;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.fx.table.AppTable;
import ph.txtdis.mgdc.ccbpi.dto.Customer;
import ph.txtdis.service.SyncService;

public abstract class AbstractItemDeliveredCustomerService //
		extends AbstractCustomerService //
		implements ItemDeliveredCustomerService {

	@Autowired
	private SyncService syncService;

	@Autowired
	private ExcelReportWriter excel;

	@Override
	public boolean areDeliveriesBooked(Customer c, LocalDate d) {
		Route r = getRoute(c, d);
		return r == null ? true : r.getName().startsWith(PRE_SELL.toString());
	}

	@Override
	public boolean areDeliveriesPickedUp(Customer c, LocalDate d) {
		Route r = getRoute(c, d);
		return r == null ? true : r.getName().equalsIgnoreCase(PICK_UP.toString());
	}

	@Override
	public Customer findByName(String text) throws Exception {
		Customer c = getOne("/find?name=" + text);
		if (c != null && c.getDeactivatedOn() != null)
			throw new DeactivatedException(c.getName());
		return c;
	}

	protected Customer getOne(String endPt) throws Exception {
		return getListedReadOnlyService().module(getModuleName()).getOne(endPt);
	}

	@Override
	public Customer findByVendorId(Long id) throws Exception {
		return getOne("/get?vendorId=" + id);
	}

	@Override
	public Route getRoute(Customer c, LocalDate date) {
		return c.getRouteHistory().stream() //
				.filter(p -> !p.getStartDate().isAfter(date)) //
				.max((a, b) -> a.getStartDate().compareTo(b.getStartDate())) //
				.orElse(new Routing()).getRoute();
	}

	@Override
	public String getSeller(Customer c, LocalDate d) {
		try {
			return getRoute(c, d).getSeller(d);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void saveAsExcel(AppTable<Customer>... tables) throws Exception {
		excel.table(tables).filename(excelName()).sheetname(getExcelSheetName()).write();
	}

	private String excelName() {
		return getExcelSheetName() + "." + toHypenatedYearMonthDay(today());
	}

	private String getExcelSheetName() {
		return "Active.Customers";
	}

	@Override
	public void openByOpenDialogInputtedKey(String key) throws Exception {
		try {
			openByDoubleClickedTableCellId(Long.valueOf(key));
		} catch (NumberFormatException e) {
			throw new NotFoundException("Customer No. " + key);
		}
	}

	@Override
	public LocalDate today() {
		return syncService.getServerDate();
	}
}
