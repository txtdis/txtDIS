package ph.txtdis.service;

import static java.math.BigDecimal.ZERO;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.reducing;
import static org.apache.log4j.Logger.getLogger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ph.txtdis.domain.EdmsCreditMemo;
import ph.txtdis.domain.EdmsCreditMemoDetail;
import ph.txtdis.domain.EdmsDelivery;
import ph.txtdis.domain.EdmsDeliveryDetail;
import ph.txtdis.domain.EdmsInvoice;
import ph.txtdis.domain.EdmsInvoiceDetail;
import ph.txtdis.domain.EdmsItem;
import ph.txtdis.domain.EdmsLoadOrder;
import ph.txtdis.domain.EdmsSalesOrder;
import ph.txtdis.domain.EdmsSalesOrderDetail;
import ph.txtdis.domain.EdmsSeller;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.repository.EdmsCreditMemoDetailRepository;
import ph.txtdis.repository.EdmsCreditMemoRepository;
import ph.txtdis.repository.EdmsDeliveryDetailRepository;
import ph.txtdis.repository.EdmsDeliveryRepository;
import ph.txtdis.repository.EdmsInvoiceDetailRepository;
import ph.txtdis.repository.EdmsInvoiceRepository;
import ph.txtdis.repository.EdmsSalesOrderDetailRepository;
import ph.txtdis.repository.EdmsSalesOrderRepository;
import ph.txtdis.type.QualityType;
import ph.txtdis.type.UomType;
import ph.txtdis.util.Code;
import ph.txtdis.util.DateTimeUtils;
import ph.txtdis.util.NumberUtils;
import ph.txtdis.util.TextUtils;

@Transactional
@Service("billableService")
public class BillableServiceImpl implements BillableService {

	private static Logger logger = getLogger(BillableServiceImpl.class);

	@Autowired
	private EdmsCreditMemoRepository edmsCreditMemoRepository;

	@Autowired
	private EdmsCreditMemoDetailRepository edmsCreditMemoDetailRepository;

	@Autowired
	private EdmsDeliveryRepository edmsDeliveryRepository;

	@Autowired
	private EdmsDeliveryDetailRepository edmsDeliveryDetailRepository;

	@Autowired
	private EdmsInvoiceRepository edmsInvoiceRepository;

	@Autowired
	private EdmsInvoiceDetailRepository edmsInvoiceDetailRepository;

	@Autowired
	private EdmsSalesOrderRepository edmsSalesOrderRepository;

	@Autowired
	private EdmsSalesOrderDetailRepository edmsSalesOrderDetailRepository;

	@Autowired
	private AutoNumberService autoNumberService;

	@Autowired
	private DriverService driverService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private HelperService helperService;

	@Autowired
	private ItemService itemService;

	@Autowired
	private RemittanceService remittanceService;

	@Autowired
	private SellerService sellerService;

	@Autowired
	private EdmsTruckService truckService;

	@Autowired
	private EdmsWarehouseService warehouseService;

	@Value("${invoice.start.id}")
	private String invoiceStartId;

	@Value("${vendor.id}")
	private String vendorId;

	@Value("${client.user}")
	private String userName;

	@Value("${prefix.so}")
	private String bookingPrefix;

	@Override
	public Billable save(Billable b) {
		if (b == null)
			return b;
		if (hasBeenBilled(b))
			saveBilling(b);
		else if (b.getReceivedOn() != null && b.getIsRma() != null)
			saveCreditMemo(b);
		return b;
	}

	private boolean hasBeenBilled(Billable b) {
		return getBilledOn(b) != null && b.getIsRma() == null;
	}

	private void saveBilling(Billable b) {
		if (b.getNumId() == null || b.getNumId() < 0)
			return;
		saveBillables(b);
	}

	private EdmsSalesOrder saveBillables(Billable b) {
		EdmsSalesOrder so = saveSalesOrder(b);
		List<EdmsSalesOrderDetail> sod = b.getDetails().stream().map(d -> newBookingDetail(b, d))
				.collect(Collectors.toList());
		saveBookingAutoNo(b);
		saveBillings(b, so, sod);
		return so;
	}

	private EdmsSalesOrder saveSalesOrder(Billable b) {
		EdmsSalesOrder e = new EdmsSalesOrder();
		EdmsSeller seller = sellerService.extractFrom(b);
		e.setReferenceNo(getBookingNo(b));
		e.setStatus(Code.CLOSED);
		e.setOrderDate(b.getCreatedOn().toLocalDate());
		e.setDeliveryDate(b.getOrderDate());
		e.setPaymentTermCode(getPaymentTermCode(b));
		e.setPaymentMode(getPaymentModeCode(b));
		e.setTotalValue(b.getTotalValue());
		e.setTotalDiscountValue(sumVolumeDiscounts(b));
		e.setRemarks(TextUtils.blankIfNull(b.getRemarks()));
		e.setNotes("");
		e.setCustomerCode(customerService.getCode(b));
		e.setSellerCode(seller.getCode());
		e.setTruckCode(seller.getTruckCode());
		e.setPlateNo(seller.getPlateNo());
		e.setDriverCode(seller.getDriver());
		e.setHelperCode(seller.getHelper());
		e.setWarehouseCode(getWarehouseCode(b));
		e.setCreatedBy(userName);
		e.setCreatedOn(DateTimeUtils.toTimestampWithSecondText(b.getCreatedOn()));
		e.setModifiedBy("");
		e.setModifiedOn("");
		return edmsSalesOrderRepository.save(e);
	}

	private String getBookingNo(Billable b) {
		return bookingPrefix + b.getOrderNo();
	}

	private String getPaymentModeCode(Billable b) {
		return Code.getPaymentModeCode(b.getDueDate(), b.getOrderDate());
	}

	private String getPaymentTermCode(Billable b) {
		logger.info("\n    OrderDate: " + b.getOrderDate());
		logger.info("\n    DueDate: " + b.getDueDate());
		logger.info("\n    Customer: " + b.getCustomerId() + " - " + b.getCustomerName());
		logger.info("\n    Details: " + b.getDetails());
		return Code.getPaymentTermCode(b.getDueDate(), b.getOrderDate());
	}

	private BigDecimal sumVolumeDiscounts(Billable b) {
		try {
			return b.getDetails().stream()//
					.map(d -> computeVolumeDiscountPerLineItem(d))//
					.reduce(BigDecimal.ZERO, BigDecimal::add);
		} catch (Exception e) {
			return BigDecimal.ZERO;
		}
	}

	private BigDecimal computeVolumeDiscountPerLineItem(BillableDetail bd) {
		BigDecimal discounted = bd.getPriceValue();
		return getOriginalPrice(bd).subtract(discounted).multiply(getQty(bd));
	}

	private BigDecimal getOriginalPrice(BillableDetail bd) {
		return bd.getUom() == UomType.CS ? itemService.getPricePerCase(bd) : itemService.getPricePerBottle(bd);
	}

	private EdmsSalesOrderDetail newBookingDetail(Billable b, BillableDetail bd) {
		EdmsItem i = itemService.getItem(bd);
		EdmsSalesOrderDetail d = new EdmsSalesOrderDetail();
		d.setReferenceNo(getBookingNo(b));
		d.setItemCode(i.getCode());
		d.setItemName(i.getName());
		d.setDeliveredQty(getQty(bd));
		d.setQty(d.getDeliveredQty());
		d.setUomCode(Code.getUomCode(bd));
		d.setCostValue(BigDecimal.ZERO);
		d.setPriceValue(bd.getPriceValue());
		d.setDiscountValue(BigDecimal.ZERO);
		d.setTotalDiscountValue(computeVolumeDiscountPerLineItem(bd));
		d.setTotalValue(bd.getFinalSubtotalValue());
		d.setTransactionCode(getSalesTransactionCode(bd));
		d.setTdi("");
		return edmsSalesOrderDetailRepository.save(d);
	}

	private BigDecimal getQty(BillableDetail bd) {
		return NumberUtils.divide( //
				bd.getFinalQty(), //
				itemService.getBottlesPerCase(bd));
	}

	private String getSalesTransactionCode(BillableDetail bd) {
		return bd.getPriceValue().compareTo(BigDecimal.ZERO) != 0 ? Code.SALES : Code.PROMO;
	}

	private void saveBookingAutoNo(Billable b) {
		saveBillableAutoNo(b, Code.BOOKING_PREFIX);
	}

	private void saveBillableAutoNo(Billable b, String prefix) {
		autoNumberService.saveAutoNo(prefix, getBillableAutoNo(b));
	}

	private String getBillableAutoNo(Billable b) {
		Long id = b.getNumId();
		id = Math.abs(id);
		return getAutoNo(id);
	}

	private String getAutoNo(Long id) {
		return Code.addZeroes(id.toString());
	}

	private void saveBillings(Billable b, EdmsSalesOrder so, List<EdmsSalesOrderDetail> sod) {
		EdmsDelivery dr = newDelivery(so, b.getDueDate());
		sod.forEach(d -> newDeliveryDetail(d));
		saveDeliveryAutoNo(b);
		newInvoice(dr);
		sod.forEach(d -> newInvoiceDetail(d));
		saveInvoiceAutoNo(b);
	}

	private EdmsDelivery newDelivery(EdmsSalesOrder so, LocalDate dueDate) {
		EdmsDelivery e = new EdmsDelivery();
		e.setReferenceNo(so.getReferenceNo().replace(Code.BOOKING_PREFIX, Code.DELIVERY_PREFIX));
		e.setBillingNo(so.getReferenceNo().replace(Code.BOOKING_PREFIX, Code.INVOICE_PREFIX));
		e.setBookingNo(so.getReferenceNo());
		e.setStatus(so.getStatus());
		e.setOrderDate(so.getOrderDate());
		e.setDeliveryDate(so.getDeliveryDate());
		e.setPaymentTermCode(so.getPaymentTermCode());
		e.setPaymentMode(so.getPaymentMode());
		e.setTotalValue(so.getTotalValue());
		e.setTotalDiscountValue(so.getTotalDiscountValue());
		e.setDueDate(dueDate);
		e.setRemarks(so.getRemarks());
		e.setNotes(so.getNotes());
		e.setCustomerCode(so.getCustomerCode());
		e.setSellerCode(so.getSellerCode());
		e.setTruckCode(so.getTruckCode());
		e.setPlateNo(so.getPlateNo());
		e.setDriverCode(so.getDriverCode());
		e.setHelperCode(so.getHelperCode());
		e.setWarehouseCode(so.getWarehouseCode());
		e.setCreatedBy("");
		e.setCreatedOn("");
		return edmsDeliveryRepository.save(e);
	}

	private void newDeliveryDetail(EdmsSalesOrderDetail sod) {
		EdmsDeliveryDetail d = new EdmsDeliveryDetail();
		d.setReferenceNo(sod.getReferenceNo().replace(Code.BOOKING_PREFIX, Code.DELIVERY_PREFIX));
		d.setSalesOrderDetailId(sod.getId());
		d.setItemCode(sod.getItemCode());
		d.setItemName(sod.getItemName());
		d.setQty(sod.getQty());
		d.setUomCode(sod.getUomCode());
		d.setCostValue(sod.getCostValue());
		d.setPriceValue(sod.getPriceValue());
		d.setDiscountValue(sod.getDiscountValue());
		d.setTotalDiscountValue(sod.getTotalDiscountValue());
		d.setTotalValue(sod.getTotalValue());
		d.setTransactionCode(sod.getTransactionCode());
		d.setTdi(sod.getTdi());
		edmsDeliveryDetailRepository.save(d);
	}

	private void saveDeliveryAutoNo(Billable b) {
		saveBillableAutoNo(b, Code.DELIVERY_PREFIX);
	}

	private EdmsInvoice newInvoice(EdmsDelivery dr) {
		EdmsInvoice e = new EdmsInvoice();
		e.setReferenceNo(dr.getReferenceNo().replace(Code.DELIVERY_PREFIX, Code.INVOICE_PREFIX));
		e.setDeliveryNo(dr.getReferenceNo());
		e.setBookingNo(dr.getReferenceNo().replace(Code.DELIVERY_PREFIX, Code.BOOKING_PREFIX));
		e.setStatus(dr.getStatus());
		e.setOrderDate(dr.getOrderDate());
		e.setDeliveryDate(dr.getDeliveryDate());
		e.setPaymentTermCode(dr.getPaymentTermCode());
		e.setPaymentMode(dr.getPaymentMode());
		e.setTotalValue(dr.getTotalValue());
		e.setTotalDiscountValue(dr.getTotalDiscountValue());
		e.setDueDate(dr.getDueDate());
		e.setRemarks(dr.getRemarks());
		e.setNotes(dr.getNotes());
		e.setCustomerCode(dr.getCustomerCode());
		e.setSellerCode(dr.getSellerCode());
		e.setTruckCode(dr.getTruckCode());
		e.setPlateNo(dr.getPlateNo());
		e.setDriverCode(dr.getDriverCode());
		e.setHelperCode(dr.getHelperCode());
		e.setWarehouseCode(dr.getWarehouseCode());
		e.setCreatedBy(dr.getCreatedBy());
		e.setCreatedOn(dr.getCreatedOn());
		e.setPostedRemittanceVariance(Code.TRUE);
		return edmsInvoiceRepository.save(e);
	}

	private void newInvoiceDetail(EdmsSalesOrderDetail sod) {
		EdmsInvoiceDetail d = new EdmsInvoiceDetail();
		d.setReferenceNo(sod.getReferenceNo().replace(Code.BOOKING_PREFIX, Code.INVOICE_PREFIX));
		d.setSalesOrderDetailId(sod.getId());
		d.setItemCode(sod.getItemCode());
		d.setItemName(sod.getItemName());
		d.setQty(sod.getQty());
		d.setUomCode(sod.getUomCode());
		d.setCostValue(sod.getCostValue());
		d.setPriceValue(sod.getPriceValue());
		d.setDiscountValue(sod.getDiscountValue());
		d.setTotalDiscountValue(sod.getTotalDiscountValue());
		d.setTotalValue(sod.getTotalValue());
		d.setTransactionCode(sod.getTransactionCode());
		d.setTdi(sod.getTdi());
		edmsInvoiceDetailRepository.save(d);
	}

	private void saveInvoiceAutoNo(Billable b) {
		saveBillableAutoNo(b, Code.INVOICE_PREFIX);
	}

	private void saveCreditMemo(Billable b) {
		EdmsCreditMemo e = edmsCreditMemoRepository.findByReferenceNo(getCreditMemoNo(b));
		if (e == null)
			saveCreatedCreditMemo(b);
		else if (e.getBilledDate() == null)
			saveUpdatedCreditMemo(e, b);
	}

	private String getCreditMemoNo(Billable b) {
		String id = getBilledOn(b) == null ? b.getReceivingId().toString() : b.getOrderNo();
		return Code.CREDIT_MEMO_PREFIX + "-" + id;
	}

	private void saveCreatedCreditMemo(Billable b) {
		saveNewCreditMemo(b);
		b.getDetails().forEach(d -> saveNewCreditMemoDetail(b, d));
		saveCreditMemoAutoNo();
	}

	private void saveNewCreditMemo(Billable b) {
		EdmsSeller seller = sellerService.extractFrom(b);
		EdmsCreditMemo e = new EdmsCreditMemo();
		e.setStatus(Code.CLOSED);
		e.setOrderDate(b.getCreatedOn().toLocalDate());
		e.setPaymentMode("");
		e.setTotalValue(b.getTotalValue());
		e.setTotalDiscountValue(sumVolumeDiscounts(b));
		e.setRemarks(TextUtils.blankIfNull(b.getRemarks()));
		e.setNotes("");
		e.setCustomerCode(customerService.getCode(b));
		e.setSellerCode(seller.getCode());
		e.setTruckCode(seller.getTruckCode());
		e.setPlateNo(seller.getPlateNo());
		e.setDriverCode(driverService.getCode(seller));
		e.setHelperCode(helperService.getCode(seller));
		e.setWarehouseCode(getWarehouseCode(b));
		e.setCreatedBy(userName);
		e.setCreatedOn(DateTimeUtils.toTimestampWithSecondText(b.getCreatedOn()));
		saveUpdatedCreditMemo(e, b);
	}

	private EdmsCreditMemo saveUpdatedCreditMemo(EdmsCreditMemo cm, Billable b) {
		cm.setReferenceNo(getCreditMemoNo(b));
		cm.setBilledDate(getBilledOn(b));
		cm.setModifiedBy(b.getBilledBy());
		cm.setModifiedOn(getModifiedOn(b));
		return edmsCreditMemoRepository.save(cm);
	}

	private LocalDate getBilledOn(Billable b) {
		return b.getBilledOn() == null ? null : b.getBilledOn().toLocalDate();
	}

	private String getWarehouseCode(Billable b) {
		return b.getIsRma() == null || b.getIsRma() ? warehouseService.getMainCode() : warehouseService.getBoCode();
	}

	private String getModifiedOn(Billable b) {
		if (getBilledOn(b) == null)
			return null;
		return DateTimeUtils.toTimestampWithSecondText(b.getBilledOn());
	}

	private void saveNewCreditMemoDetail(Billable b, BillableDetail bd) {
		EdmsItem i = itemService.getItem(bd);
		EdmsCreditMemoDetail d = new EdmsCreditMemoDetail();
		d.setItemCode(i.getCode());
		d.setItemName(i.getName());
		d.setDeliveredQty(BigDecimal.ZERO);
		d.setQty(bd.getFinalQty());
		d.setUomCode(Code.getUomCode(bd));
		d.setCostValue(BigDecimal.ZERO);
		d.setPriceValue(bd.getPriceValue());
		d.setDiscountValue(BigDecimal.ZERO);
		d.setTotalValue(bd.getFinalSubtotalValue());
		d.setTransactionCode(getReturnsTransactionCode(b));
		edmsCreditMemoDetailRepository.save(d);
	}

	private String getReturnsTransactionCode(Billable b) {
		return b.getIsRma() ? Code.GOOD_RETURNS : Code.BAD_ORDER;
	}

	private void saveCreditMemoAutoNo() {
		EdmsCreditMemo e = edmsCreditMemoRepository.findFirstByOrderByIdDesc();
		autoNumberService.saveAutoNo(Code.CREDIT_MEMO_PREFIX, getAutoNo(e.getId()));
	}

	@Override
	public EdmsInvoice getByOrderNo(String orderNo) {
		String refNo = Code.INVOICE_PREFIX + "-" + orderNo;
		return edmsInvoiceRepository.findByReferenceNoAndStatus(refNo, Code.CLOSED);
	}

	@Override
	public List<Billable> list() {
		Iterable<EdmsInvoice> i = edmsInvoiceRepository.findAll();
		return StreamSupport.stream(i.spliterator(), false).map(e -> toBillable(e)).filter(p -> p != null).distinct()
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private Billable toBillable(EdmsInvoice i) {
		if (!i.getStatus().equals(Code.CLOSED))
			return null;
		Billable b = new Billable();
		b = setOrderNo(b, i.getReferenceNo());
		b.setOrderDate(i.getDeliveryDate());
		b.setDueDate(i.getDueDate());
		b.setBookingId(null);
		b.setCustomerId(null);
		b.setCustomerName(customerService.getName(i));
		b.setPickListId(truckService.getId(i));
		b.setTotalValue(i.getTotalValue());
		b.setUnpaidValue(remittanceService.getUnpaidAmount(i));
		b.setFullyPaid(remittanceService.isFullyPaid(b));
		b.setBilledBy(Code.EDMS);
		b.setBilledOn(getBilledOn(i));
		b.setDetails(getDetails(b, i.getReferenceNo()));
		logger.info("\n    Billing: " + b);
		return b;
	}

	private Billable setOrderNo(Billable b, String refNo) {
		b.setNumId(getNumIdPrefixedIfOnNewEdmsVersionAndDR(refNo));
		b.setSuffix(getSuffix(refNo));
		return b;
	}

	private long getNumIdPrefixedIfOnNewEdmsVersionAndDR(String refNo) {
		long id = getNumId(refNo);
		if (id < 0 && StringUtils.contains(refNo, "MAG"))
			id = id - 100_000;
		return id;
	}

	private Long getNumId(String refNo) {
		String code = StringUtils.substringAfterLast(refNo, "-");
		long id = Long.valueOf(Code.numbersOnly(code));
		return id < Long.valueOf(invoiceStartId) ? toDeliveryId(id) : id;
	}

	private long toDeliveryId(long id) {
		return id < 0 ? id : -id;
	}

	private String getSuffix(String referenceNo) {
		String s = StringUtils.right(referenceNo, 1);
		return StringUtils.isAlpha(s) ? s.toUpperCase() : null;
	}

	private ZonedDateTime toTimeStamp(String ts) {
		return DateTimeUtils.toZonedDateTimeFromTimestampWithSeconds(ts);
	}

	private ZonedDateTime getBilledOn(EdmsInvoice i) {
		String billedOn = i.getCreatedOn();
		if (billedOn != null && !billedOn.isEmpty())
			return toTimeStamp(billedOn);
		return DateTimeUtils.toZonedDateTime(i.getOrderDate());
	}

	@Override
	public Long getBookingId(EdmsInvoice i) {
		String no = i.getBookingNo();
		return StringUtils.isNumeric(no) ? Long.valueOf(no) : extractFromSubstring(i, no);
	}

	private Long extractFromSubstring(EdmsInvoice i, String no) {
		no = StringUtils.substringAfterLast(no, "-");
		long id = Long.valueOf(Code.numbersOnly(no));
		id = !isOrderNoSuffixed(i) ? id : id + 100_000;
		return !isNewEdmsOrderNo(i) ? id : id + 100_000;
	}

	private boolean isOrderNoSuffixed(EdmsInvoice i) {
		String orderNo = i.getReferenceNo();
		orderNo = StringUtils.substringAfterLast(orderNo, "-");
		return !StringUtils.isNumeric(orderNo);
	}

	private boolean isNewEdmsOrderNo(EdmsInvoice i) {
		String orderNo = i.getReferenceNo();
		return StringUtils.contains(orderNo, "MAG");
	}

	private List<BillableDetail> getDetails(Billable b, String code) {
		List<EdmsInvoiceDetail> l = edmsInvoiceDetailRepository.findByReferenceNoAndSalesOrderDetailIdNotNull(code);
		return l == null ? null
				: l.stream()//
						.map(i -> toDetailWithVendorNoAsConcatOfItemCodeAndQtyPerCaseAndPrice(i)).distinct()//
						.collect(groupingBy(BillableDetail::getItemVendorNo, //
								mapping(BillableDetail::getInitialQty, //
										reducing(ZERO, BigDecimal::add)))) //
						.entrySet().stream().map(e -> toBillableDetail(e))//
						.collect(Collectors.toList());
	}

	private BillableDetail toDetailWithVendorNoAsConcatOfItemCodeAndQtyPerCaseAndPrice(EdmsInvoiceDetail id) {
		BillableDetail d = new BillableDetail();
		d.setQtyPerCase(getBottlesPerCase(id).intValue());
		d.setItemVendorNo(id.getItemCode() + "," + d.getQtyPerCase() + "," + getPrice(id));
		d.setInitialQty(getQtyInBottles(id, d));
		logger.info("\n    BillableDetailPerBottle: " + id.getReferenceNo() + " - " + d.getItemVendorNo() + ","
				+ d.getInitialQty());
		return d;
	}

	private BillableDetail toBillableDetail(Entry<String, BigDecimal> e) {
		String[] data = e.getKey().split(",");
		BillableDetail d = new BillableDetail();
		d.setItemVendorNo(data[0]);
		d.setQtyPerCase(Integer.valueOf(data[1]));
		d.setPriceValue(new BigDecimal(data[2]));
		d.setQuality(QualityType.GOOD);
		d.setUom(UomType.CS);
		d.setInitialQty(e.getValue());
		logger.info("\n    Billing Detail: " + d.getItemVendorNo() + " - " + d.getInitialQty().intValue() + d.getUom());
		return d;
	}

	private BigDecimal getQtyInBottles(EdmsInvoiceDetail id, BillableDetail bd) {
		BigDecimal qty = id.getQty();
		if (isPerCase(id))
			qty = qty.multiply(new BigDecimal(bd.getQtyPerCase()));
		return qty;
	}

	private BigDecimal getPrice(EdmsInvoiceDetail id) {
		return isPromo(id) ? BigDecimal.ZERO : getDiscountedPrice(id);
	}

	private boolean isPromo(EdmsInvoiceDetail id) {
		return id.getTransactionCode().equals(Code.PROMO);
	}

	private BigDecimal getDiscountedPrice(EdmsInvoiceDetail id) {
		return getInitialPrice(id).subtract(getDiscount(id)).setScale(2, RoundingMode.HALF_EVEN);
	}

	private BigDecimal getInitialPrice(EdmsInvoiceDetail id) {
		return isPerCase(id) ? id.getPriceValue() : itemService.getPricePerCase(id);
	}

	private boolean isPerCase(EdmsInvoiceDetail id) {
		return id.getUomCode().equals(Code.CS);
	}

	private BigDecimal getDiscount(EdmsInvoiceDetail id) {
		return isPerCase(id) ? dividePerCase(id) : dividePerBottleThenConvertPerCase(id);
	}

	private BigDecimal dividePerCase(EdmsInvoiceDetail id) {
		return divideByQty(id);
	}

	private BigDecimal divideByQty(EdmsInvoiceDetail id) {
		return id.getTotalDiscountValue().divide(id.getQty(), 4, RoundingMode.HALF_EVEN);
	}

	private BigDecimal dividePerBottleThenConvertPerCase(EdmsInvoiceDetail id) {
		return divideByQty(id).multiply(getBottlesPerCase(id));
	}

	private BigDecimal getBottlesPerCase(EdmsInvoiceDetail id) {
		return itemService.getBottlesPerCase(id);
	}

	@Override
	public String getOrderNoFromBillingNo(String no) {
		return NumberUtils.formatId(getNumIdPrefixedIfOnNewEdmsVersionAndDR(no)) + TextUtils.blankIfNull(getSuffix(no));
	}

	@Override
	public List<EdmsInvoice> getBillables(EdmsLoadOrder logp) {
		return edmsInvoiceRepository.findByOrderDateAndTruckCodeAndStatus(logp.getOrderDate(), logp.getTruckCode(),
				Code.CLOSED);
	}

	@Override
	public Billable findById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}
}
