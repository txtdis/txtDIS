package ph.txtdis.mgdc.ccbpi.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.dto.Booking;
import ph.txtdis.dto.Keyed;
import ph.txtdis.exception.DateInThePastException;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.exception.UnpickedBookingException;
import ph.txtdis.mgdc.ccbpi.dto.Item;
import ph.txtdis.mgdc.service.HolidayService;
import ph.txtdis.service.CustomerSearchableService;
import ph.txtdis.service.RestClientService;
import ph.txtdis.type.QualityType;
import ph.txtdis.type.UomType;
import ph.txtdis.util.ClientTypeMap;
import ph.txtdis.util.DateTimeUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.type.UserType.AUDITOR;
import static ph.txtdis.type.UserType.MANAGER;
import static ph.txtdis.util.DateTimeUtils.getServerDate;
import static ph.txtdis.util.UserUtils.isUser;
import static ph.txtdis.util.UserUtils.username;

public abstract class AbstractBillableService //
	implements BillableService,
	CustomerSearchableService {

	private static Logger logger = getLogger(AbstractBillableService.class);

	@Autowired
	protected CustomerService customerService;

	@Autowired
	protected BommedDiscountedPricedValidatedItemService itemService;

	@Value("${prefix.module}")
	protected String modulePrefix;

	protected BigDecimal qty;

	protected Billable billable;

	protected Item item;

	protected UomType uom;

	@Autowired
	private HolidayService holidayService;

	@Autowired
	private RestClientService<Billable> restClientService;

	@Autowired
	private ClientTypeMap typeMap;

	@Value("${go.live}")
	private String goLive;

	public AbstractBillableService() {
		reset();
	}

	@Override
	public void reset() {
		billable = null;
		item = null;
		qty = null;
		uom = null;
	}

	@Override
	public boolean canApprove() {
		return !isNew() && (isAuditor() || isUser(MANAGER));
	}

	@Override
	public boolean isAuditor() {
		return isUser(AUDITOR);
	}

	@Override
	public String getAlternateName() {
		return null;
	}

	@Override
	public BillableDetail createDetail() {
		BillableDetail sd = new BillableDetail();
		sd.setId(item.getId());
		sd.setItemName(item.getName());
		sd.setUom(uom);
		sd = setQty(sd);
		sd.setQuality(quality());
		sd.setQtyPerCase(qtyPerCase(sd.getUom()));
		sd.setItemVendorNo(item.getVendorNo());
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

	protected int qtyPerCase(UomType uom) {
		return uom != UomType.CS ? 0 : itemService.getQtyPerUom(item, uom).intValue();
	}

	public Billable findBillable(String endPt) throws Exception {
		return getRestClientService()//
			.module("billable")//
			.getOne(endPt);
	}

	@Override
	@SuppressWarnings("unchecked")
	public RestClientService<Billable> getRestClientService() {
		return restClientService;
	}

	@Override
	public String getBilledBy() {
		return get().getBilledBy();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Billable get() {
		if (billable == null)
			set(new Billable());
		return billable;
	}

	@Override
	public <T extends Keyed<Long>> void set(T t) {
		if (t == null)
			return;
		billable = (Billable) t;
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
	public void setBookingId(Long id) {
		get().setBookingId(id);
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
	public void setDetails(List<BillableDetail> l) {
		if (l != null)
			l = l.stream().filter(d -> d != null).collect(toList());
		get().setDetails(l);
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
	public BigDecimal getGrossValue() {
		return get().getGrossValue();
	}

	@Override
	public Long getId() {
		return get().getId();
	}

	@Override
	public void setId(Long id) {
		get().setId(id);
	}

	@Override
	public Boolean getIsValid() {
		return get().getIsValid();
	}

	@Override
	public String getItemName() {
		return item == null ? null : item.getDescription();
	}

	@Override
	public String getModuleName() {
		return "billable";
	}

	@Override
	public LocalDate getOrderDate() {
		return get().getOrderDate();
	}

	@Override
	public void setOrderDate(LocalDate d) {
		get().setOrderDate(d);
	}

	@Override
	public List<String> getPayments() {
		return get().getPayments();
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
	public BommedDiscountedPricedValidatedItemService getItemService() {
		return itemService;
	}

	@Override
	public Item getItem() {
		return item;
	}

	@Override
	public void setItem(Item item) {
		this.item = item;
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

	@Override
	public String getRemarks() {
		return get().getRemarks();
	}

	@Override
	public void setRemarks(String remarks) {
		if (remarks == null || remarks.trim().isEmpty())
			return;
		if (getRemarks() != null)
			remarks = getRemarks() + "\n" + remarks;
		get().setRemarks(remarks);
	}

	@Override
	public String getTitleName() {
		return getUserAndModuleNames() + BillableService.super.getTitleName();
	}

	protected String getUserAndModuleNames() {
		return getUsername() + "@" + modulePrefix + " ";
	}

	@Override
	public String getUsername() {
		return username();
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

	protected List<Billable> findBillables(String endPt) {
		try {
			return restClientService.module("billable").getList(endPt);
		} catch (Exception e) {
			return emptyList();
		}
	}

	@Override
	public void searchForCustomer(String name) throws Exception {
		customerService.search(name);
	}

	protected void verifyNotInThePast(LocalDate d) throws Exception {
		if (d.isBefore(goLive()))
			return;
		if (d.isBefore(today())) {
			reset();
			throw new DateInThePastException();
		}
	}

	protected LocalDate goLive() {
		return DateTimeUtils.toDate(goLive);
	}

	@Override
	public LocalDate today() {
		return getServerDate();
	}

	protected String getSeller() {
		return getUsername();
	}

	@Override
	public void setItemUponValidation(long id) throws Exception {
		if (id == 0)
			return;
		setItem(null);
		setItem(verifyItem(id));
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

	@Override
	public void updateSummaries(List<BillableDetail> items) {
		setDetails(items);
	}

	protected void confirmBookingExists(String id, Billable b) throws NotFoundException {
		if (b == null)
			throw new NotFoundException("ID No. " + id);
	}

	protected void confirmBookingHasBeenPicked(String id, Billable b) throws Exception {
		if (b.getPickListId() == null)
			throw new UnpickedBookingException(getReferencePrompt(b), id);
	}

	protected String getReferencePrompt(Billable b) {
		return getReferencePrompt();
	}

	protected LocalDate nextWorkDay() {
		return nextWorkDay(today());
	}

	protected LocalDate nextWorkDay(LocalDate d) {
		return holidayService.nextWorkDay(d);
	}

	protected LocalDate previousWorkDay() {
		return previousWorkDay(today());
	}

	protected LocalDate previousWorkDay(LocalDate d) {
		return holidayService.previousWorkDay(d);
	}

	protected void setReceivedByUser() {
		get().setReceivedBy(getUsername());
	}

	protected Booking toBooking(Billable a) {
		Booking b = new Booking();
		b.setId(a.getId());
		b.setBookingId(a.getBookingId());
		b.setCustomer(a.getCustomerName());
		b.setLocation(a.getCustomerLocation());
		b.setRoute(a.getRoute());
		return b;
	}
}
