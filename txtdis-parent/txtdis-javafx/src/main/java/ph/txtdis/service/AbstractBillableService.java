package ph.txtdis.service;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.type.UserType.MANAGER;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import ph.txtdis.dto.Billable;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.dto.Booking;
import ph.txtdis.dto.Item;
import ph.txtdis.dto.Keyed;
import ph.txtdis.exception.DateInThePastException;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;
import ph.txtdis.exception.UnauthorizedUserException;
import ph.txtdis.exception.UnpickedBookingException;
import ph.txtdis.type.QualityType;
import ph.txtdis.type.UomType;
import ph.txtdis.util.ClientTypeMap;
import ph.txtdis.util.DateTimeUtils;

public abstract class AbstractBillableService implements BillableService, CustomerSearchableService {

	private static Logger logger = getLogger(AbstractBillableService.class);

	@Autowired
	private HolidayService holidayService;

	@Autowired
	private RestServerService serverService;

	@Autowired
	private SavingService<Billable> savingService;

	@Autowired
	private SpunService<Billable, Long> spunService;

	@Autowired
	private ClientTypeMap typeMap;

	@Value("${go.live}")
	private String goLive;

	@Autowired
	protected ChannelService channelService;

	@Autowired
	protected CredentialService credentialService;

	@Autowired
	protected CustomerService customerService;

	@Autowired
	protected ItemService itemService;

	@Autowired
	protected ReadOnlyService<Billable> billableReadOnlyService;

	@Autowired
	protected SyncService syncService;

	@Value("${prefix.module}")
	protected String modulePrefix;

	protected BigDecimal qty;

	protected Billable billable;

	protected Item item;

	protected Long numId;

	protected UomType uom;

	public AbstractBillableService() {
		reset();
	}

	protected void confirmBookingExists(String id, Billable b) throws NotFoundException {
		if (b == null)
			throw new NotFoundException(getReferencePrompt(b) + " " + id);
	}

	protected void confirmBookingHasBeenPicked(String id, Billable b) throws Exception {
		if (b.getPickListId() == null)
			throw new UnpickedBookingException(getReferencePrompt(b), id);
	}

	@Override
	public BillableDetail createDetail() {
		BillableDetail sd = new BillableDetail();
		sd.setId(item.getId());
		sd.setItemName(item.getName());
		sd.setUom(uom);
		sd = setQty(sd);
		sd.setQuality(quality());
		sd.setQtyPerCase(getQtyPerCase(sd));
		logger.info("\n    QtyPerCase = " + sd.getQtyPerCase());
		return sd;
	}

	protected BillableDetail setQty(BillableDetail sd) {
		sd.setInitialQty(qty);
		return sd;
	}

	protected QualityType quality() {
		return QualityType.GOOD;
	}

	private int getQtyPerCase(BillableDetail sd) {
		UomType uom = sd.getUom();
		return uom != UomType.CS ? 0 : itemService.getQtyPerUom(item, uom).intValue();
	}

	protected Billable findById(Long id) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		return findBillable("/" + getSpunModule() + "?id=" + id);
	}

	protected Billable findBillable(String endPt) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		return billableReadOnlyService.module(getModule()).getOne(endPt);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Billable find(String id) throws Exception {
		String endpt = "/" + getSpunModule() + "?id=" + id;
		Billable e = findBillable(endpt);
		if (e == null)
			throw new NotFoundException(getModuleId() + id);
		return e;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Billable get() {
		if (billable == null)
			reset();
		return billable;
	}

	@Override
	public BigDecimal getBadOrderAllowanceValue() {
		return get().getBadOrderAllowanceValue();
	}

	@Override
	public String getBilledBy() {
		return get().getBilledBy();
	}

	@Override
	public ZonedDateTime getBilledOn() {
		return get().getBilledOn();
	}

	@Override
	public Long getBookingId() {
		return get().getBookingId();
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
	public String getCustomerAddress() {
		return get().getCustomerAddress();
	}

	@Override
	public Long getCustomerId() {
		return get().getCustomerId();
	}

	@Override
	public String getCustomerName() {
		return get().getCustomerName();
	}

	@Override
	public String getDateLabelName() {
		return "Date";
	}

	@Override
	public String getDecidedBy() {
		return get().getDecidedBy();
	}

	@Override
	public ZonedDateTime getDecidedOn() {
		return get().getDecidedOn();
	}

	@Override
	public List<BillableDetail> getDetails() {
		return get().getDetails();
	}

	@Override
	public List<String> getDiscounts() {
		return get().getDiscounts();
	}

	@Override
	public LocalDate getDueDate() {
		return get().getDueDate();
	}

	@Override
	public String getFontIcon() {
		return getTypeMap().icon(getSpunModule());
	}

	@Override
	public BigDecimal getGrossValue() {
		return get().getGrossValue();
	}

	@Override
	public Long getId() {
		return get().getId();
	}

	@Override
	public Boolean getIsValid() {
		return get().getIsValid();
	}

	@Override
	public Item getItem() {
		return item;
	}

	@Override
	public String getItemName() {
		return item == null ? null : item.getDescription();
	}

	@Override
	public ItemService getItemService() {
		return itemService;
	}

	@Override
	public String getModule() {
		return "billable";
	}

	@Override
	public Long getNumId() {
		return get().getNumId();
	}

	@Override
	public LocalDate getOrderDate() {
		return get().getOrderDate();
	}

	@Override
	public List<String> getPayments() {
		return get().getPayments();
	}

	@Override
	public String getPrefix() {
		return get().getPrefix();
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
	public BigDecimal getQtyPerUom(UomType uom) {
		return getItemService().getQtyPerUom(getItem(), uom);
	}

	@Override
	@SuppressWarnings("unchecked")
	public ReadOnlyService<Billable> getReadOnlyService() {
		return billableReadOnlyService;
	}

	@Override
	public String getReceivedBy() {
		return get().getReceivedBy();
	}

	@Override
	public ZonedDateTime getReceivedOn() {
		return get().getReceivedOn();
	}

	@Override
	public String getReceivingModifiedBy() {
		return get().getReceivingModifiedBy();
	}

	@Override
	public ZonedDateTime getReceivingModifiedOn() {
		return get().getReceivingModifiedOn();
	}

	@Override
	public Long getReceivingId() {
		return get().getReceivingId();
	}

	protected String getReferencePrompt(Billable b) {
		return getReferencePrompt();
	}

	@Override
	public String getRemarks() {
		return get().getRemarks();
	}

	@Override
	@SuppressWarnings("unchecked")
	public SavingService<Billable> getSavingService() {
		return savingService;
	}

	@Override
	public SpunService<Billable, Long> getSpunService() {
		return spunService;
	}

	@Override
	public String getSuffix() {
		return get().getSuffix();
	}

	@Override
	public String getTitleText() {
		return credentialService.username() + "@" + modulePrefix + " " + BillableService.super.getTitleText();
	}

	@Override
	public BigDecimal getTotalValue() {
		return get().getTotalValue();
	}

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public void invalidate() {
		get().setIsValid(false);
	}

	@Override
	public boolean isAppendable() {
		return false;
	}

	protected boolean isNewAndOffSite() {
		return isNew() && isOffSite();
	}

	@Override
	public boolean isOffSite() {
		return serverService.isOffSite();
	}

	@Override
	public List<Booking> listUnpicked(LocalDate d) {
		return getList("/unpicked?date=" + d).stream().map(a -> toBooking(a)).collect(toList());
	}

	protected List<Billable> getList(String endPt) {
		try {
			return billableReadOnlyService.module(getModule()).getList(endPt);
		} catch (Exception e) {
			return emptyList();
		}
	}

	protected Booking toBooking(Billable a) {
		Booking b = new Booking();
		b.setId(a.getBookingId());
		b.setCustomer(a.getCustomerName());
		b.setLocation(a.getCustomerLocation());
		b.setRoute(a.getRoute());
		return b;
	}

	protected LocalDate nextWorkDay() {
		return holidayService.nextWorkDay(today());
	}

	@Override
	public void reset() {
		nullifyAll();
		set(new Billable());
	}

	protected void nullifyAll() {
		billable = null;
		item = null;
		numId = null;
		qty = null;
		uom = null;
	}

	@Override
	public void searchForCustomer(String name) throws Exception {
		customerService.search(name);
	}

	@Override
	public <T extends Keyed<Long>> void set(T t) {
		if (t == null)
			return;
		billable = (Billable) t;
	}

	@Override
	public void setDetails(List<BillableDetail> l) {
		get().setDetails(l);
	}

	@Override
	public void setId(Long id) {
		get().setId(id);
	}

	protected void setReceivedByUser() {
		get().setReceivedBy(credentialService.username());
	}

	protected void verifyUserAuthorization() throws UnauthorizedUserException {
		if (credentialService.isUser(MANAGER))
			return;
	}

	protected void verifyNotInThePast(LocalDate d) throws DateInThePastException {
		if (d.isBefore(goLiveDate()))
			return;
		if (d.isBefore(syncService.getServerDate())) {
			reset();
			throw new DateInThePastException();
		}
	}

	protected LocalDate goLiveDate() {
		return DateTimeUtils.toDate(goLive);
	}

	protected String getSeller() {
		return credentialService.username();
	}

	@Override
	public void setItem(Item item) {
		this.item = item;
	}

	@Override
	public void setItemUponValidation(long id) throws Exception {
		if (id == 0)
			return;
		setItem(null);
		setItem(verifyItem(id));
	}

	@Override
	public void setOrderDate(LocalDate d) {
		get().setOrderDate(d);
	}

	@Override
	public void setRemarks(String remarks) {
		get().setRemarks(remarks);
	}

	@Override
	public void setQtyUponValidation(UomType uom, BigDecimal qty) throws Exception {
		this.uom = uom;
		this.qty = qty;
	}

	@Override
	public void setUnpaidValue(BigDecimal unpaid) {
		get().setUnpaidValue(unpaid);
	}

	protected LocalDate today() {
		return syncService.getServerDate();
	}

	@Override
	public Item verifyItem(Long id) throws Exception {
		return confirmItemExistsAndIsNotDeactivated(id);
	}
}
