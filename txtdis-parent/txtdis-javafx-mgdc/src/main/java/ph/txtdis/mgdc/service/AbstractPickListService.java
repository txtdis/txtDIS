package ph.txtdis.mgdc.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.dto.Booking;
import ph.txtdis.exception.NothingToPickException;
import ph.txtdis.exception.UnauthorizedUserException;
import ph.txtdis.service.TruckService;
import ph.txtdis.service.UserService;
import ph.txtdis.type.DeliveryType;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.type.UserType.*;
import static ph.txtdis.util.UserUtils.isUser;

public abstract class AbstractPickListService //
	extends AbstractLoadingService //
	implements PickListService {

	private static final String PICK_UP = DeliveryType.PICK_UP.toString();

	private static Logger logger = getLogger(AbstractPickListService.class);

	@Autowired
	protected UserService userService;

	protected List<Booking> unpickedBookings;

	@Autowired
	private TruckService truckService;

	@Override
	public String getAlternateName() {
		return "Pick List";
	}

	@Override
	public String getCreatedBy() {
		return get().getCreatedBy();
	}

	@Override
	public ZonedDateTime getCreatedOn() {
		return get().getCreatedOn();
	}

	@Override
	public String getPrintedBy() {
		return get().getPrintedBy();
	}

	@Override
	public ZonedDateTime getPrintedOn() {
		return get().getPrintedOn();
	}

	@Override
	public List<String> listDrivers() {
		String d = get().getDriver();
		return d == null ? drivers() : asList(d);
	}

	private List<String> drivers() {
		return !isNew() || get().getTruck() == null ? null : userService.listNamesByRole(DRIVER);
	}

	@Override
	public List<String> listHelpers() {
		String a = get().getAssistant();
		return a == null ? helpers() : asList(a);
	}

	protected List<String> helpers() {
		return !isNew() && get().getAssistant() == null ? null : userService.listNamesByRole(HELPER);
	}

	@Override
	public List<String> listRouteNames() {
		List<String> unpickedRoutes = unpickedRoutes();
		logger.info("\n    Unpicked@listRouteNames = " + unpickedRoutes);
		if (unpickedRoutes.isEmpty() || getBookings() == null)
			return unpickedRoutes;
		return unpickedRouteNames(new ArrayList<>(unpickedRoutes));
	}

	private List<String> unpickedRoutes() {
		return unpickedBookings.stream() //
			.map(b -> routeName(b))//
			.filter(n -> n != null) //
			.distinct().sorted().collect(toList());
	}

	@Override
	public List<Booking> getBookings() {
		return get().getBookings();
	}

	private List<String> unpickedRouteNames(List<String> unpickedList) {
		List<String> pickedList = getBookings().stream().map(b -> routeName(b)).distinct().collect(toList());
		logger.info("\n    Picked@unpickedRouteNames = " + pickedList);
		if (pickedList != null)
			unpickedList.removeAll(pickedList);
		logger.info("\n    Unpicked@unpickedRouteNames = " + unpickedList);
		return unpickedList;
	}

	protected String routeName(Booking b) {
		String route = b.getRoute();
		return route != null ? route : PICK_UP.toString();
	}

	@Override
	public void setBookings(List<Booking> bookings) {
		get().setBookings(bookings);
	}

	@Override
	public List<String> listTrucks() {
		return isNew() ? allTrucks() : truck();
	}

	private List<String> allTrucks() {
		List<String> l = new ArrayList<>();
		l.add(PICK_UP);
		l.addAll(truckService.listNames());
		return l;
	}

	private List<String> truck() {
		String s = get().getTruck();
		return asList(s == null ? PICK_UP : s);
	}

	@Override
	public List<Booking> listUnpickedBookings(String route) {
		return unpickedBookings.stream().filter(b -> route.equals(routeName(b))).collect(toList());
	}

	@Override
	public void print() throws Exception {
		set(getRestClientService().module(getModuleName()).getOne("/print?id=" + getId()));
	}

	@Override
	public String getModuleName() {
		return "pickList";
	}

	@Override
	public void reset() {
		super.reset();
		unpickedBookings = Collections.emptyList();
	}

	@Override
	public void setAssistantUponValidation(String a) throws Exception {
		if (a != null && !a.isEmpty() && isNew())
			setAssistant(a);
	}

	private void setAssistant(String a) {
		get().setAssistant(a);
	}

	@Override
	public void setDriverUponValidation(String d) throws Exception {
		if (d != null && !d.isEmpty() && isNew())
			setDriver(d);
	}

	private void setDriver(String d) {
		get().setDriver(d);
	}

	@Override
	public void setPickDateUponValidation(LocalDate d) throws Exception {
		if (d == null || !isNew())
			return;
		if (!isUser(MANAGER) && !isUser(STOCK_CHECKER))
			throw new UnauthorizedUserException("Stock Checkers Only");
		get().setPickDate(d);
	}

	@Override
	public void setRemarks(String s) {
		get().setRemarks(s);
	}

	@Override
	public void setTruckUponValidation(String truck) throws Exception {
		verifyThereAreBookingsToBePickedOnPickDate();
		noTruckAndDriverAndHelperForPickUp(truck);
	}

	private void verifyThereAreBookingsToBePickedOnPickDate() throws Exception {
		unpickedBookings = listUnpicked();
		logger.info("\n    UnpickedBookings@verifyThereAreBookingsToBePickedOnPickDate = " + unpickedBookings);
		if (unpickedBookings.isEmpty())
			throw new NothingToPickException(getPickDate());
	}

	private void noTruckAndDriverAndHelperForPickUp(String t) {
		get().setTruck(t.equals(PICK_UP) ? null : t);
		setDriver(null);
		setLeadAssistant(null);
		setAssistant(null);
	}

	protected abstract List<Booking> listUnpicked();

	@Override
	public LocalDate getPickDate() {
		return get().getPickDate();
	}

	private void setLeadAssistant(String h) {
		get().setLeadAssistant(h);
	}

	@Override
	public void unpick(Booking b) {
		unpickedBookings.add(b);
	}
}
