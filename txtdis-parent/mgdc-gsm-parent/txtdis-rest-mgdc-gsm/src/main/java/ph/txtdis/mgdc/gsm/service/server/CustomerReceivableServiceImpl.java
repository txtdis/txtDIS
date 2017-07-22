package ph.txtdis.mgdc.gsm.service.server;

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
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Billable;
import ph.txtdis.dto.CustomerReceivable;
import ph.txtdis.dto.CustomerReceivableReport;

@Service("customerReceivableService")
public class CustomerReceivableServiceImpl //
		implements CustomerReceivableService {

	@Autowired
	private AllBillingService billingService;

	private List<CustomerReceivable> receivables;

	private List<BigDecimal> totals;

	private String customerName;

	@Override
	public CustomerReceivableReport generateCustomerReceivableReport(Long customerId, long minDaysOver, long maxDaysOver) {
		return generateCustomerReceivablesFromInvoices(customerId, minDaysOver, maxDaysOver);
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

	private long daysOver(Billable i) {
		return i.getDueDate().until(now(), DAYS);
	}

	private CustomerReceivableReport generateCustomerReceivablesFromInvoices(Long customerId, long minDaysOver, long maxDaysOver) {
		List<Billable> list = billingService.findAllUnpaid(customerId);
		if (list != null)
			setCustomerReceivableData(minDaysOver, maxDaysOver, list);
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

	private void generateReceivableList(long min, long max, List<Billable> list) {
		receivables = list.stream().filter(b -> daysOver(b) >= min && daysOver(b) <= max).map(i -> toCustomerReceivable(i)).collect(toList());
	}

	private CustomerReceivable toCustomerReceivable(Billable b) {
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

	private boolean isPostDatedCheck(Billable i) {
		try {
			return !i.getFullyPaid() && isZero(i.getUnpaidValue());
		} catch (Exception e) {
			return false;
		}
	}

	private BigDecimal unpaid(Billable i) {
		return isPostDatedCheck(i) ? i.getTotalValue() : i.getUnpaidValue();
	}

	private void setCustomer(List<Billable> list) {
		if (list.isEmpty())
			return;
		String n = list.get(0).getCustomerName();
		customerName = capitalizeFully(n);
	}

	private void setCustomerReceivableData(long minDaysOver, long maxDaysOver, List<Billable> list) {
		generateReceivableList(minDaysOver, maxDaysOver, list);
		setCustomer(list);
		computeTotals();
	}
}