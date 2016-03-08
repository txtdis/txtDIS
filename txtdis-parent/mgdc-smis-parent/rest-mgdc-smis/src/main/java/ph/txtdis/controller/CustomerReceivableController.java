package ph.txtdis.controller;

import static java.time.LocalDate.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.text.WordUtils.capitalizeFully;
import static ph.txtdis.util.NumberUtils.isZero;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.domain.Billing;
import ph.txtdis.domain.Customer;
import ph.txtdis.dto.CustomerReceivable;
import ph.txtdis.dto.CustomerReceivableReport;
import ph.txtdis.repository.BillingRepository;

@RestController("customerReceivableController")
@RequestMapping("/customerReceivables")
public class CustomerReceivableController {

	@Autowired
	private BillingRepository billingRepository;

	private List<CustomerReceivable> receivables;

	private List<BigDecimal> totals;

	private String customerName;

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<?> customerReceivableReport(@RequestParam("customer") Customer c,
			@RequestParam("lowerDayCount") long low, @RequestParam("upperDayCount") long up) {
		CustomerReceivableReport r = extractDataFromInvoices(c, low, up);
		return new ResponseEntity<>(r == null ? null : r, HttpStatus.OK);
	}

	private void computeTotals() {
		totals = new ArrayList<>(2);
		for (int i = 0; i < 2; i++)
			totals.add(BigDecimal.ZERO);
		receivables.forEach(r -> {
			totals.set(0, totals.get(0).add(r.getTotalValue()));
			totals.set(1, totals.get(1).add(r.getUnpaidValue()));
		});
	}

	private long daysOver(Billing i) {
		return i.getDueDate().until(now(), DAYS);
	}

	private CustomerReceivableReport extractDataFromInvoices(Customer c, long low, long up) {
		List<Billing> list = billingRepository.findByNumIdNotNullAndFullyPaidFalseAndCustomerOrderByOrderDateDesc(c);
		if (list != null)
			setData(low, up, list);
		return newCustomerReceivableReport();
	}

	private CustomerReceivableReport newCustomerReceivableReport() {
		CustomerReceivableReport r = new CustomerReceivableReport();
		r.setReceivables(receivables);
		r.setTotals(totals);
		r.setCustomer(customerName);
		r.setTimestamp(ZonedDateTime.now());
		return r;
	}

	private void generateReceivableList(long low, long up, List<Billing> list) {
		receivables = list.stream().filter(b -> daysOver(b) >= low && daysOver(b) <= up)
				.map(i -> toCustomerReceivable(i)).collect(toList());
	}

	private CustomerReceivable toCustomerReceivable(Billing b) {
		CustomerReceivable r = new CustomerReceivable();
		r.setId(b.getId());
		r.setOrderNo(b.getOrderNo());
		r.setOrderDate(b.getOrderDate());
		r.setDueDate(b.getDueDate());
		r.setDaysOverCount((int) b.getDueDate().until(now(), DAYS));
		r.setUnpaidValue(unpaid(b));
		r.setTotalValue(b.getTotalValue());
		return r;
	}

	private boolean isPostDatedCheck(Billing i) {
		try {
			return !i.getFullyPaid() && isZero(i.getUnpaidValue());
		} catch (Exception e) {
			return false;
		}
	}

	private BigDecimal unpaid(Billing i) {
		return isPostDatedCheck(i) ? i.getTotalValue() : i.getUnpaidValue();
	}

	private void setCustomer(List<Billing> list) {
		if (list.isEmpty())
			return;
		String n = list.get(0).getCustomer().getName();
		customerName = capitalizeFully(n);
	}

	private void setData(long low, long up, List<Billing> list) {
		generateReceivableList(low, up, list);
		setCustomer(list);
		computeTotals();
	}
}