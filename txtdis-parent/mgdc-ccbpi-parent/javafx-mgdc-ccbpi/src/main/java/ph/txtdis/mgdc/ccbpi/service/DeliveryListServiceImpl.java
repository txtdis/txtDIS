package ph.txtdis.mgdc.ccbpi.service;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.substringBefore;
import static ph.txtdis.type.PriceType.PURCHASE;
import static ph.txtdis.util.DateTimeUtils.toDateDisplay;
import static ph.txtdis.util.DateTimeUtils.toLocalDateFromOrderConfirmationFormat;
import static ph.txtdis.util.DateTimeUtils.toOrderConfirmationDate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Billable;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.exception.InvalidWorkBookFormatException;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.info.Information;
import ph.txtdis.info.SuccessfulSaveInfo;
import ph.txtdis.type.UomType;

@Service("deliveryListService")
public class DeliveryListServiceImpl //
	extends AbstractCokeBillableService //
	implements DeliveryListService {

	private static final String ROUTE_ID = "E3CX";

	@Autowired
	private ChannelService channelService;

	@Value("${client.name}")
	private String clientName;

	@Value("${route.initial}")
	private String routeInitial;

	private Map<Integer, Billable> map;

	@Override
	public void confirmNoDeliveryListOfSameDateExists() throws Exception {
		if (findByOrderDateAndRoute(getOrderDate(), "") != null)
			throw new DuplicateException("DDL dated " + toDateDisplay(getOrderDate()));
	}

	private Billable findByOrderDateAndRoute(LocalDate date, String route) throws Exception {
		return getRestClientService().module(getModuleName()).getOne("/ddl?date=" + date + "&route=" + route);
	}

	@Override
	public String getModuleName() {
		return "deliveryList";
	}

	@Override
	@SuppressWarnings("unchecked")
	public Billable findByOrderNo(String id) throws Exception {
		Billable b = findByOrderDateAndRoute(date(id), route(id));
		return throwNotFoundExceptionIfNull(b, id);
	}

	private LocalDate date(String id) throws Exception {
		LocalDate date = toLocalDateFromOrderConfirmationFormat(substringBefore(id, routeInitial));
		if (date == null)
			throw new NotFoundException(getAbbreviatedModuleNoPrompt() + id);
		return date;
	}

	private String route(String id) {
		return routeInitial + StringUtils.substringAfter(id, routeInitial);
	}

	@Override
	public String getAlternateName() {
		return "DDL";
	}

	@Override
	public String getHeaderName() {
		return "DP Summary Delivery List";
	}

	@Override
	public String getOpenDialogKeyPrompt() {
		return getAbbreviatedModuleNoPrompt();
	}

	@Override
	public String getOpenDialogPrompt() {
		return "Format is: " + ORDER_DATE + ROUTE_ID //
			+ "\nfor DDL dated " + DATE //
			+ " of " + getRoutePrompt() + " " + ROUTE_ID;
	}

	@Override
	public String getRoutePrompt() {
		return "Route";
	}

	@Override
	public String getSavingInfo() {
		return ocsDate() + route();
	}

	private String ocsDate() {
		return toOrderConfirmationDate(getOrderDate());
	}

	@Override
	public void initializeMap() {
		map = new HashMap<>();
	}

	@Override
	public void mapIndexToDDL(int index, String name) {
		if (isRoute(name))
			map.put(index, newDeliveryList(name));
	}

	private boolean isRoute(String name) {
		return name == null ? false : listRouteNames().contains(name);
	}

	private Billable newDeliveryList(String name) {
		Billable b = new Billable();
		b.setOrderDate(getOrderDate());
		b.setSuffix(name);
		return b;
	}

	@Override
	public List<String> listRouteNames() {
		return isNew() ? routeNames() : asList(route());
	}

	private List<String> routeNames() {
		List<String> routes = new ArrayList<>(asList("IMPORT"));
		routes.addAll(channelService.listNames());
		return routes;
	}

	@Override
	public void saveExtractedDDLs() throws Information, Exception {
		for (Billable ddl : map.values())
			set(save(ddl));
		throw new SuccessfulSaveInfo(ocsDate());
	}

	@Override
	public void setItemUponValidation(int vendorId) throws Exception {
		setItem(itemService.findByVendorNo(String.valueOf(vendorId)));
	}

	@Override
	public void setRouteItemQtyUponValidation(int columnIdx, double qty) throws Exception {
		Billable deliveryList = map.get(columnIdx);
		if (deliveryList != null && item != null && qty > 0)
			updateMap(columnIdx, deliveryList, qty);
	}

	private void updateMap(int columnIdx, Billable deliveryList, double qty) throws Exception {
		deliveryList.setDetails(details(deliveryList, qty));
		map.put(columnIdx, deliveryList);
	}

	private List<BillableDetail> details(Billable deliveryList, double qty) throws Exception {
		setQtyUponValidation(UomType.CS, unitQty(qty));
		return details(deliveryList);
	}

	private BigDecimal unitQty(double qty) {
		qty = qty * qtyPerCase(UomType.CS);
		return new BigDecimal(qty);
	}

	private List<BillableDetail> details(Billable deliveryList) {
		List<BillableDetail> details = details(deliveryList.getDetails());
		details.add(createDetail());
		return details;
	}

	private List<BillableDetail> details(List<BillableDetail> details) {
		return details == null ? new ArrayList<>() : new ArrayList<>(details);
	}

	@Override
	public BillableDetail createDetail() {
		BillableDetail d = super.createDetail();
		d.setPriceValue(currentPurchasePriceValue(d));
		return d;
	}

	private BigDecimal currentPurchasePriceValue(BillableDetail d) {
		return itemService.getCurrentPriceValue(d.getId(), getOrderDate(), PURCHASE);
	}

	@Override
	public void updateUponRouteValidation(String route) throws Exception {
		if (!isNew() || getOrderDate() == null)
			return;
		if (findByOrderDateAndRoute(getOrderDate(), route) != null)
			throw new DuplicateException(ocsDate() + route);
		setRoute(route);
	}

	private void setRoute(String route) {
		get().setSuffix(route);
	}

	@Override
	public void validateNameIsOfClient(String name) throws Exception {
		if (name != null && !name.equalsIgnoreCase(clientName))
			throw new InvalidWorkBookFormatException("B1", clientName);
	}

	@Override
	public void validateNamesAreOfRoutes() throws Exception {
		if (map.isEmpty())
			throw new InvalidWorkBookFormatException("in row 4 from column B", "route names");
	}
}
