package ph.txtdis.service;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.type.UserType.COLLECTOR;
import static ph.txtdis.util.NumberUtils.formatId;
import static ph.txtdis.util.NumberUtils.isZero;
import static ph.txtdis.util.SpringUtil.username;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.math.BigDecimal.ZERO;

import static ph.txtdis.type.ModuleType.SALES_ORDER;
import static ph.txtdis.type.ModuleType.SALES_RETURN;

import ph.txtdis.dto.Billing;
import ph.txtdis.dto.CreationTracked;
import ph.txtdis.dto.Customer;
import ph.txtdis.dto.Item;
import ph.txtdis.dto.ItemList;
import ph.txtdis.dto.Keyed;
import ph.txtdis.exception.DeactivatedException;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidDateSequenceException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.exception.NotPickedBookingIdException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;
import ph.txtdis.info.SuccessfulSaveInfo;
import ph.txtdis.type.ModuleType;
import ph.txtdis.util.TypeMap;

@Service("billingService")
public class BillingService implements CreationTracked, Detailed, Listed<Billing>, Reset, Serviced<Long> {

	private class AlreadyReceivedBookingIdException extends Exception {

		private static final long serialVersionUID = -8123802272614410524L;

		public AlreadyReceivedBookingIdException(String reference, Long id, Long receivingId) {
			super(reference + " No. " + id + "'s items\n"//
					+ "have been received in its\n"//
					+ "R/R No. " + receivingId);
		}
	}

	private static Logger logger = getLogger(BillingService.class);

	@Autowired
	private CustomerService customerService;

	@Autowired
	private ItemService itemService;

	@Autowired
	private ReadOnlyService<Billing> readOnlyService;

	@Autowired
	private SavingService<Billing> savingService;

	@Autowired
	private SpunService<Billing, Long> spunService;

	@Autowired
	private UserService userService;

	private Billing billing;

	private Item item;

	private ItemList receivingDetail;

	private List<ItemList> originalDetails;

	private ModuleType type;

	public BillingService() {
		reset();
	}

	public ItemList createDetail(int cases, int bottles) {
		ItemList sd = new ItemList();
		sd.setItem(item);
		sd.setInitialCount(cases * item.getBottlePerCase() + bottles);
		sd.setPriceValue(item.getPriceValue());
		return sd;
	}

	public Billing findById(String id) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, InvalidException, NotFoundException, NumberFormatException, RestException {
		Billing b = findById(Long.valueOf(id));
		if (b == null)
			throw new NotFoundException(getModuleId() + " No. " + id);
		return b;
	}

	public Item findItem(Long id) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, InvalidException, RestException, NotFoundException {
		Item i = itemService.findByCode(id);
		if (i == null)
			throw new NotFoundException("Item Code " + id);
		return i;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Billing get() {
		if (billing == null)
			reset();
		return billing;
	}

	@Override
	public String getAlternateName() {
		switch (type) {
			case SALES_RETURN:
			case SALES_ORDER:
				return reference();
			default:
				return null;
		}
	}

	public Billing getBillable(Long id) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, InvalidException, RestException {
		return readOnlyService.module(getModule()).getOne("/find?id=" + id);
	}

	@Override
	public String getCreatedBy() {
		return get().getCreatedBy();
	}

	@Override
	public ZonedDateTime getCreatedOn() {
		return get().getCreatedOn();
	}

	public Customer getCustomer() {
		return get().getCustomer();
	}

	public Long getCustomerId() {
		return getCustomer() == null ? null : getCustomer().getCode();
	}

	public String getCustomerName() {
		return getCustomer() == null ? null : getCustomer().getName();
	}

	public List<ItemList> getDetails() {
		if (get().getDetails() == null)
			return emptyList();
		return isASalesReturn() ? nonZeroReturnedQtyBillableDetails() : nonZeroQtyBillableDetails();
	}

	@Override
	public String getFontIcon() {
		return new TypeMap().icon(getSpunModule());
	}

	@Override
	public String getHeaderText() {
		switch (type) {
			case SALES_ORDER:
				return "Sales Order";
			case SALES_RETURN:
				return "S/O Returns";
			default:
				return "";
		}
	}

	@Override
	public Long getId() {
		return get().getId();
	}

	public String getIdPrompt() {
		if (isASalesOrder())
			return "S/I(D/R)";
		return getAlternateName();
	}

	public Long getItemCode() {
		return item == null ? null : item.getCode();
	}

	public String getItemName() {
		return item == null ? null : item.getName();
	}

	@Override
	public String getModule() {
		return "billing";
	}

	@Override
	public String getOpenDialogHeading() {
		return "Open a(n) " + getHeaderText();
	}

	@Override
	public String getOrderNo() {
		return formatId((isASalesReturn() ? get().getReceivingId() : getId()));
	}

	@Override
	@SuppressWarnings("unchecked")
	public ReadOnlyService<Billing> getReadOnlyService() {
		return readOnlyService;
	}

	public List<String> getReceivableItemNames() {
		try {
			List<String> l = originalDetails.stream().map(ItemList::getItem).map(Item::getName).collect(toList());
			l = l.stream().filter(n -> nonReturnedItem(n)).collect(toList());
			return l;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public SavingService<Billing> getSavingService() {
		return savingService;
	}

	@Override
	public Long getSpunId() {
		if (isNew())
			return null;
		return isASalesReturn() ? get().getReceivingId() : getId();
	}

	@Override
	public String getSpunModule() {
		switch (type) {
			case SALES_ORDER:
				return "salesOrder";
			case SALES_RETURN:
				return "salesReturn";
			default:
				return "billing";
		}
	}

	@Override
	public SpunService<Billing, Long> getSpunService() {
		return spunService;
	}

	public boolean importExists() throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, InvalidException, RestException {
		Billing b = readOnlyService.module(getModule()).getOne("/imported");
		return b != null;
	}

	@Override
	public boolean isAppendable() {
		return true;
	}

	public boolean isASalesOrder() {
		return type == SALES_ORDER;
	}

	public boolean isASalesReturn() {
		return type == SALES_RETURN;
	}

	public List<String> listCollectors() {
		String d = get().getCollector();
		return d == null ? allCollectors() : asList(d);
	}

	public List<ItemList> nonZeroReturnedQtyBillableDetails() {
		try {
			return get().getDetails().stream().filter(d -> !isZero(d.getReturnedFraction())).collect(toList());
		} catch (Exception e) {
			e.printStackTrace();
			return emptyList();
		}
	}

	@Override
	public void reset() {
		nullifyAll();
		set(new Billing());
	}

	@Override
	public <T extends Keyed<Long>> void save() throws SuccessfulSaveInfo, NoServerConnectionException,
			StoppedServerException, FailedAuthenticationException, InvalidException, RestException {
		get().setOrderDate(LocalDate.now());
		saveAll();
	}

	@Override
	public <T extends Keyed<Long>> void set(T t) {
		if (t != null)
			billing = (Billing) t;
	}

	public void setCustomer(Customer c) {
		get().setCustomer(c);
	}

	public void setItem(Item i) {
		item = i;
	}

	public void setItemUponValidation(long id) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, InvalidException, DuplicateException, NotFoundException, RestException {
		item = null;
		item = validateItem(id);
	}

	public void setType(ModuleType t) {
		type = t;
	}

	public ItemList updateReturnedQty(int cases, int bottles) {
		int bottlePerCase = receivingDetail.getItem().getBottlePerCase();
		receivingDetail.setReturnedCount(cases * bottlePerCase + bottles);
		return receivingDetail;
	}

	public void updateSummaries(List<ItemList> l) {
		get().setDetails(l);
		get().setTotalValue(totalAmount());
		get().setTotalQty(totalQty());
	}

	public void updateUponBookingIdValidation(long id) throws NotFoundException, NoServerConnectionException,
			StoppedServerException, FailedAuthenticationException, InvalidException, AlreadyReceivedBookingIdException,
			NotPickedBookingIdException, InvalidDateSequenceException, DeactivatedException, RestException {
		if (id == 0)
			return;
		Billing b = validateBooking(id);
		updateBasedOnBooking(b);
	}

	public void updateUponCustomerIdValidation(Long id) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, InvalidException, RestException, NotFoundException {
		if (!isNew() || id == 0 || getCustomer() != null)
			return;
		logger.info("Customer code from input = " + id);
		get().setCustomer(customerService.findByCode(id));
		logger.info("Customer from dbase = " + getCustomer());
		if (getCustomer() == null)
			throw new NotFoundException("Customer Code " + id);
	}

	public Item validateItem(Long id) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, InvalidException, DuplicateException, NotFoundException, RestException {
		Item i = findItem(id);
		confirmItemIsNotOnList(i);
		return i;
	}

	private List<String> allCollectors() {
		return userService.listNamesByRole(COLLECTOR);
	}

	private String bookingModule() {
		switch (type) {
			case SALES_RETURN:
				return "salesOrder";
			default:
				return null;
		}
	}

	private void confirmBookingExists(long id, Billing b) throws NotFoundException {
		if (b == null)
			throw new NotFoundException(reference() + " No. " + id);
	}

	private void confirmBookingIsStillOpen(long id, Billing b) throws InvalidException,
			AlreadyReceivedBookingIdException, NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, NotPickedBookingIdException, InvalidDateSequenceException {
		Long receivingId = b.getReceivingId();
		if (receivingId != null)
			throw new AlreadyReceivedBookingIdException(reference(), id, receivingId);
	}

	private void confirmItemIsNotOnList(Item i) throws DuplicateException {
		if (getDetails().stream().anyMatch(d -> d.getItemCode() == i.getCode()))
			throw new DuplicateException(i.getName());
	}

	private Billing findById(Long id) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, InvalidException, RestException {
		return readOnlyService.module(getModule()).getOne("/" + getSpunModule() + "?id=" + id);
	}

	private boolean nonReturnedItem(String n) {
		return !getDetails().stream()
				.anyMatch(d -> isZero(d.getNetFraction()) && d.getItem() != null && n.equals(d.getItem().getName()));
	}

	private List<ItemList> nonZeroQtyBillableDetails() {
		try {
			return get().getDetails().stream().filter(d -> !isZero(d.getNetFraction())).collect(toList());
		} catch (Exception e) {
			e.printStackTrace();
			return emptyList();
		}
	}

	private void nullifyAll() {
		item = null;
		receivingDetail = null;
	}

	private String reference() {
		return "S/O";
	}

	private void saveAll() throws SuccessfulSaveInfo, NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, InvalidException, RestException {
		set(savingService.module(getModule()).save(get()));
		if (get() != null)
			throw new SuccessfulSaveInfo(getModuleId() + getOrderNo());
	}

	private void setBillableData(Billing b) {
		if (isASalesReturn())
			get().setDetails(null);
		set(b);
	}

	private BigDecimal totalAmount() {
		try {
			return getDetails().stream().map(ItemList::getSubtotalValue).reduce(ZERO, BigDecimal::add);
		} catch (Exception e) {
			return null;
		}
	}

	private BigDecimal totalQty() {
		try {
			return getDetails().stream().map(d -> new BigDecimal(d.getNetFraction().doubleValue())).reduce(ZERO,
					BigDecimal::add);
		} catch (Exception e) {
			return ZERO;
		}
	}

	private void updateBasedOnBooking(Billing b) {
		originalDetails = b.getDetails();
		setBillableData(b);
		if (isASalesReturn())
			get().setReceivedBy(username());
	}

	private Billing validateBooking(long id) throws NotFoundException, NoServerConnectionException,
			StoppedServerException, FailedAuthenticationException, InvalidException, AlreadyReceivedBookingIdException,
			NotPickedBookingIdException, InvalidDateSequenceException, DeactivatedException, RestException {
		Billing b = readOnlyService.module(getModule()).getOne("/" + bookingModule() + "?id=" + id);
		confirmBookingExists(id, b);
		confirmBookingIsStillOpen(id, b);
		return b;
	}
}
