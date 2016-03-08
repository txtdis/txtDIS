package ph.txtdis.service;

import static java.time.ZonedDateTime.now;
import static java.util.stream.Collectors.toList;
import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.util.NumberUtils.isZero;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.Billing;
import ph.txtdis.domain.BillingDetail;
import ph.txtdis.domain.Customer;
import ph.txtdis.domain.CustomerDiscount;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.repository.BillingRepository;
import ph.txtdis.repository.CustomerDiscountRepository;
import ph.txtdis.repository.CustomerRepository;
import ph.txtdis.repository.ItemRepository;

@Service("billableToBillingService")
public class BillableToBillingService {

	private static Logger logger = getLogger(BillableToBillingService.class);

	@Autowired
	private CustomerRepository customer;

	@Autowired
	private CustomerDiscountRepository discount;

	@Autowired
	private BillingRepository billing;

	@Autowired
	private ItemRepository item;

	@Value("${vendor.id}")
	private String vendorId;

	private Customer vendor;

	public Billing toBilling(Billable b) {
		return b == null ? null : convert(b);
	}

	private boolean allReturned(Billable a) {
		return isZero(a.getTotalValue());
	}

	private Long bookingId() {
		Billing b = billing.findFirstByBookingIdNotNullOrderByBookingIdDesc();
		return b.getBookingId() + 1;
	}

	private void cancelIfAllReturned(Billing b, Billable a) {
		if (allReturned(a))
			setCancelledData(b);
		else
			b.setFullyPaid(false);
	}

	private boolean cancelled(Billable a) {
		return voided(a) || allReturned(a);
	}

	private Billing convert(Billable i) {
		return i.getId() == null ? create(i) : update(i);
	}

	private Billing create(Billable a) {
		logger.info("Creating new billing");
		Billing b = new Billing();
		if (cancelled(a)) {
			b.setPrefix(a.getPrefix());
			b.setSuffix(a.getSuffix());
			b.setNumId(a.getNumId());
			b.setOrderDate(a.getOrderDate());
			setCancelledData(b);
		} else {
			b.setCustomer(customer(a));
			b.setOrderDate(a.getOrderDate());
			b.setDueDate(a.getDueDate());
			b.setCustomerDiscounts(customerDiscounts(a));
			b.setDetails(details(b, a));
			b.setBookingId(bookingId());
			b.setRma(a.getIsRma());
			b.setBadOrderAllowanceValue(a.getBadOrderAllowanceValue());
			updateTotals(b, a);
			updateDecisionData(b, a);
		}
		return b;
	}

	private void createReceivingData(Billing b, Billable a) {
		if (a.getReceivedBy() != null && a.getReceivedOn() == null) {
			b.setReceivedBy(a.getReceivedBy());
			b.setReceivedOn(now());
			b.setReceivingId(receivingId());
			b.setDetails(receivingDetails(b, a));
		}
	}

	private Customer customer(Billable a) {
		Long id = a.getCustomerId();
		return id == null ? null : customer.findOne(id);
	}

	private List<CustomerDiscount> customerDiscounts(Billable a) {
		try {
			return a.getDiscountIds().stream().map(id -> discount.findOne(id)).collect(toList());
		} catch (Exception e) {
			return null;
		}
	}

	private Long deliveryId() {
		Billing b = billing.findFirstByNumIdNotNullAndNumIdLessThanOrderByNumIdAsc(0L);
		return b.getNumId() - 1;
	}

	private BillingDetail detail(Billing b, Billable a, BillableDetail ad) {
		BillingDetail bd = new BillingDetail();
		bd.setBilling(b);
		bd.setItem(item.findOne(ad.getId()));
		bd.setUom(ad.getUom());
		bd.setInitialQty(ad.getInitialQty());
		bd.setQuality(ad.getQuality());
		bd.setReturnedQty(ad.getReturnedQty());
		bd.setPriceValue(ad.getPriceValue());
		if (isAPurchaseOrder(a)) {
			bd.setOnPurchaseDaysLevel(ad.getOnPurchaseDaysLevel());
			bd.setOnReceiptDaysLevel(ad.getOnReceiptDaysLevel());
		}
		return bd;
	}

	private List<BillingDetail> details(Billing b, Billable a) {
		return a.getDetails().stream().map(d -> detail(b, a, d)).collect(toList());
	}

	private void fullyPayIfReturnedItemOrderHasBeenInvoiced(Billing b, Billable a) {
		if (a.getIsRma() != null)
			b.setFullyPaid(true);
	}

	private boolean isAPurchaseOrder(Billable a) {
		return a.getCustomerName().equals(vendor().getName());
	}

	private Long numId(Billable a) {
		Long id = a.getNumId();
		return id == null ? deliveryId() : id;
	}

	private List<BillingDetail> receivingDetails(Billing b, Billable a) {
		Map<Long, BillingDetail> m = new HashMap<>();
		for (BillingDetail bd : b.getDetails())
			m.put(bd.getItem().getId(), bd);
		for (BillableDetail ad : a.getDetails()) {
			Long id = ad.getId();
			BillingDetail bid = m.get(id);
			if (bid != null) {
				bid.setReturnedQty(ad.getReturnedQty());
				m.put(id, bid);
			}
		}
		return new ArrayList<>(m.values());
	}

	private Long receivingId() {
		Billing b = billing.findFirstByReceivingIdNotNullOrderByReceivingIdDesc();
		Long id = b == null ? 0L : b.getReceivingId();
		return id + 1;
	}

	private Billing setCancelledData(Billing b) {
		b.setRemarks("CANCELLED");
		b.setFullyPaid(true);
		return b;
	}

	private Billing setThreePartId(Billing b, Billable a) {
		b.setNumId(numId(a));
		b.setPrefix(a.getPrefix());
		b.setSuffix(a.getSuffix());
		return b;
	}

	private Billing update(Billable a) {
		logger.info("Updating billing");
		Billing b = billing.findOne(a.getId());
		updateBillingData(b, a);
		updateReceivingData(b, a);
		updateDecisionData(b, a);
		updatePickData(b, a);
		b.setBadOrderAllowanceValue(a.getBadOrderAllowanceValue());
		return b;
	}

	private void updateBillingData(Billing b, Billable a) {
		logger.info("Updating billing data");
		if (a.getIsValid() != null && !a.getIsValid()) {
			b.setBilledBy(null);
			b.setBilledOn(null);
			b.setPrefix(null);
			b.setNumId(null);
			b.setSuffix(null);
			b.setFullyPaid(null);
		} else if (a.getBilledBy() != null && a.getBilledOn() == null) {
			b.setBilledBy(a.getBilledBy());
			b.setBilledOn(now());
			setThreePartId(b, a);
			updateTotals(b, a);
			cancelIfAllReturned(b, a);
			fullyPayIfReturnedItemOrderHasBeenInvoiced(b, a);
		}
	}

	private void updateDecisionData(Billing b, Billable a) {
		logger.info("Updating decision data");
		Boolean av = a.getIsValid();
		Boolean bv = b.getIsValid();
		logger.info("Is billable valid = " + av);
		logger.info("Is billing valid = " + bv);
		if (av == null && bv == null)
			return;
		b.setIsValid(av);
		b.setRemarks(a.getRemarks());
		b.setDecidedBy(a.getDecidedBy());
		b.setDecidedOn(a.getDecidedOn());
	}

	private void updatePickData(Billing b, Billable a) {
		b.setPrintedBy(a.getPrintedBy());
		b.setPrintedOn(a.getPrintedOn());
	}

	private void updateReceivingData(Billing b, Billable a) {
		createReceivingData(b, a);
		updateReceivingModification(b, a);
	}

	private void updateReceivingModification(Billing b, Billable a) {
		logger.info("Updating receiving modifications");
		String modded = a.getReceivingModifiedBy();
		if (modded != null) {
			b.setReceivingModifiedBy(modded);
			b.setReceivingModifiedOn(now());
			b.setDetails(receivingDetails(b, a));
		}
	}

	private void updateTotals(Billing b, Billable a) {
		b.setGrossValue(a.getGrossValue());
		b.setTotalValue(a.getTotalValue());
		b.setUnpaidValue(a.getUnpaidValue());
	}

	private Customer vendor() {
		if (vendor == null)
			vendor = customer.findOne(Long.valueOf(vendorId));
		return vendor;
	}

	private boolean voided(Billable a) {
		return a.getBookingId() == null && a.getCustomerId() == null;
	}
}
