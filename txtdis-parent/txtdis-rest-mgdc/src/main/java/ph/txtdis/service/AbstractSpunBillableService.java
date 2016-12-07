package ph.txtdis.service;

import static java.math.BigDecimal.ZERO;
import static java.time.DayOfWeek.SATURDAY;
import static java.util.Collections.emptyList;
import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.util.DateTimeUtils.toDate;
import static ph.txtdis.util.DateTimeUtils.toDateDisplay;
import static ph.txtdis.util.NumberUtils.toCurrencyText;
import static ph.txtdis.util.NumberUtils.toPercentRate;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import ph.txtdis.domain.BillableDetailEntity;
import ph.txtdis.domain.BillableEntity;
import ph.txtdis.domain.CustomerDiscountEntity;
import ph.txtdis.domain.CustomerEntity;
import ph.txtdis.domain.ItemEntity;
import ph.txtdis.domain.PickListEntity;
import ph.txtdis.domain.RemittanceDetailEntity;
import ph.txtdis.domain.RemittanceEntity;
import ph.txtdis.domain.TruckEntity;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.repository.BillingRepository;
import ph.txtdis.repository.CustomerDiscountRepository;
import ph.txtdis.repository.CustomerRepository;
import ph.txtdis.repository.RemittanceDetailRepository;
import ph.txtdis.type.UomType;
import ph.txtdis.util.NumberUtils;

public abstract class AbstractSpunBillableService
		extends AbstractCreateService<BillingRepository, BillableEntity, Billable, Long> implements SpunBillableService {

	private static Logger logger = getLogger(AbstractSpunBillableService.class);

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private CustomerDiscountRepository discount;

	@Autowired
	private RemittanceDetailRepository remittanceDetail;

	@Autowired
	protected ItemService itemService;

	@Autowired
	protected PickListService pickingService;

	@Value("${go.live}")
	private String goLive;

	@Value("${pricing.uom}")
	private String pricingUom;

	@Value("${vendor.id}")
	private String vendorId;

	@Value("${grace.period.invoicing}")
	private String invoicingGracePeriod;

	protected LocalDate billingCutoff(Date d, String seller) {
		LocalDate date = d.toLocalDate();
		if (!seller.equalsIgnoreCase("ALL"))
			date = date.minusDays(Long.valueOf(invoicingGracePeriod));
		if (date.getDayOfWeek() == SATURDAY)
			date = date.minusDays(1L);
		return date;
	}

	@Override
	public Billable findById(Long id) {
		return toDTO(repository.findOne(id));
	}

	@Override
	public Billable first() {
		return firstSpun();
	}

	@Override
	public Billable firstToSpin() {
		return toSpunIdOnlyBillable(firstSpun());
	}

	@Override
	public Billable last() {
		return lastSpun();
	}

	@Override
	public Billable lastToSpin() {
		return toSpunIdOnlyBillable(lastSpun());
	}

	@Override
	public Billable toBookingIdOnlyBillable(BillableEntity e) {
		if (e == null)
			return null;
		Billable b = new Billable();
		b.setBookingId(e.getBookingId());
		return b;
	}

	@Override
	public Billable toOrderNoOnlyBillable(BillableEntity e) {
		if (e == null)
			return null;
		Billable b = new Billable();
		b.setPrefix(e.getPrefix());
		b.setNumId(e.getNumId());
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

	private Long bookingId(BillableEntity b) {
		Long id = b.getBookingId();
		return id == null && b.getCreatedOn() != null ? b.getId() : id;
	}

	protected LocalDate goLive() {
		return toDate(goLive);
	}

	@Override
	public Billable toDTO(BillableEntity e) {
		Billable b = toOrderNoOnlyBillable(e);
		if (b == null)
			return null;
		b.setId(e.getId());
		b.setRemarks(e.getRemarks());
		b.setCreatedBy(e.getCreatedBy());
		b.setCreatedOn(e.getCreatedOn());
		if (e.getBookingId() == null)
			return b;
		b = customerOnlyBillable(b, e);
		b.setAlias(getAlias(e));
		b.setBadOrderAllowanceValue(e.getBadOrderAllowanceValue());
		b.setBilledBy(e.getBilledBy());
		b.setBilledOn(e.getBilledOn());
		b.setBookingId(bookingId(e));
		b.setDecidedBy(e.getDecidedBy());
		b.setDecidedOn(e.getDecidedOn());
		b.setDetails(details(e));
		b.setDiscounts(discounts(e));
		b.setDueDate(e.getDueDate());
		b.setGrossValue(e.getGrossValue());
		b.setIsRma(e.getRma());
		b.setIsValid(e.getIsValid());
		b.setOrderDate(e.getOrderDate());
		b.setPayments(listPayments(e));
		b.setReceivedBy(e.getReceivedBy());
		b.setReceivedOn(e.getReceivedOn());
		b.setReceivingId(e.getReceivingId());
		b.setReceivingModifiedBy(e.getReceivingModifiedBy());
		b.setReceivingModifiedOn(e.getReceivingModifiedOn());
		b.setRoute(getRouteName(e));
		b.setTotalValue(e.getTotalValue());
		b.setTruck(truck(e));
		b.setUnpaidValue(e.getUnpaidValue());
		return setPickingData(b, e.getPicking());
	}

	private Billable setPickingData(Billable b, PickListEntity p) {
		if (p == null)
			return b;
		b.setPickedBy(p.getCreatedBy());
		b.setPickedOn(p.getCreatedOn());
		b.setPickListId(p.getId());
		b.setPrintedBy(p.getPrintedBy());
		b.setPrintedOn(p.getPrintedOn());
		return b;
	}

	protected Billable customerOnlyBillable(Billable b, BillableEntity e) {
		if (b == null)
			b = new Billable();
		CustomerEntity c = e.getCustomer();
		if (c == null)
			return b;
		b.setCustomerId(c.getId());
		b.setCustomerVendorId(c.getVendorId());
		b.setCustomerName(c.getName());
		b.setCustomerAddress(c.getAddress());
		b.setCustomerLocation(c.getLocation());
		return b;
	}

	private String getAlias(BillableEntity e) {
		CustomerEntity c = e.getCustomer();
		if (c == null)
			return null;
		c = c.getParent();
		return c == null ? null : c.getName();
	}

	private String createEachLevelDiscountText(CustomerDiscountEntity d, BigDecimal total, BigDecimal net) {
		BigDecimal perLevel = net.multiply(toPercentRate(d.getPercent()));
		total = total.add(perLevel);
		net = net.subtract(total);
		return "[" + d.getLevel() + ": " + d.getPercent() + "%] " + toCurrencyText(perLevel);
	}

	private String createRemitIdAndDateAndPaymentText(RemittanceDetailEntity p) {
		RemittanceEntity r = p.getRemittance();
		return "[" + status(r) + ": " + r + " - " + toDateDisplay(r.getPaymentDate()) + "] "
				+ toCurrencyText(p.getPaymentValue());
	}

	private List<BillableDetail> details(BillableEntity b) {
		try {
			return b.getDetails().stream().map(d -> toBillableDetail(d)).collect(Collectors.toList());
		} catch (Exception e) {
			return emptyList();
		}
	}

	private List<String> discounts(BillableEntity b) {
		try {
			List<CustomerDiscountEntity> d = b.getCustomerDiscounts();
			return d.isEmpty() ? emptyList() : listDiscounts(b);
		} catch (Exception e) {
			return emptyList();
		}
	}

	private BigDecimal discountValue(BillableEntity b) {
		try {
			return b.getGrossValue().subtract(b.getTotalValue());
		} catch (Exception e) {
			return ZERO;
		}
	}

	private List<String> getEachLevelDiscountTextList(BillableEntity b, List<String> list) {
		BigDecimal net = b.getGrossValue();
		b.getCustomerDiscounts().forEach(d -> list.add(createEachLevelDiscountText(d, ZERO, net)));
		return list;
	}

	private List<String> getRemitIdAndDateAndPaymentTextList(List<RemittanceDetailEntity> r, List<String> list) {
		r.forEach(p -> list.add(createRemitIdAndDateAndPaymentText(p)));
		return list;
	}

	private String getTotalInText(BigDecimal t) {
		return "[TOTAL] " + toCurrencyText(t);
	}

	private Boolean isValid(RemittanceDetailEntity d) {
		try {
			return d.getRemittance().getIsValid();
		} catch (Exception e) {
			return false;
		}
	}

	private List<String> listDiscounts(BillableEntity e) {
		ArrayList<String> list = new ArrayList<>();
		if (e.getCustomerDiscounts().size() > 1)
			list.add(getTotalInText(discountValue(e)));
		return getEachLevelDiscountTextList(e, list);
	}

	private List<String> listPayments(List<RemittanceDetailEntity> e) {
		ArrayList<String> list = new ArrayList<>();
		if (e.size() > 1)
			list.add(getTotalInText(sumPayments(e)));
		return getRemitIdAndDateAndPaymentTextList(e, list);
	}

	private List<String> listPayments(BillableEntity e) {
		try {
			List<RemittanceDetailEntity> l = remittanceDetail.findByBilling(e);
			return l.isEmpty() ? emptyList() : listPayments(l);
		} catch (Exception ex) {
			return emptyList();
		}
	}

	private String getRouteName(BillableEntity e) {
		CustomerEntity c = e.getCustomer();
		if (c == null || c.getId().equals(vendorId()))
			return null;
		return getRouteName(e, c);
	}

	private String getRouteName(BillableEntity e, CustomerEntity c) {
		try {
			return c.getRouteHistory().stream()//
					.filter(r -> r.getStartDate().compareTo(e.getOrderDate()) <= 0)
					.max((a, b) -> a.getStartDate().compareTo(b.getStartDate()))//
					.get().getRoute().getName();
		} catch (Exception x) {
			return null;
		}
	}

	protected Long vendorId() {
		return Long.valueOf(vendorId);
	}

	private String status(RemittanceEntity r) {
		if (r.getIsValid() == null)
			return "PENDING";
		return r.getIsValid() == true ? "VALID" : "INVALID";
	}

	private BigDecimal sumPayments(List<RemittanceDetailEntity> payments) {
		try {
			return payments.stream().filter(d -> isValid(d)).map(d -> d.getPaymentValue()).reduce(ZERO,
					(a, b) -> a.add(b));
		} catch (Exception e) {
			return ZERO;
		}
	}

	private BillableDetail toBillableDetail(BillableDetailEntity e) {
		ItemEntity item = e.getItem();
		BillableDetail d = new BillableDetail();
		d.setId(item.getId());
		d.setItemName(item.getName());
		d.setItemVendorNo(item.getVendorId());
		d.setUom(e.getUom());
		d.setInitialQty(e.getInitialQty());
		d.setSoldQty(e.getSoldQty());
		d.setReturnedQty(e.getReturnedQty());
		d.setQuality(e.getQuality());
		d.setPriceValue(e.getPriceValue());
		d.setDiscountValue(e.getDiscountValue());
		d.setQtyPerCase(getQtyPerCase(e, item));
		d = setDaysLevel(d, e);
		return d;
	}

	private int getQtyPerCase(BillableDetailEntity e, ItemEntity item) {
		if (UomType.valueOf(pricingUom) != UomType.CS)
			return 0;
		return itemService.getQtyPerCase(item);
	}

	private BillableDetail setDaysLevel(BillableDetail bd, BillableDetailEntity ed) {
		if (ed.getOnPurchaseDaysLevel() == null)
			return bd;
		bd.setOnPurchaseDaysLevel(ed.getOnPurchaseDaysLevel());
		bd.setOnReceiptDaysLevel(ed.getOnReceiptDaysLevel());
		return bd;
	}

	private String truck(BillableEntity b) {
		PickListEntity p = b.getPicking();
		return p == null ? null : truck(p);
	}

	private String truck(PickListEntity p) {
		TruckEntity t = p.getTruck();
		return t == null ? "PICK-UP" : t.getName();
	}

	@Override
	public BillableEntity toEntity(Billable b) {
		return b == null ? null : getEntity(b);
	}

	private BillableEntity getEntity(Billable b) {
		return b.getId() == null ? create(b) : update(b);
	}

	private BillableEntity create(Billable b) {
		BillableEntity e = new BillableEntity();
		e.setPrefix(b.getPrefix());
		e.setSuffix(b.getSuffix());
		e.setNumId(b.getNumId());
		e.setOrderDate(b.getOrderDate());
		if (cancelled(b))
			return setCancelledData(e);
		e.setCustomer(customer(b));
		e.setDueDate(b.getDueDate());
		e.setCustomerDiscounts(customerDiscounts(b));
		e.setBookingId(getBookingId(b));
		e.setRma(b.getIsRma());
		e.setBadOrderAllowanceValue(b.getBadOrderAllowanceValue());
		e.setPicking(getPicking(b));
		e = setBillingAuditTrail(e, b);
		e = updateTotals(e, b);
		e = updateDecisionData(e, b);
		e = updateReceivingInfo(e, b);
		e.setDetails(details(e, b));
		return e;
	}

	private BillableEntity updateReceivingInfo(BillableEntity e, Billable b) {
		if (b.getReceivedBy() == null || e.getReceivedOn() != null)
			return e;
		e.setReceivedBy(b.getReceivedBy());
		e.setReceivedOn(ZonedDateTime.now());
		e.setReceivingId(receivingId());
		return e;
	}

	private PickListEntity getPicking(Billable b) {
		Long id = b.getPickListId();
		return id == null ? null : pickingService.findEntity(id);
	}

	protected boolean cancelled(Billable b) {
		return voided(b) || allReturned(b);
	}

	private boolean voided(Billable b) {
		boolean voided = b.getBookingId() == null && b.getCustomerId() == null && b.getCustomerName() == null;
		logger.info("\n    BillableIsVoid: " + voided);
		return voided;
	}

	private boolean allReturned(Billable b) {
		List<BillableDetail> details = b.getDetails();
		if (details == null || details.isEmpty())
			return true;
		BigDecimal qty = details.stream().map(getQty(b)).reduce(BigDecimal.ZERO, BigDecimal::add);
		boolean allReturned = NumberUtils.isZero(qty);
		logger.info("\n    BillableHasAllItemsReturned: " + allReturned);
		return allReturned;
	}

	private Function<BillableDetail, BigDecimal> getQty(Billable b) {
		if (b.getCustomerId() == null || !b.getCustomerId().equals(vendorId()))
			return BillableDetail::getQty;
		return BillableDetail::getQtyInDecimals;
	}

	private BillableEntity setCancelledData(BillableEntity e) {
		e.setRemarks("CANCELLED");
		e.setFullyPaid(true);
		return e;
	}

	private BillableEntity updateTotals(BillableEntity e, Billable b) {
		e.setGrossValue(b.getGrossValue());
		e.setTotalValue(b.getTotalValue());
		e.setUnpaidValue(b.getUnpaidValue());
		return e;
	}

	private BillableEntity updateDecisionData(BillableEntity e, Billable b) {
		if (b.getIsValid() == null && e.getIsValid() == null)
			return e;
		e.setIsValid(b.getIsValid());
		e.setRemarks(b.getRemarks());
		e.setDecidedBy(b.getDecidedBy());
		e.setDecidedOn(b.getDecidedOn());
		return e;
	}

	private List<CustomerDiscountEntity> customerDiscounts(Billable b) {
		try {
			return b.getDiscountIds().stream().map(id -> discount.findOne(id)).collect(Collectors.toList());
		} catch (Exception e) {
			return null;
		}
	}

	private BillableEntity setBillingAuditTrail(BillableEntity e, Billable b) {
		if (b.getBilledBy() == null || e.getBilledOn() != null)
			return e;
		e.setBilledBy(b.getBilledBy());
		e.setBilledOn(ZonedDateTime.now());
		e.setFullyPaid(isFullyPaid(b));
		return e;
	}

	private boolean isFullyPaid(Billable b) {
		return !NumberUtils.isPositive(b.getUnpaidValue());
	}

	private BillableEntity cancelIfAllReturned(BillableEntity e, Billable b) {
		if (allReturned(b))
			return setCancelledData(e);
		e.setFullyPaid(false);
		return e;
	}

	private Long getBookingId(Billable b) {
		Long id = b.getBookingId();
		return id != null ? id : incrementBookingId();
	}

	private Long incrementBookingId() {
		BillableEntity b = repository.findFirstByBookingIdNotNullOrderByBookingIdDesc();
		return b == null || b.getBookingId() == null ? 1L : b.getBookingId() + 1;
	}

	private List<BillableDetailEntity> details(BillableEntity e, Billable b) {
		return b.getDetails().stream().map(d -> detail(e, b, d)).collect(Collectors.toList());
	}

	private BillableDetailEntity detail(BillableEntity e, Billable b, BillableDetail bd) {
		BillableDetailEntity ed = new BillableDetailEntity();
		ed.setBilling(e);
		ed.setItem(itemService.findEntity(bd));
		ed.setUom(bd.getUom());
		ed.setInitialQty(bd.getInitialQty());
		ed.setQuality(bd.getQuality());
		ed.setReturnedQty(bd.getReturnedQty());
		ed.setPriceValue(bd.getPriceValue());
		ed.setDiscountValue(bd.getDiscountValue());
		ed = setDaysLevel(ed, b, bd);
		logger.info("\n    BillableDetailEntity: " //
				+ (b.getOrderNo().isEmpty() ? "NEW" : b.getOrderNo()) + ", " + ed.getItem() + ", " + ed.getQty());
		return ed;
	}

	private BillableDetailEntity setDaysLevel(BillableDetailEntity ed, Billable b, BillableDetail bd) {
		if (!isAPurchaseOrder(b))
			return ed;
		ed.setOnPurchaseDaysLevel(bd.getOnPurchaseDaysLevel());
		ed.setOnReceiptDaysLevel(bd.getOnReceiptDaysLevel());
		return ed;
	}

	private BillableEntity createReceivingData(BillableEntity e, Billable b) {
		if (b.getReceivedBy() == null || b.getReceivedOn() != null)
			return e;
		e = updateReceivingInfo(e, b);
		e.setDetails(receivingDetails(e, b));
		return e;
	}

	private CustomerEntity customer(Billable b) {
		Long id = b.getCustomerId();
		logger.info("\n    CustomerIdFromBillableForEntityConversion: " + id);
		return id == null ? customerRepository.findByNameIgnoreCase(b.getCustomerName()) : customerRepository.findOne(id);
	}

	private Long generateDeliveryId() {
		BillableEntity b = repository.findFirstByNumIdLessThanOrderByIdDesc(0L);
		return b.getNumId() - 1;
	}

	private BillableEntity fullyPayIfReturnedItemOrderHasBeenInvoiced(BillableEntity e, Billable b) {
		if (b.getIsRma() != null)
			e.setFullyPaid(true);
		return e;
	}

	private boolean isAPurchaseOrder(Billable b) {
		return b.getCustomerId() == vendorId();
	}

	private Long numId(Billable b) {
		Long id = b.getNumId();
		return id == null ? generateDeliveryId() : id;
	}

	private List<BillableDetailEntity> receivingDetails(BillableEntity e, Billable b) {
		Map<Long, BillableDetailEntity> m = new HashMap<>();
		for (BillableDetailEntity ed : e.getDetails())
			m.put(ed.getItem().getId(), ed);
		for (BillableDetail bd : b.getDetails()) {
			Long id = bd.getId();
			BillableDetailEntity ed = m.get(id);
			if (ed != null) {
				ed.setReturnedQty(bd.getReturnedQty());
				m.put(id, ed);
			}
		}
		return new ArrayList<>(m.values());
	}

	private Long receivingId() {
		BillableEntity b = repository.findFirstByReceivingIdNotNullOrderByReceivingIdDesc();
		return 1 + (b == null ? 0L : b.getReceivingId());
	}

	private BillableEntity update(Billable b) {
		BillableEntity e = repository.findOne(b.getId());
		e = updateBillingData(e, b);
		e = updateReceivingData(e, b);
		e = updateDecisionData(e, b);
		e.setBadOrderAllowanceValue(b.getBadOrderAllowanceValue());
		e.setDetails(soldDetails(e, b));
		return e;
	}

	private BillableEntity updateBillingData(BillableEntity e, Billable b) {
		if (b.getIsValid() != null && b.getIsValid() == false)
			return nullifyBillingData(e);
		if (b.getBilledBy() == null || b.getBilledOn() != null)
			return e;
		e = setBilledData(e, b);
		e = setThreePartId(e, b);
		e = updateTotals(e, b);
		e = cancelIfAllReturned(e, b);
		e = fullyPayIfReturnedItemOrderHasBeenInvoiced(e, b);
		return e;
	}

	private BillableEntity nullifyBillingData(BillableEntity e) {
		e.setBilledBy(null);
		e.setBilledOn(null);
		e.setPrefix(null);
		e.setNumId(null);
		e.setSuffix(null);
		e.setFullyPaid(null);
		return e;
	}

	private BillableEntity setBilledData(BillableEntity e, Billable b) {
		e.setBilledBy(b.getBilledBy());
		e.setBilledOn(ZonedDateTime.now());
		return e;
	}

	private BillableEntity setThreePartId(BillableEntity e, Billable b) {
		e.setNumId(numId(b));
		e.setPrefix(b.getPrefix());
		e.setSuffix(b.getSuffix());
		return e;
	}

	private BillableEntity updateReceivingData(BillableEntity e, Billable b) {
		e = updateReceivingModification(e, b);
		e = createReceivingData(e, b);
		return e;
	}

	private BillableEntity updateReceivingModification(BillableEntity e, Billable b) {
		if (b.getReceivingModifiedBy() == null || e.getReceivingModifiedOn() != null)
			return e;
		e.setReceivingModifiedBy(b.getReceivingModifiedBy());
		e.setReceivingModifiedOn(ZonedDateTime.now());
		e.setDetails(receivingDetails(e, b));
		return e;
	}

	private List<BillableDetailEntity> soldDetails(BillableEntity e, Billable b) {
		Map<Long, BillableDetailEntity> m = new HashMap<>();
		for (BillableDetailEntity ed : e.getDetails())
			m.put(ed.getItem().getId(), ed);
		for (BillableDetail bd : b.getDetails()) {
			Long id = bd.getId();
			BillableDetailEntity ed = m.get(id);
			if (ed != null) {
				ed.setSoldQty(getSoldQty(bd));
				m.put(id, ed);
			}
		}
		return new ArrayList<>(m.values());
	}

	private BigDecimal getSoldQty(BillableDetail bd) {
		BigDecimal sold = bd.getSoldQtyInDecimals();
		return NumberUtils.isZero(sold) ? null : sold;
	}

	protected abstract Billable firstSpun();

	protected abstract Billable lastSpun();
}