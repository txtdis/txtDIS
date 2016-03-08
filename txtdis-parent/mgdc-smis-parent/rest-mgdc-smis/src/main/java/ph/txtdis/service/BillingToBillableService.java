package ph.txtdis.service;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static ph.txtdis.util.NumberUtils.formatCurrency;
import static ph.txtdis.util.NumberUtils.toPercentRate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static java.math.BigDecimal.ZERO;

import static ph.txtdis.util.DateTimeUtils.toDateDisplay;

import ph.txtdis.domain.Billing;
import ph.txtdis.domain.BillingDetail;
import ph.txtdis.domain.Customer;
import ph.txtdis.domain.CustomerDiscount;
import ph.txtdis.domain.Picking;
import ph.txtdis.domain.Remittance;
import ph.txtdis.domain.RemittanceDetail;
import ph.txtdis.domain.Truck;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.repository.RemittanceDetailRepository;

@Service("billingToBillableService")
public class BillingToBillableService {

	@Autowired
	private RemittanceDetailRepository remitDetailRepo;

	@Value("${vendor.id}")
	private String vendorId;

	public Billable toBillable(Billing b) {
		return b == null ? null : convert(b);
	}

	public List<Billable> toBillable(List<Billing> b) {
		return b == null ? null : convert(b);
	}

	private Long bookingId(Billing b) {
		Long id = b.getBookingId();
		return id == null && b.getCreatedOn() != null ? b.getId() : id;
	}

	private Billable convert(Billing b) {
		Billable i = new Billable();
		if (b.getBookingId() != null) {
			Customer c = b.getCustomer();
			i.setCustomerId(c.getId());
			i.setCustomerName(c.getName());
			i.setCustomerAddress(c.getAddress());
			i.setCustomerLocation(c.getLocation());
			i.setRoute(route(b));

			i.setBookingId(bookingId(b));
			i.setIsRma(b.getRma());

			i.setDueDate(b.getDueDate());
			i.setOrderDate(b.getOrderDate());
			i.setGrossValue(b.getGrossValue());
			i.setTotalValue(b.getTotalValue());
			i.setUnpaidValue(b.getUnpaidValue());

			i.setReceivedBy(b.getReceivedBy());
			i.setReceivedOn(b.getReceivedOn());
			i.setReceivingId(b.getReceivingId());

			i.setBilledBy(b.getBilledBy());
			i.setBilledOn(b.getBilledOn());

			i.setDecidedBy(b.getDecidedBy());
			i.setDecidedOn(b.getDecidedOn());
			i.setIsValid(b.getIsValid());

			i.setPayments(payments(b));
			i.setDiscounts(discounts(b));
			i.setDetails(details(b));
			i.setTruck(truck(b));

			i.setReceivingModifiedBy(b.getReceivingModifiedBy());
			i.setReceivingModifiedOn(b.getReceivingModifiedOn());

			i.setBadOrderAllowanceValue(b.getBadOrderAllowanceValue());
			i.setPrintedBy(b.getPrintedBy());
			i.setPrintedOn(b.getPrintedOn());
		}
		i.setPrefix(b.getPrefix());
		i.setSuffix(b.getSuffix());
		i.setId(b.getId());
		i.setNumId(b.getNumId());
		i.setRemarks(b.getRemarks());
		i.setCreatedBy(b.getCreatedBy());
		i.setCreatedOn(b.getCreatedOn());
		return i;
	}

	private List<Billable> convert(List<Billing> b) {
		return b.stream().map(billing -> convert(billing)).collect(toList());
	}

	private String createEachLevelDiscountText(CustomerDiscount d, BigDecimal total, BigDecimal net) {
		BigDecimal perLevel = net.multiply(toPercentRate(d.getPercent()));
		total = total.add(perLevel);
		net = net.subtract(total);
		return "[" + d.getLevel() + ": " + d.getPercent() + "%] " + formatCurrency(perLevel);
	}

	private String createRemitIdAndDateAndPaymentText(RemittanceDetail p) {
		Remittance r = p.getRemittance();
		return "[" + status(r) + ": " + r + " - " + toDateDisplay(r.getPaymentDate()) + "] "
				+ formatCurrency(p.getPaymentValue());
	}

	private List<BillableDetail> details(Billing b) {
		try {
			return b.getDetails().stream().map(d -> toBillableDetail(d)).collect(toList());
		} catch (Exception e) {
			return emptyList();
		}
	}

	private List<String> discounts(Billing b) {
		try {
			List<CustomerDiscount> d = b.getCustomerDiscounts();
			return d.isEmpty() ? emptyList() : listDiscounts(b);
		} catch (Exception e) {
			return emptyList();
		}
	}

	private BigDecimal discountValue(Billing b) {
		try {
			return b.getGrossValue().subtract(b.getTotalValue());
		} catch (Exception e) {
			return ZERO;
		}
	}

	private List<String> getEachLevelDiscountTextList(Billing b, List<String> list) {
		BigDecimal net = b.getGrossValue();
		b.getCustomerDiscounts().forEach(d -> list.add(createEachLevelDiscountText(d, ZERO, net)));
		return list;
	}

	private List<String> getRemitIdAndDateAndPaymentTextList(List<RemittanceDetail> r, List<String> list) {
		r.forEach(p -> list.add(createRemitIdAndDateAndPaymentText(p)));
		return list;
	}

	private String getTotalInText(BigDecimal t) {
		return "[TOTAL] " + formatCurrency(t);
	}

	private Boolean isValid(RemittanceDetail d) {
		try {
			return d.getRemittance().getIsValid();
		} catch (Exception e) {
			return false;
		}
	}

	private List<String> listDiscounts(Billing b) {
		ArrayList<String> list = new ArrayList<>();
		if (b.getCustomerDiscounts().size() > 1)
			list.add(getTotalInText(discountValue(b)));
		return getEachLevelDiscountTextList(b, list);
	}

	private List<String> listPayments(List<RemittanceDetail> p) {
		ArrayList<String> list = new ArrayList<>();
		if (p.size() > 1)
			list.add(getTotalInText(sumPayments(p)));
		return getRemitIdAndDateAndPaymentTextList(p, list);
	}

	private List<String> payments(Billing b) {
		try {
			List<RemittanceDetail> p = remitDetailRepo.findByBilling(b);
			return p.isEmpty() ? emptyList() : listPayments(p);
		} catch (Exception e) {
			return emptyList();
		}
	}

	private String route(Billing i) {
		Customer c = i.getCustomer();
		if (!c.getId().equals(Long.valueOf(vendorId)))
			try {
				return c.getRouteHistory().stream()//
						.filter(r -> r.getStartDate().compareTo(i.getOrderDate()) <= 0)
						.max((a, b) -> a.getStartDate().compareTo(b.getStartDate()))//
						.get().getRoute().getName();
			} catch (Exception e) {
				return null;
			}
		return null;
	}

	private void setDaysLevel(BillingDetail id, BillableDetail ad) {
		Integer i = id.getOnPurchaseDaysLevel();
		if (i != null) {
			ad.setOnPurchaseDaysLevel(i);
			ad.setOnReceiptDaysLevel(id.getOnReceiptDaysLevel());
		}
	}

	private String status(Remittance r) {
		Boolean b = r.getIsValid();
		if (b == null)
			return "PENDING";
		if (b)
			return "VALID";
		return "INVALID";
	}

	private BigDecimal sumPayments(List<RemittanceDetail> payments) {
		try {
			return payments.stream().filter(d -> isValid(d)).map(d -> d.getPaymentValue()).reduce(ZERO,
					(a, b) -> a.add(b));
		} catch (Exception e) {
			return ZERO;
		}
	}

	private BillableDetail toBillableDetail(BillingDetail id) {
		BillableDetail ad = new BillableDetail();
		ad.setId(id.getItem().getId());
		ad.setItemName(id.getItem().getName());
		ad.setUom(id.getUom());
		ad.setInitialQty(id.getInitialQty());
		ad.setReturnedQty(id.getReturnedQty());
		ad.setQuality(id.getQuality());
		ad.setPriceValue(id.getPriceValue());
		setDaysLevel(id, ad);
		return ad;
	}

	private String truck(Billing b) {
		Picking p = b.getPicking();
		return p == null ? null : truck(p);
	}

	private String truck(Picking p) {
		Truck t = p.getTruck();
		return t == null ? "PICK-UP" : t.getName();
	}
}
