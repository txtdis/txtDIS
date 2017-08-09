package ph.txtdis.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ph.txtdis.domain.*;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.repository.*;
import ph.txtdis.type.QualityType;
import ph.txtdis.type.UomType;
import ph.txtdis.util.Code;
import ph.txtdis.util.DateTimeUtils;
import ph.txtdis.util.NumberUtils;
import ph.txtdis.util.TextUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.math.BigDecimal.ZERO;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.stream.Collectors.*;
import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.apache.commons.lang3.text.WordUtils.capitalizeFully;
import static ph.txtdis.util.Code.*;
import static ph.txtdis.util.DateTimeUtils.toTimestampWithSecondText;
import static ph.txtdis.util.NumberUtils.*;
import static ph.txtdis.util.TextUtils.blankIfNull;

@Transactional
@Service("billableService")
public class EdmsBillableServiceImpl //
	implements EdmsBillableService {

	@Autowired
	private EdmsCreditMemoRepository creditMemoRepository;

	@Autowired
	private EdmsCreditMemoDetailRepository creditMemoDetailRepository;

	@Autowired
	private EdmsDeliveryRepository deliveryRepository;

	@Autowired
	private EdmsDeliveryDetailRepository deliveryDetailRepository;

	@Autowired
	private EdmsInvoiceRepository invoiceRepository;

	@Autowired
	private EdmsInvoiceDetailRepository invoiceDetailRepository;

	@Autowired
	private EdmsPurchaseReceiptRepository purchaseReceiptRepository;

	@Autowired
	private EdmsPurchaseReceiptDetailRepository purchaseReceiptDetailRepository;

	@Autowired
	private EdmsSalesOrderRepository salesOrderRepository;

	@Autowired
	private EdmsSalesOrderDetailRepository salesOrderDetailRepository;

	@Autowired
	private AutoNumberService autoNumberService;

	@Autowired
	private EndOfDayService endOfDayService;

	@Autowired
	private DriverService driverService;

	@Autowired
	private EdmsCustomerService customerService;

	@Autowired
	private HelperService helperService;

	@Autowired
	private EdmsItemService itemService;

	@Autowired
	private EdmsRemittanceService remittanceService;

	@Autowired
	private EdmsSellerService sellerService;

	@Autowired
	private EdmsTruckService truckService;

	@Autowired
	private EdmsWarehouseService warehouseService;

	@Value("${invoice.start.id}")
	private String invoiceStartId;

	@Value("${vendor.id}")
	private Long vendorId;

	@Value("${client.user}")
	private String username;

	@Value("${prefix.purchase}")
	private String purchasePrefix;

	@Value("${transaction.code}")
	private String transactionCode;

	@Override
	public Billable save(Billable b) {
		if (b != null) {
			if (b.getCustomerId() == vendorId)
				savePurchaseReceipts(b);
			else if (isAnInvoice(b))
				saveBillables(b);
		}
		return b;
	}

	private void savePurchaseReceipts(Billable b) {
		savePurchaseReceipt(b);
		b.getDetails().forEach(d -> savePurchaseReceiptDetail(b, d));
		savePurchaseReceiptAutoNo(b);
	}

	private void savePurchaseReceipt(Billable b) {
		EdmsPurchaseReceipt e = new EdmsPurchaseReceipt();
		e.setReferenceNo(purchaseReceiptNo(b));
		e.setStatus(CLOSED);
		e.setOrderDate(b.getOrderDate());
		e.setWarehouseCode(warehouseCode(b));
		e.setCreatedBy(username);
		e.setCreatedOn(toTimestampWithSecondText(b.getCreatedOn()));
		purchaseReceiptRepository.save(e);
	}

	public String purchaseReceiptNo(Billable b) {
		return purchasePrefix + getAutoNo(b.getId());
	}

	private String getAutoNo(Long id) {
		return Code.addZeroes(id.toString());
	}

	private void savePurchaseReceiptDetail(Billable b, BillableDetail bd) {
		EdmsItem i = itemService.getItem(bd);
		EdmsPurchaseReceiptDetail d = new EdmsPurchaseReceiptDetail();
		d.setReferenceNo(purchaseReceiptNo(b));
		d.setItemCode(i.getCode());
		d.setItemName(i.getName());
		d.setQty(bd.getReturnedQtyInCases());
		d.setUomCode(Code.CS);
		purchaseReceiptDetailRepository.save(d);
	}

	private void savePurchaseReceiptAutoNo(Billable b) {
		saveAutoNo(b, PURCHASE_RECEIPT_PREFIX);
	}

	private void saveAutoNo(Billable b, String prefix) {
		autoNumberService.saveAutoNo(prefix, getAutoNo(b.getId()));
	}

	private boolean isAnInvoice(Billable b) {
		return b.getBilledOn() != null && b.getNumId() > 0;
	}

	private void saveBillables(Billable b) {
		if (isAnRMA(b))
			saveCreditMemo(b);
		else
			saveSalesOrderAndDeliveryReceiptAndSalesInvoice(b);
	}

	private boolean isAnRMA(Billable b) {
		return b.getIsRma() != null;
	}

	private void saveSalesOrderAndDeliveryReceiptAndSalesInvoice(Billable b) {
		endOfDayService.updateEndOfDay();
		EdmsSalesOrder e = salesOrderRepository.findByReferenceNo(bookingNo(b));
		if (e == null)
			createSalesOrderAndDeliveryReceiptAndSalesInvoice(b);
		else
			updateSalesOrderAndDeliveryReceiptAndInvoice(e, b);
	}

	private void createSalesOrderAndDeliveryReceiptAndSalesInvoice(Billable b) {
		List<EdmsSalesOrderDetail> details = b.getDetails().stream() //
			.flatMap(d -> createBookingDetails(b, d).stream()) //
			.filter(d -> isPositive(d.getQty())) //
			.collect(toList());
		EdmsSalesOrder e = createSalesOrder(b, details);
		saveAutoNo(b, BOOKING_PREFIX);
		createDeliveryReceiptAndSalesInvoice(b, e, details);
	}

	private List<EdmsSalesOrderDetail> createBookingDetails(Billable b, BillableDetail bd) {
		EdmsItem i = itemService.getItem(bd);
		return Arrays.asList(newBookingDetailInCases(b, bd, i), newBookingDetailInBottles(b, bd, i));
	}

	private EdmsSalesOrderDetail newBookingDetailInCases(Billable b, BillableDetail bd, EdmsItem i) {
		EdmsSalesOrderDetail d = newBookingDetail(b, bd, i);
		d.setUomCode(Code.CS);
		d.setPriceValue(bd.getPriceValue());
		d.setDeliveredQty(NumberUtils.toWholeNo(bd.getFinalQtyInCases()));
		return setQtyAndTotalsAndTransactionCodeSavingIfHasQty(d);
	}

	public EdmsSalesOrderDetail setQtyAndTotalsAndTransactionCodeSavingIfHasQty(EdmsSalesOrderDetail d) {
		d.setQty(d.getDeliveredQty());
		d.setTotalDiscountValue(computeVolumeDiscountPerLineItem(d));
		d.setTotalValue(computeSubtotalPerLineItem(d));
		d.setTransactionCode(getSalesTransactionCode(d));
		return isZero(d.getQty()) ? d : salesOrderDetailRepository.save(d);
	}

	public BigDecimal computeSubtotalPerLineItem(EdmsSalesOrderDetail d) {
		return d.getPriceValue().multiply(d.getQty());
	}

	private String getSalesTransactionCode(EdmsSalesOrderDetail d) {
		return isZero(d.getPriceValue()) ? PROMO : SALES;
	}

	private EdmsSalesOrderDetail newBookingDetailInBottles(Billable b, BillableDetail bd, EdmsItem i) {
		BigDecimal qtyPerCase = toDecimal(bd.getQtyPerCase());
		EdmsSalesOrderDetail d = newBookingDetail(b, bd, i);
		d.setUomCode(Code.BTL);
		d.setPriceValue(divide(bd.getPriceValue(), qtyPerCase));
		d.setDeliveredQty(remainder(bd.getFinalQty(), qtyPerCase));
		return setQtyAndTotalsAndTransactionCodeSavingIfHasQty(d);
	}

	public EdmsSalesOrderDetail newBookingDetail(Billable b, BillableDetail bd, EdmsItem i) {
		EdmsSalesOrderDetail d = new EdmsSalesOrderDetail();
		d.setReferenceNo(bookingNo(b));
		d.setItemCode(i.getCode());
		d.setItemName(i.getName());
		return d;
	}

	private BigDecimal computeVolumeDiscountPerLineItem(EdmsSalesOrderDetail d) {
		return getOriginalPrice(d).subtract(d.getPriceValue()).multiply(d.getQty());
	}

	private BigDecimal getOriginalPrice(EdmsSalesOrderDetail d) {
		return d.getUomCode() == Code.CS ? itemService.getPricePerCase(d) : itemService.getPricePerBottle(d);
	}

	private EdmsSalesOrder createSalesOrder(Billable b, List<EdmsSalesOrderDetail> details) {
		EdmsSalesOrder e = new EdmsSalesOrder();
		e.setReferenceNo(bookingNo(b));
		e.setOrderDate(b.getOrderDate());
		e.setDeliveryDate(b.getOrderDate());
		e.setPaymentTermCode(paymentTermCode(b));
		e.setPaymentMode(paymentModeCode(e));
		e.setTotalValue(b.getTotalValue());
		e.setTotalDiscountValue(sumVolumeDiscounts(details));
		e.setCustomerCode(customerService.getCode(b));
		e.setSellerCode(sellerCode(b));
		e.setTruckCode(truckCode(b));
		e.setPlateNo(b.getTruck());
		e.setDriverCode(driver(b));
		e.setHelperCode(helper(b));
		e.setWarehouseCode(warehouseCode(b));
		e.setCreatedBy(username);
		e.setCreatedOn(toTimestampWithSecondText(b.getCreatedOn()));
		e.setStatus(status(b));
		return salesOrderRepository.save(e);
	}

	private String bookingNo(Billable b) {
		return BOOKING_PREFIX + transactionCode + b.getId();
	}

	private String status(Billable b) {
		Boolean isValid = b.getIsValid();
		return isValid == null || isValid == true ? CLOSED : VOID;
	}

	private String paymentTermCode(Billable b) {
		Long days = 0L;
		if (b.getOrderDate() != null && b.getDueDate() != null)
			days = b.getOrderDate().until(b.getDueDate(), DAYS);
		return days == 0 ? COD : leftPad(days.toString(), 2, "0") + " Days";
	}

	private String paymentModeCode(EdmsSalesOrder e) {
		return e.getPaymentTermCode().equalsIgnoreCase(COD) ? CASH : CREDIT;
	}

	private BigDecimal sumVolumeDiscounts(List<EdmsSalesOrderDetail> details) {
		try {
			return details.stream()//
				.map(d -> computeVolumeDiscountPerLineItem(d))//
				.reduce(ZERO, BigDecimal::add);
		} catch (Exception e) {
			return ZERO;
		}
	}

	private String sellerCode(Billable b) {
		return capitalizeFully(blankIfNull(b.getRoute()), ' ', '-');
	}

	private String truckCode(Billable b) {
		EdmsTruck t = truckService.findEntityByPlateNo(b.getTruck());
		return t == null ? "" : t.getCode();
	}

	private String driver(Billable b) {
		return capitalizeFully(blankIfNull(b.getDriver()));
	}

	private String helper(Billable b) {
		return capitalizeFully(blankIfNull(b.getHelper()));
	}

	private String warehouseCode(Billable b) {
		return b.getIsRma() == null || b.getIsRma() //
			? warehouseService.getMainCode() //
			: warehouseService.getBoCode();
	}

	private void createDeliveryReceiptAndSalesInvoice(Billable b, EdmsSalesOrder so, List<EdmsSalesOrderDetail> sod) {
		EdmsDelivery dr = newDelivery(so, b);
		sod.forEach(d -> newDeliveryDetail(d));
		saveDeliveryAutoNo(b);
		newInvoice(dr);
		sod.forEach(d -> newInvoiceDetail(b, d));
		saveInvoiceAutoNo(b);
	}

	private String invoiceNo(Billable b) {
		return INVOICE_PREFIX + transactionCode + b.getOrderNo();
	}

	private EdmsDelivery newDelivery(EdmsSalesOrder so, Billable b) {
		EdmsDelivery e = new EdmsDelivery();
		e.setReferenceNo(so.getReferenceNo().replace(BOOKING_PREFIX, DELIVERY_PREFIX));
		e.setBillingNo(invoiceNo(b));
		e.setBookingNo(so.getReferenceNo());
		e.setStatus(so.getStatus());
		e.setOrderDate(so.getOrderDate());
		e.setDeliveryDate(so.getDeliveryDate());
		e.setPaymentTermCode(so.getPaymentTermCode());
		e.setPaymentMode(so.getPaymentMode());
		e.setTotalValue(so.getTotalValue());
		e.setTotalDiscountValue(so.getTotalDiscountValue());
		e.setDueDate(b.getDueDate());
		e.setRemarks(so.getRemarks());
		e.setNotes(so.getNotes());
		e.setCustomerCode(so.getCustomerCode());
		e.setSellerCode(so.getSellerCode());
		e.setTruckCode(so.getTruckCode());
		e.setPlateNo(so.getPlateNo());
		e.setDriverCode(so.getDriverCode());
		e.setHelperCode(so.getHelperCode());
		e.setWarehouseCode(so.getWarehouseCode());
		e.setCreatedBy(so.getCreatedBy());
		e.setCreatedOn(so.getCreatedOn());
		return deliveryRepository.save(e);
	}

	private void newDeliveryDetail(EdmsSalesOrderDetail sod) {
		EdmsDeliveryDetail d = new EdmsDeliveryDetail();
		d.setReferenceNo(deliveryNo(sod.getReferenceNo()));
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
		deliveryDetailRepository.save(d);
	}

	private String deliveryNo(String reference) {
		return reference.replace(BOOKING_PREFIX, DELIVERY_PREFIX);
	}

	private void saveDeliveryAutoNo(Billable b) {
		saveAutoNo(b, DELIVERY_PREFIX);
	}

	private EdmsInvoice newInvoice(EdmsDelivery dr) {
		EdmsInvoice e = new EdmsInvoice();
		e.setReferenceNo(dr.getBillingNo());
		e.setDeliveryNo(dr.getReferenceNo());
		e.setBookingNo(dr.getBookingNo());
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
		e.setPostedRemittanceVariance(TRUE);
		return invoiceRepository.save(e);
	}

	private void newInvoiceDetail(Billable b, EdmsSalesOrderDetail sod) {
		EdmsInvoiceDetail d = new EdmsInvoiceDetail();
		d.setReferenceNo(invoiceNo(b));
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
		invoiceDetailRepository.save(d);
	}

	private void saveInvoiceAutoNo(Billable b) {
		saveAutoNo(b, INVOICE_PREFIX);
	}

	private void updateSalesOrderAndDeliveryReceiptAndInvoice(EdmsSalesOrder e, Billable b) {
		if (b.getIsValid() != null && b.getIsValid() == false && e.getStatus().equals(CLOSED))
			invalidateSalesOrderAndDeliveryReceiptAndInvoice(e);
		updateInvoiceNo(e, b);
	}

	private void invalidateSalesOrderAndDeliveryReceiptAndInvoice(EdmsSalesOrder so) {
		String reference = so.getReferenceNo();
		invalidateSalesOrder(so);
		invalidateSalesOrderDetail(reference);
		invalidateDeliveryReceipt(reference);
		invalidateDeliveryReceiptDetail(reference);
		invalidateInvoice(reference);
		invalidateInvoiceDetail(reference);
	}

	private void invalidateSalesOrder(EdmsSalesOrder e) {
		e.setReferenceNo(e.getReferenceNo() + "X");
		e.setStatus(VOID);
		salesOrderRepository.save(e);
	}

	private void invalidateSalesOrderDetail(String reference) {
		List<EdmsSalesOrderDetail> l = salesOrderDetailRepository.findAllByReferenceNo(reference);
		if (l != null)
			l.stream().forEach(d -> d.setReferenceNo(d.getReferenceNo() + "X"));
		salesOrderDetailRepository.save(l);
	}

	private void invalidateDeliveryReceipt(String reference) {
		EdmsDelivery e = deliveryByBookingNo(reference);
		e.setReferenceNo(e.getReferenceNo() + "X");
		e.setBillingNo(e.getBillingNo() + "X");
		e.setBookingNo(e.getBookingNo() + "X");
		e.setStatus(VOID);
		deliveryRepository.save(e);
	}

	private EdmsDelivery deliveryByBookingNo(String reference) {
		return deliveryRepository.findByBookingNoAndStatus(reference, CLOSED);
	}

	private void invalidateDeliveryReceiptDetail(String reference) {
		List<EdmsDeliveryDetail> l = deliveryDetailRepository.findAllByReferenceNo(deliveryNo(reference));
		if (l != null)
			l.stream().forEach(d -> d.setReferenceNo(d.getReferenceNo() + "X"));
		deliveryDetailRepository.save(l);
	}

	private void invalidateInvoice(String reference) {
		EdmsInvoice e = invoiceByBookingNo(reference);
		e.setReferenceNo(e.getReferenceNo() + "X");
		e.setDeliveryNo(e.getDeliveryNo() + "X");
		e.setBookingNo(e.getBookingNo() + "X");
		e.setStatus(VOID);
		invoiceRepository.save(e);
	}

	private EdmsInvoice invoiceByBookingNo(String reference) {
		return invoiceRepository.findByBookingNoAndStatus(reference, Code.CLOSED);
	}

	private void invalidateInvoiceDetail(String reference) {
		List<EdmsInvoiceDetail> l = invoiceDetailRepository.findAllByReferenceNo(deliveryNo(reference));
		if (l != null)
			l.stream().forEach(d -> d.setReferenceNo(d.getReferenceNo() + "X"));
		invoiceDetailRepository.save(l);
	}

	private void updateInvoiceNo(EdmsSalesOrder so, Billable b) {
		updateDelivery(so, b);
		updateInvoice(so, b);
	}

	private void updateDelivery(EdmsSalesOrder so, Billable b) {
		EdmsDelivery d = deliveryByBookingNo(so.getReferenceNo());
		if (d == null)
			return;
		d.setBillingNo(invoiceNo(b));
		deliveryRepository.save(d);
	}

	private void updateInvoice(EdmsSalesOrder so, Billable b) {
		EdmsInvoice i = invoiceByBookingNo(so.getReferenceNo());
		if (i == null)
			return;
		i.setReferenceNo(invoiceNo(b));
		invoiceRepository.save(i);
	}

	private void saveCreditMemo(Billable b) {
		EdmsCreditMemo e = creditMemoRepository.findByReferenceNo(getCreditMemoNo(b));
		if (e == null)
			saveCreatedCreditMemo(b);
		else if (e.getBilledDate() == null)
			saveUpdatedCreditMemo(e, b);
	}

	private String getCreditMemoNo(Billable b) {
		String id = getBilledOn(b) == null ? b.getReceivingId().toString() : b.getOrderNo();
		return CREDIT_MEMO_PREFIX + "-" + id;
	}

	private void saveCreatedCreditMemo(Billable b) {
		saveNewCreditMemo(b);
		b.getDetails().forEach(d -> saveNewCreditMemoDetail(b, d));
		saveCreditMemoAutoNo(b);
	}

	private void saveNewCreditMemo(Billable b) {
		EdmsSeller seller = sellerService.extractFrom(b);
		EdmsCreditMemo e = new EdmsCreditMemo();
		e.setStatus(CLOSED);
		e.setOrderDate(b.getCreatedOn().toLocalDate());
		e.setPaymentMode("");
		e.setTotalValue(b.getTotalValue());
		e.setTotalDiscountValue(ZERO);
		e.setRemarks("");
		e.setNotes("");
		e.setCustomerCode(customerService.getCode(b));
		e.setSellerCode(seller.getCode());
		e.setTruckCode(seller.getTruckCode());
		e.setPlateNo(seller.getPlateNo());
		e.setDriverCode(driverService.getCode(seller));
		e.setHelperCode(helperService.getCode(seller));
		e.setWarehouseCode(warehouseCode(b));
		e.setCreatedBy(username);
		e.setCreatedOn(toTimestampWithSecondText(b.getCreatedOn()));
		saveUpdatedCreditMemo(e, b);
	}

	private EdmsCreditMemo saveUpdatedCreditMemo(EdmsCreditMemo cm, Billable b) {
		cm.setReferenceNo(getCreditMemoNo(b));
		cm.setBilledDate(getBilledOn(b));
		cm.setModifiedBy(b.getBilledBy());
		cm.setModifiedOn(getModifiedOn(b));
		return creditMemoRepository.save(cm);
	}

	private LocalDate getBilledOn(Billable b) {
		return b.getBilledOn() == null ? null : b.getBilledOn().toLocalDate();
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
		d.setDeliveredQty(ZERO);
		d.setQty(bd.getFinalQty());
		d.setUomCode(Code.getUomCode(bd));
		d.setCostValue(ZERO);
		d.setPriceValue(bd.getPriceValue());
		d.setDiscountValue(ZERO);
		d.setTotalValue(bd.getFinalSubtotalValue());
		d.setTransactionCode(getReturnsTransactionCode(b));
		creditMemoDetailRepository.save(d);
	}

	private String getReturnsTransactionCode(Billable b) {
		return b.getIsRma() ? GOOD_RETURNS : BAD_ORDER;
	}

	private void saveCreditMemoAutoNo(Billable b) {
		saveAutoNo(b, CREDIT_MEMO_PREFIX);
	}

	@Override
	public EdmsInvoice getByOrderNo(String orderNo) {
		String refNo = INVOICE_PREFIX + "-" + orderNo;
		return invoiceRepository.findByReferenceNoAndStatus(refNo, CLOSED);
	}

	@Override
	public List<Billable> list() {
		Iterable<EdmsInvoice> i = invoiceRepository.findAll();
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
		return b;
	}

	private Billable setOrderNo(Billable b, String refNo) {
		b.setNumId(getNumIdPrefixedIfOnNewEdmsVersionAndDR(refNo));
		b.setSuffix(getSuffix(refNo));
		return b;
	}

	private ZonedDateTime getBilledOn(EdmsInvoice i) {
		String billedOn = i.getCreatedOn();
		if (billedOn != null && !billedOn.isEmpty())
			return toTimeStamp(billedOn);
		return DateTimeUtils.toZonedDateTime(i.getOrderDate());
	}

	private List<BillableDetail> getDetails(Billable b, String code) {
		List<EdmsInvoiceDetail> l = invoiceDetailRepository.findByReferenceNoAndSalesOrderDetailIdNotNull(code);
		return l == null ? null
			: l.stream() //
			.map(i -> toDetailWithVendorNoAsConcatOfItemCodeAndQtyPerCaseAndPrice(i)).distinct()//
			.collect(groupingBy(BillableDetail::getItemVendorNo, //
				mapping(BillableDetail::getInitialQty, //
					reducing(ZERO, BigDecimal::add)))) //
			.entrySet().stream().map(e -> toBillableDetail(e)) //
			.collect(Collectors.toList());
	}

	private long getNumIdPrefixedIfOnNewEdmsVersionAndDR(String refNo) {
		long id = getNumId(refNo);
		if (id < 0 && StringUtils.contains(refNo, "MAG"))
			id = id - 100_000;
		return id;
	}

	private String getSuffix(String referenceNo) {
		String s = StringUtils.right(referenceNo, 1);
		return StringUtils.isAlpha(s) ? s.toUpperCase() : null;
	}

	private ZonedDateTime toTimeStamp(String ts) {
		return DateTimeUtils.toZonedDateTimeFromTimestampWithSeconds(ts);
	}

	private BillableDetail toDetailWithVendorNoAsConcatOfItemCodeAndQtyPerCaseAndPrice(EdmsInvoiceDetail id) {
		BillableDetail d = new BillableDetail();
		d.setQtyPerCase(getBottlesPerCase(id).intValue());
		d.setItemVendorNo(id.getItemCode() + "," + d.getQtyPerCase() + "," + getPrice(id));
		d.setInitialQty(getQtyInBottles(id, d));
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
		return d;
	}

	private Long getNumId(String refNo) {
		String code = StringUtils.substringAfterLast(refNo, "-");
		long id = Long.valueOf(Code.numbersOnly(code));
		return id < Long.valueOf(invoiceStartId) ? toDeliveryId(id) : id;
	}

	private BigDecimal getBottlesPerCase(EdmsInvoiceDetail id) {
		return itemService.getBottlesPerCase(id);
	}

	private BigDecimal getPrice(EdmsInvoiceDetail id) {
		return isPromo(id) ? BigDecimal.ZERO : getDiscountedPrice(id);
	}

	private BigDecimal getQtyInBottles(EdmsInvoiceDetail id, BillableDetail bd) {
		BigDecimal qty = id.getQty();
		if (isPerCase(id))
			qty = qty.multiply(new BigDecimal(bd.getQtyPerCase()));
		return qty;
	}

	private long toDeliveryId(long id) {
		return id < 0 ? id : -id;
	}

	private boolean isPromo(EdmsInvoiceDetail id) {
		return id.getTransactionCode().equals(Code.PROMO);
	}

	private BigDecimal getDiscountedPrice(EdmsInvoiceDetail id) {
		return getInitialPrice(id).subtract(getDiscount(id)).setScale(2, RoundingMode.HALF_EVEN);
	}

	private boolean isPerCase(EdmsInvoiceDetail id) {
		return id.getUomCode().equals(Code.CS);
	}

	private BigDecimal getInitialPrice(EdmsInvoiceDetail id) {
		return isPerCase(id) ? id.getPriceValue() : itemService.getPricePerCase(id);
	}

	private BigDecimal getDiscount(EdmsInvoiceDetail id) {
		return isPerCase(id) ? dividePerCase(id) : dividePerBottleThenConvertPerCase(id);
	}

	private BigDecimal dividePerCase(EdmsInvoiceDetail id) {
		return divideByQty(id);
	}

	private BigDecimal dividePerBottleThenConvertPerCase(EdmsInvoiceDetail id) {
		return divideByQty(id).multiply(getBottlesPerCase(id));
	}

	private BigDecimal divideByQty(EdmsInvoiceDetail id) {
		return id.getTotalDiscountValue().divide(id.getQty(), 4, RoundingMode.HALF_EVEN);
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

	@Override
	public String getOrderNoFromBillingNo(String no) {
		return toIdText(getNumIdPrefixedIfOnNewEdmsVersionAndDR(no)) + TextUtils.blankIfNull(getSuffix(no));
	}

	@Override
	public List<EdmsInvoice> getBillables(EdmsLoadOrder logp) {
		return invoiceRepository
			.findByOrderDateAndTruckCodeAndStatus(logp.getOrderDate(), logp.getTruckCode(), Code.CLOSED);
	}
}
