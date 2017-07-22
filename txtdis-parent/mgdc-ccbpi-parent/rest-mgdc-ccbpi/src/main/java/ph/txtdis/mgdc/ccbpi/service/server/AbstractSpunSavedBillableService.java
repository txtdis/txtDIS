package ph.txtdis.mgdc.ccbpi.service.server;

import static java.time.DayOfWeek.SATURDAY;
import static java.util.stream.Collectors.toList;
import static ph.txtdis.type.DeliveryType.PICK_UP;
import static ph.txtdis.util.NumberUtils.nullIfZero;
import static ph.txtdis.util.NumberUtils.toCurrencyText;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import ph.txtdis.domain.TruckEntity;
import ph.txtdis.domain.UserEntity;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.mgdc.ccbpi.domain.BillableDetailEntity;
import ph.txtdis.mgdc.ccbpi.domain.BillableEntity;
import ph.txtdis.mgdc.ccbpi.domain.CustomerEntity;
import ph.txtdis.mgdc.ccbpi.domain.ItemEntity;
import ph.txtdis.mgdc.ccbpi.domain.PickListEntity;
import ph.txtdis.mgdc.ccbpi.repository.BillableRepository;
import ph.txtdis.mgdc.domain.RouteEntity;
import ph.txtdis.mgdc.service.server.AbstractServerSpunSavedSearchedEntityService;
import ph.txtdis.service.ServerUserService;
import ph.txtdis.type.UomType;
import ph.txtdis.util.DateTimeUtils;

public abstract class AbstractSpunSavedBillableService //
		extends AbstractServerSpunSavedSearchedEntityService<BillableRepository, BillableEntity, Billable, Long> //
		implements BillingDataService, SpunSavedBillableService {

	protected static final String CANCELLED = "CANCELLED";

	protected static final String EXTRACTED_FROM_INVALIDATED_S_I_D_R_NO = "EXTRACTED FROM INVALIDATED S/I(D/R) No. ";

	@Autowired
	protected CustomerService customerService;

	@Autowired
	protected ItemService itemService;

	@Autowired
	protected ServerUserService userService;

	@Value("${go.live}")
	private String goLive;

	@Value("${pricing.uom}")
	private UomType pricingUom;

	@Value("${grace.period.invoicing}")
	private Long invoicingGracePeriod;

	@Override
	public Billable toBookingIdOnlyBillable(BillableEntity e) {
		if (e == null)
			return null;
		Billable b = new Billable();
		b.setBookingId(e.getBookingId());
		return b;
	}

	@Override
	public Billable toModel(BillableEntity e) {
		Billable b = toOrderNoOnlyBillable(e);
		if (b == null)
			return null;
		b.setId(e.getId());
		b.setRemarks(e.getRemarks());
		b.setCreatedBy(e.getCreatedBy());
		b.setCreatedOn(e.getCreatedOn());
		b = customerOnlyBillable(b, e);
		b.setOrderDate(e.getOrderDate());
		b.setBookingId(bookingId(e));
		b.setBilledBy(e.getBilledBy());
		b.setBilledOn(e.getBilledOn());
		b.setDetails(details(e));
		b.setDueDate(e.getDueDate());
		b.setIsRma(e.getRma());
		b.setTotalValue(e.getTotalValue());
		b = setReceivingData(b, e);
		return setPickingData(b, e.getPicking());
	}

	protected Billable customerOnlyBillable(Billable b, BillableEntity e) {
		if (b == null)
			b = new Billable();
		CustomerEntity c = e.getCustomer();
		if (c == null)
			return b;
		b.setCustomerId(c.getId());
		b.setCustomerName(c.getName());
		b.setRoute(routeName(e, c));
		b.setSeller(seller(e, c));
		return b;
	}

	private String routeName(BillableEntity e, CustomerEntity c) {
		RouteEntity route = c.getRoute(e.getOrderDate());
		return route == null ? null : route.getName();
	}

	private String seller(BillableEntity e, CustomerEntity c) {
		String seller = c.getSeller(e.getOrderDate());
		if (seller == null)
			return null;
		UserEntity u = userService.findEntityByPrimaryKey(seller);
		return u.getSurname() + ", " + u.getName();
	}

	private Long bookingId(BillableEntity e) {
		Long id = e.getBookingId();
		return id == null && e.getCreatedOn() != null ? e.getId() : id;
	}

	private List<BillableDetail> details(BillableEntity e) {
		List<BillableDetailEntity> details = e.getDetails();
		return details.stream().map(d -> toBillableDetail(d)).collect(Collectors.toList());
	}

	private BillableDetail toBillableDetail(BillableDetailEntity e) {
		ItemEntity item = e.getItem();
		BillableDetail d = new BillableDetail();
		d.setId(item.getId());
		d.setItemName(item.getName());
		d.setItemVendorNo(item.getVendorId());
		d.setUom(e.getUom());
		d.setInitialQty(e.getInitialQty());
		d.setReturnedQty(e.getReturnedQty());
		d.setQuality(e.getQuality());
		d.setPriceValue(e.getPriceValue());
		d.setQtyPerCase(getQtyPerCase(e, item));
		return d;
	}

	private int getQtyPerCase(BillableDetailEntity e, ItemEntity item) {
		if (pricingUom != UomType.CS)
			return 0;
		return itemService.getCountPerCase(item);
	}

	protected String getTotalInText(BigDecimal t) {
		return "[TOTAL] " + toCurrencyText(t);
	}

	private Billable setReceivingData(Billable b, BillableEntity e) {
		b.setReceivedBy(e.getReceivedBy());
		b.setReceivedOn(e.getReceivedOn());
		b.setReceivingId(e.getReceivingId());
		b.setReceivingModifiedBy(e.getReceivingModifiedBy());
		b.setReceivingModifiedOn(e.getReceivingModifiedOn());
		return b;
	}

	private Billable setPickingData(Billable b, PickListEntity p) {
		if (p == null)
			return b;
		b.setPickedBy(p.getCreatedBy());
		b.setPickedOn(p.getCreatedOn());
		b.setPickListId(p.getId());
		b.setPrintedBy(p.getPrintedBy());
		b.setPrintedOn(p.getPrintedOn());
		b.setTruck(truck(p));
		b.setDriver(driver(p));
		b.setHelper(helper(p));
		return b;
	}

	private String truck(PickListEntity p) {
		TruckEntity t = p.getTruck();
		return t == null ? PICK_UP.toString() : t.getName();
	}

	private String driver(PickListEntity p) {
		UserEntity u = p.getDriver();
		return fullName(u);
	}

	private String fullName(UserEntity u) {
		return u == null ? null : u.getName() + " " + u.getSurname();
	}

	private String helper(PickListEntity p) {
		UserEntity u = p.getAssistant();
		return fullName(u);
	}

	@Override
	public BillableEntity toEntity(Billable b) {
		return b == null ? null : setEntity(b);
	}

	protected BillableEntity setEntity(Billable b) {
		return b.getId() == null ? create(b) : update(b);
	}

	protected BillableEntity create(Billable b) {
		BillableEntity e = orderNoOnlyEntity(b);
		e.setOrderDate(b.getOrderDate());
		e.setCustomer(customer(b));
		e.setDueDate(b.getDueDate());
		e.setBookingId(bookingId(b));
		e.setRma(b.getIsRma());
		e.setRemarks(b.getRemarks());
		e.setDetails(entityDetails(e, b));
		e.setTotalValue(nullIfZero(b.getTotalValue()));
		return e;
	}

	protected BillableEntity orderNoOnlyEntity(Billable b) {
		BillableEntity e = new BillableEntity();
		return setThreePartOrderNo(e, b);
	}

	private CustomerEntity customer(Billable b) {
		return customerService.findEntity(b);
	}

	private Long bookingId(Billable b) {
		Long id = b.getBookingId();
		return id != null ? id : incrementBookingId();
	}

	private Long incrementBookingId() {
		BillableEntity b = repository.findFirstByBookingIdNotNullOrderByBookingIdDesc();
		return b == null || b.getBookingId() == null ? 1L : b.getBookingId() + 1;
	}

	protected List<BillableDetailEntity> entityDetails(BillableEntity e, Billable b) {
		List<BillableDetail> l = b.getDetails();
		return l == null ? null
				: l.stream() //
						.filter(d -> d != null) //
						.map(d -> detail(e, d)) //
						.collect(toList());
	}

	private BillableDetailEntity detail(BillableEntity e, BillableDetail bd) {
		BillableDetailEntity ed = new BillableDetailEntity();
		ed.setBilling(e);
		ed.setItem(itemService.findEntity(bd));
		ed.setUom(bd.getUom());
		ed.setInitialQty(nullIfZero(bd.getInitialQty()));
		ed.setQuality(bd.getQuality());
		ed.setReturnedQty(nullIfZero(bd.getReturnedQty()));
		ed.setPriceValue(nullIfZero(bd.getPriceValue()));
		return ed;
	}

	protected BillableEntity update(Billable b) {
		BillableEntity e = repository.findOne(b.getId());
		return update(e, b);
	}

	protected BillableEntity update(BillableEntity e, Billable b) {
		e.setRemarks(b.getRemarks());
		return e;
	}

	@Override
	public Billable toOrderNoOnlyBillable(BillableEntity e) {
		return e == null ? null : setOrderNo(new Billable(), e);
	}

	protected Billable setOrderNo(Billable b, BillableEntity e) {
		b.setPrefix(e.getPrefix());
		b.setSuffix(e.getSuffix());
		return b;
	}

	@Override
	public Billable toSpunIdOnlyBillable(Billable billable) {
		Billable b = new Billable();
		b.setId(billable.getId());
		return b;
	}

	@Override
	public Billable toTotalValueOnlyBillable(BigDecimal total) {
		Billable b = new Billable();
		b.setTotalValue(total);
		return b;
	}

	protected LocalDate billingCutoff(LocalDate date) {
		date = date.minusDays(Long.valueOf(invoicingGracePeriod));
		if (date.getDayOfWeek() == SATURDAY)
			date = date.minusDays(1L);
		return date;
	}

	protected LocalDate goLive() {
		return DateTimeUtils.toDate(goLive);
	}
}