package ph.txtdis.mgdc.ccbpi.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.dto.Booking;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.exception.UnauthorizedUserException;
import ph.txtdis.info.Information;
import ph.txtdis.info.SuccessfulSaveInfo;
import ph.txtdis.mgdc.ccbpi.dto.Channel;
import ph.txtdis.mgdc.ccbpi.dto.Customer;
import ph.txtdis.mgdc.ccbpi.exception.NoDeliveryRouteException;
import ph.txtdis.mgdc.service.HolidayService;
import ph.txtdis.mgdc.service.RouteService;
import ph.txtdis.type.OrderConfirmationType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.type.OrderConfirmationType.*;
import static ph.txtdis.type.UserType.MANAGER;
import static ph.txtdis.type.UserType.SALES_ENCODER;
import static ph.txtdis.util.DateTimeUtils.getServerDate;
import static ph.txtdis.util.NumberUtils.isPositive;
import static ph.txtdis.util.UserUtils.isUser;

@Service("orderConfirmationService")
public class OrderConfirmationServiceImpl //
	extends AbstractOrderConfirmationService //
	implements OrderConfirmationService {

	private static Logger logger = getLogger(OrderConfirmationServiceImpl.class);

	@Autowired
	private HolidayService holidayService;

	@Autowired
	private RouteService routeService;

	@Override
	public String getAlternateName() {
		return "OCS";
	}

	@Override
	public String getDelivery() {
		return get().getRoute();
	}

	@Override
	public BigDecimal getDeliveredValue(String collector, LocalDate start, LocalDate end) {
		try {
			Billable b =
				orderService().getOne("/deliveredValue?receivedFrom=" + collector + "&start=" + start + "&end=" + end);
			return b.getTotalValue();
		} catch (Exception e) {
			return BigDecimal.ZERO;
		}
	}

	@Override
	public String getHeaderName() {
		return "Order Confirmation";
	}

	@Override
	public String getModuleName() {
		return "orderConfirmation";
	}

	@Override
	public List<BigDecimal> getTotals(List<BillableDetail> l) {
		return asList(totalQty(l), totalValue(l));
	}

	private BigDecimal totalQty(List<BillableDetail> l) {
		return l.stream() //
			.filter(d -> d != null) //
			.map(BillableDetail::getInitialQtyInCases) //
			.reduce(ZERO, BigDecimal::add);
	}

	private BigDecimal totalValue(List<BillableDetail> l) {
		get().setTotalValue(total(l));
		return getTotalValue();
	}

	private BigDecimal total(List<BillableDetail> l) {
		return l.stream() //
			.filter(d -> d != null) //
			.map(BillableDetail::getInitialSubtotalValue) //
			.reduce(ZERO, BigDecimal::add);
	}

	@Override
	public boolean isAppendable() {
		boolean b = !isUndeliveredOrStockOut(get());
		logger.info("\n    NotUndeliveredOrStockOut@isAppendable = " + b);
		return b && super.isAppendable();
	}

	private boolean isUndeliveredOrStockOut(Billable b) {
		return isDelivery(b, UNDELIVERED) || isDelivery(b, STOCK_OUT);
	}

	private boolean isDelivery(Billable b, OrderConfirmationType type) {
		String p = b.getPrefix();
		return p == null ? false : p.equalsIgnoreCase(type.toString());
	}

	@Override
	public List<String> listRoutes() {
		return routeService.listNames();
	}

	@Override
	public List<OrderConfirmationType> listTypes() {
		if (isNew())
			return types();
		return asList(OrderConfirmationType.valueOf(getOrderType()));
	}

	private List<OrderConfirmationType> types() {
		List<OrderConfirmationType> types = new ArrayList<>(asList(OrderConfirmationType.values()));
		if (getSequenceId() == null || getSequenceId() == 1)
			types.removeAll(asList(PARTIAL, STOCK_OUT, UNDELIVERED));
		else if (!openBlanketExists())
			types.remove(PARTIAL);
		else
			types.removeAll(asList(BLANKET, BUFFER));
		if (isDeliveryMoreThanTwoDaysFromOrderDate(getOrderDate(), getDeliveryDate()) //
			|| (isDeliveryTwoDaysFromOrderDate(getOrderDate(), getDeliveryDate()) //
			&& !isPreviousDayASundayOrHoliday(getDeliveryDate())))
			types.removeAll(asList(MANUAL, REGULAR));
		return types;
	}

	@Override
	public Long getSequenceId() {
		return getBookingId();
	}

	private boolean openBlanketExists() {
		Billable b = previousOrder(1);
		return b == null ? false : isDelivery(b, BLANKET) && isPositive(balanceQty(b));
	}

	private boolean isDeliveryMoreThanTwoDaysFromOrderDate(LocalDate order, LocalDate due) {
		return noOfDays(order, due) > 2;
	}

	@Override
	public LocalDate getOrderDate() {
		if (get().getOrderDate() == null)
			setOrderDate(getServerDate());
		return get().getOrderDate();
	}

	@Override
	public LocalDate getDeliveryDate() {
		if (getDueDate() == null)
			setRecommendedDeliveryDate(getOrderDate());
		return getDueDate();
	}

	private boolean isDeliveryTwoDaysFromOrderDate(LocalDate order, LocalDate due) {
		return noOfDays(order, due) == 2;
	}

	private boolean isPreviousDayASundayOrHoliday(LocalDate d) {
		LocalDate previous = d.minusDays(1L);
		return isSunday(previous) || isAHoliday(previous);
	}

	private Billable previousOrder(long currentId) {
		try {
			logger.info("\n    OrderDate@previousOrder = " + getOrderDate());
			logger.info("\n    CustomerId@previousOrder = " + getCustomerId());
			logger.info("\n    SequenceId@previousOrder = " + currentId);
			return findByOrderDateAndOutletIdAndSequenceId(getOrderDate(), getCustomerId(), currentId - 1);
		} catch (Exception e) {
			return null;
		}
	}

	private BigDecimal balanceQty(Billable b) {
		return b.getDetails().stream() //
			.map(d -> d.getFinalQty()) //
			.reduce(ZERO, BigDecimal::add);
	}

	private long noOfDays(LocalDate order, LocalDate due) {
		return order.until(due, DAYS);
	}

	private void setRecommendedDeliveryDate(LocalDate orderDate) {
		LocalDate d = holidayService.nextWorkDay(orderDate);
		get().setDueDate(d);
	}

	private boolean isSunday(LocalDate d) {
		return d.getDayOfWeek() == SUNDAY;
	}

	private boolean isAHoliday(LocalDate yesterday) {
		return holidayService.isAHoliday(yesterday);
	}

	@Override
	protected Billable findByOrderDateAndOutletIdAndSequenceId(LocalDate orderDate, Long outletId, Long sequenceId)
		throws Exception {
		return orderService().getOne("/ocs?date=" + orderDate + "&outletId=" + outletId + "&count=" + sequenceId);
	}

	private void setSequenceId(Billable b) {
		Long id = 1L;
		if (b != null)
			id = id + b.getBookingId();
		get().setBookingId(id);
	}

	@Override
	public List<Booking> listUnpicked(LocalDate d) {
		try {
			List<Billable> l = orderService().getList("/unpicked?date=" + d);
			return l.stream().map(a -> toBooking(a)).collect(toList());
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}

	@Override
	protected Booking toBooking(Billable t) {
		Booking b = new Booking();
		b.setId(t.getId());
		b.setBookingId(t.getBookingId());
		b.setCustomer(t.getCustomerName());
		b.setLocation(orderConfirmationNo(t));
		b.setRoute(t.getSuffix());
		b.setDeliveryRoute(t.getRoute());
		return b;
	}

	@Override
	public void openByOpenDialogInputtedKey(String key) throws Exception {
		set(findByOrderNo(key));
	}

	@Override
	public void openByDoubleClickedTableCellKey(String pk) throws Exception {
		logger.info("\n    EndPt@openByDoubleClickedTableCellKey = " + pk);
		set(findByEndPt("/" + pk));
	}

	@Override
	public void resetAndSetOrderDate(LocalDate d) {
		reset();
		setOrderDate(d);
	}

	@Override
	public void save() throws Information, Exception {
		if (getRoute() == null || getRoute().isEmpty())
			get().setPrefix(REGULAR.toString());
		set(save(get()));
		throw new SuccessfulSaveInfo(getSavingInfo());
	}

	@Override
	public String getSavingInfo() {
		return getAbbreviatedModuleNoPrompt() + getModuleNo();
	}

	@Override
	public void setDeliveryDateUponValidation(LocalDate d) throws Exception {
		if (d != null && getOrderDate() != null && d.isBefore(getOrderDate()))
			throw new InvalidException("Delivery cannot be before order date");
		get().setDueDate(d);
		setDetails(null);
	}

	@Override
	public void setRoute(String route) {
		get().setRoute(route);
	}

	@Override
	public void updateUponCustomerVendorIdValidation(Long id) throws Exception {
		validateUser();
		setCustomerUponVendorIdValidation(id);
		getPreviousOrderToSetDeliveryDateAndSequenceIdAndTypeList();
		setDetails(null);
	}

	private void validateUser() throws Exception {
		if (!isUser(MANAGER) && !isUser(SALES_ENCODER))
			throw new UnauthorizedUserException("Sales Encoders Only");
	}

	private void setCustomerUponVendorIdValidation(Long id) throws Exception {
		Customer c = customerService.findByVendorId(id);
		if (c == null)
			throw new NotFoundException(CUSTOMER_NO + id);
		setCustomer(c);
	}

	private void getPreviousOrderToSetDeliveryDateAndSequenceIdAndTypeList() {
		Billable b = previousOrder(1);
		logger.info("\n    PreviousOrder@getPreviousOrderToSetDeliveryDateAndSequenceIdAndTypeList = " + b);
		setSequenceId(b);
		setRecommendedDeliveryDate(orderDate(b));
	}

	@Override
	public void setCustomer(Customer c) throws Exception {
		setCustomerId(c.getId());
		get().setCustomerName(c.getName());
		get().setCustomerVendorId(c.getVendorId());
		get().setSuffix(channel(c));
		get().setRoute(route(c));
	}

	private LocalDate orderDate(Billable b) {
		return b == null ? getOrderDate() : b.getOrderDate();
	}

	@Override
	public void setCustomerId(Long id) {
		get().setCustomerId(id);
	}

	private String channel(Customer c) {
		Channel r = c.getChannel();
		return r == null ? null : r.getName();
	}

	private String route(Customer c) throws Exception {
		try {
			return customerService.getRoute(c, getDeliveryDate()).getName();
		} catch (NullPointerException e) {
			throw new NoDeliveryRouteException(c.getName());
		}
	}

	@Override
	public void updateUponTypeValidation(OrderConfirmationType type) throws Exception {
		if (type == null)
			return;
		if (isUndeliveredOrStockOut(type) || isRegularMixedWithManual(type))
			importOriginalDetails();
		setType(type.toString());
	}

	private boolean isUndeliveredOrStockOut(OrderConfirmationType type) {
		return type == UNDELIVERED || type == STOCK_OUT;
	}

	private boolean isRegularMixedWithManual(OrderConfirmationType type) {
		return type == MANUAL && getSequenceId() != null && getSequenceId() > 1;
	}

	private void importOriginalDetails() throws Exception {
		List<BillableDetail> details = details(new ArrayList<>(), null, getSequenceId());
		setDetails(details);
	}

	private void setType(String type) {
		get().setPrefix(type.toString());
	}

	private List<BillableDetail> details(List<BillableDetail> details, Billable b, Long i) {
		do {
			b = previousOrder(i--);
			logger.info("\n    PreviousOrder@details = " + b);
			logger.info("\n    PreviousDetails@details = " + b.getDetails());
			logger.info("\n    CurrentDetails@details = " + details);
			logger.info("\n    SequenceId@details = " + i);
			details.addAll(b.getDetails());
		} while (i > 1 && !isUndeliveredOrStockOut(b));
		return details;
	}
}
