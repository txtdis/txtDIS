package ph.txtdis.service;

import static java.time.LocalDate.now;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static ph.txtdis.type.UserType.DRIVER;
import static ph.txtdis.type.UserType.HELPER;
import static ph.txtdis.type.UserType.MANAGER;
import static ph.txtdis.type.UserType.STOCK_CHECKER;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import ph.txtdis.dto.Booking;
import ph.txtdis.dto.Keyed;
import ph.txtdis.dto.PickList;
import ph.txtdis.exception.AlreadyAssignedPersonException;
import ph.txtdis.exception.DateInThePastException;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.NotAllowedOffSiteTransactionException;
import ph.txtdis.exception.NotNextWorkDayException;
import ph.txtdis.exception.NothingToPickException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;
import ph.txtdis.exception.UnauthorizedUserException;
import ph.txtdis.util.ClientTypeMap;
import ph.txtdis.util.DateTimeUtils;

public abstract class AbstractPickListService implements PickListService {

	private static final String PICK_UP = "PICK-UP";

	@Autowired
	private HolidayService holidayService;

	@Autowired
	private RestServerService serverService;

	@Autowired
	private SavingService<PickList> savingService;

	@Autowired
	private SpunService<PickList, Long> spunService;

	@Autowired
	private SyncService syncService;

	@Autowired
	private TruckService truckService;

	@Autowired
	private ClientTypeMap typeMap;

	@Autowired
	protected CredentialService credentialService;

	@Autowired
	protected ReadOnlyService<PickList> readOnlyService;

	@Autowired
	protected UserService userService;

	@Value("${go.live}")
	private String goLive;

	@Value("${prefix.module}")
	protected String modulePrefix;

	private List<Booking> unpickedBookings;

	private List<PickList> pickLists;

	private PickList pickList;

	public AbstractPickListService() {
		reset();
	}

	@Override
	@SuppressWarnings("unchecked")
	public PickList get() {
		if (pickList == null)
			reset();
		return pickList;
	}

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
	public String getHelper() {
		return get().getHelper();
	}

	@Override
	public Long getId() {
		return get().getId();
	}

	@Override
	public String getModule() {
		return "pickList";
	}

	@Override
	@SuppressWarnings("unchecked")
	public ReadOnlyService<PickList> getReadOnlyService() {
		return readOnlyService;
	}

	@Override
	@SuppressWarnings("unchecked")
	public SavingService<PickList> getSavingService() {
		return savingService;
	}

	@Override
	public SpunService<PickList, Long> getSpunService() {
		return spunService;
	}

	@Override
	public SyncService getSyncService() {
		return syncService;
	}

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public String getTitleText() {
		return credentialService.username() + "@" + modulePrefix + " " + PickListService.super.getTitleText();
	}

	public List<Booking> listBookings(String route) {
		List<Booking> picked = unpickedBookings.stream().filter(b -> b.getRoute().equals(route)).collect(toList());
		picked.forEach(p -> unpickedBookings.remove(p));
		return picked;
	}

	@Override
	public List<String> listDrivers() {
		return get().getTruck() == null ? null : drivers();
	}

	@Override
	public List<String> listHelpers() {
		String helper = get().getHelper();
		if (helper != null)
			return asList(helper);
		return get().getDriver() == null ? allHelpers() : leadHelpers();
	}

	@Override
	public List<String> listRoutes() {
		List<String> unpickedRoutes = unpickedBookings.stream().map(b -> b.getRoute()).distinct().sorted()
				.collect(toList());
		List<Booking> bookings = get().getBookings();
		if (unpickedRoutes.isEmpty() || bookings == null)
			return unpickedRoutes;

		ArrayList<String> unpickedRouteList = new ArrayList<>(unpickedRoutes);
		List<String> pickedRoutes = bookings.stream().map(b -> b.getRoute()).distinct().collect(toList());
		if (pickedRoutes != null)
			unpickedRouteList.removeAll(pickedRoutes);
		return unpickedRouteList;
	}

	@Override
	public List<String> listTrucks() {
		return isNew() ? allTrucks() : truck();
	}

	@Override
	public List<Booking> listUnpickedBookings(String route) {
		return unpickedBookings.stream().filter(b -> route.equals(b.getRoute())).collect(toList());
	}

	@Override
	public void print() throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException,
			RestException, InvalidException {
		set(readOnlyService.module(getModule()).getOne("/print?id=" + getId()));
	}

	@Override
	public void reset() {
		set(new PickList());
		instantiateLists();
	}

	@Override
	public <T extends Keyed<Long>> void set(T t) {
		pickList = (PickList) t;
	}

	@Override
	public void setDriverUponValidation(String d) throws AlreadyAssignedPersonException {
		if (d == null || !isNew())
			return;
		if (hasPersonBeenAssigned(d))
			throw new AlreadyAssignedPersonException(d);
		get().setDriver(d);
	}

	@Override
	public void setHelperUponValidation(String h) throws AlreadyAssignedPersonException {
		if (h == null || !isNew())
			return;
		if (hasPersonBeenAssigned(h))
			throw new AlreadyAssignedPersonException(h);
		get().setHelper(h);
	}

	@Override
	public void setPickDateUponValidation(LocalDate d) throws DateInThePastException, NotNextWorkDayException,
			NotAllowedOffSiteTransactionException, UnauthorizedUserException {
		if (d == null || !isNew())
			return;
		if (isOffSite())
			throw new NotAllowedOffSiteTransactionException();
		if (!credentialService.isUser(MANAGER) && !credentialService.isUser(STOCK_CHECKER))
			throw new UnauthorizedUserException("Stock Checkers Only");
		if (!d.isBefore(goLiveDate()))
			verifyDateIsTodayOrTheNextWorkDay(d);
		get().setPickDate(d);
	}

	private LocalDate goLiveDate() {
		return DateTimeUtils.toDate(goLive);
	}

	@Override
	public void setTruckUponValidation(String t) throws NothingToPickException {
		if (t == null || t.trim().isEmpty() || !isNew())
			return;
		if (getPickDate() != null)
			verifyThereAreBookingsToBePickedOnPickDate();
		assignPersonsBasedOnTruckStatus(t);
	}

	@Override
	public void unpick(Booking b) {
		unpickedBookings.add(b);
	}

	private List<String> allDrivers() {
		return userService.listNamesByRole(DRIVER);
	}

	protected List<String> allHelpers() {
		return userService.listNamesByRole(HELPER);
	}

	private List<String> allTrucks() {
		List<String> l = new ArrayList<>();
		l.add(PICK_UP);
		l.addAll(truckService.listNames());
		return l;
	}

	private void assignPersonsBasedOnTruckStatus(String t) {
		if (!t.equals(PICK_UP)) {
			Optional<PickList> o = pickLists.stream().filter(p -> p.getTruck().equals(t)).findFirst();
			if (o.isPresent()) {
				get().setHelper(o.get().getHelper());
				get().setThirdPerson(o.get().getThirdPerson());
			}
		}
		get().setTruck(t.equals(PICK_UP) ? null : t);
	}

	private List<String> drivers() {
		String d = get().getDriver();
		return d == null ? allDrivers() : asList(d);
	}

	protected boolean hasPersonBeenAssigned(String t) {
		return hasPersonBeenAssignedInOtherTrucks(t) || hasPersonBeenAssignedInCurrentTruck(t);
	}

	protected boolean hasPersonBeenAssignedInOtherTrucks(String t) {
		return pickLists.stream()
				.anyMatch(p -> t.equals(p.getDriver()) || t.equals(p.getHelper()) || t.equals(p.getThirdPerson()));
	}

	private boolean hasPersonBeenAssignedInCurrentTruck(String t) {
		return t.equals(get().getDriver()) || t.equals(get().getHelper()) || t.equals(get().getThirdPerson());
	}

	private void instantiateLists() {
		unpickedBookings = new ArrayList<>();
		pickLists = new ArrayList<>();
	}

	private List<String> leadHelpers() {
		String h = get().getHelper();
		return h == null ? allHelpers() : asList(h);
	}

	private List<String> truck() {
		String s = get().getTruck();
		return asList(s == null ? PICK_UP : s);
	}

	private void verifyDateIsTodayOrTheNextWorkDay(LocalDate date)
			throws DateInThePastException, NotNextWorkDayException {
		if (date.isBefore(now()))
			throw new DateInThePastException();
	}

	private void verifyThereAreBookingsToBePickedOnPickDate() throws NothingToPickException {
		unpickedBookings = listUnpicked();
		if (unpickedBookings.isEmpty())
			throw new NothingToPickException(getPickDate());
	}

	protected abstract List<Booking> listUnpicked();

	public boolean isOffSite() {
		return serverService.isOffSite();
	}

	@Override
	public HolidayService getHolidayService() {
		return holidayService;
	}

	@Override
	public void setId(Long id) {
		get().setId(id);
	}

	@Override
	public String getRemarks() {
		return get().getRemarks();
	}

	@Override
	public void setRemarks(String s) {
		get().setRemarks(s);
	}

	@Override
	public List<Booking> getBookings() {
		return get().getBookings();
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
	public LocalDate getPickDate() {
		return get().getPickDate();
	}

	@Override
	public void setBookings(List<Booking> bookings) {
		get().setBookings(bookings);
	}

	protected void setThirdPersonUponValidation(String h) throws AlreadyAssignedPersonException {
		if (h == null || h.isEmpty() || !isNew())
			return;
		if (hasPersonBeenAssigned(h))
			throw new AlreadyAssignedPersonException(h);
		get().setThirdPerson(h);
	}
}
