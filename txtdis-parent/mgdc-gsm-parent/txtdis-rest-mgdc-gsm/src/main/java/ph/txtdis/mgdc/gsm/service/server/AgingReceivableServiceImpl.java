package ph.txtdis.mgdc.gsm.service.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ph.txtdis.dto.AgingReceivable;
import ph.txtdis.dto.AgingReceivableReport;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.mgdc.gsm.domain.CustomerEntity;
import ph.txtdis.mgdc.gsm.repository.BillableRepository;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static java.math.BigDecimal.ZERO;
import static java.time.LocalDate.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.stream.Collectors.toList;
import static ph.txtdis.util.NumberUtils.isZero;

@Service("agingReceivableService")
public class AgingReceivableServiceImpl //
	implements AgingReceivableService {

	private final static int CURRENT = 0;

	private final static int ONE_TO_SEVEN = 1;

	private final static int EIGHT_TO_FIFTEEN = 2;

	private final static int SIXTEEN_TO_THIRTY = 3;

	private final static int MORE_THAN_THIRTY = 4;

	private final static int AGING = 5;

	private final static int ALL = 6;

	@Autowired
	private BillableRepository repository;

	private Vector<AgingReceivable> agingReceivables;

	private List<BigDecimal> totals;

	@Override
	public AgingReceivableReport getAgingReceivableReport() {
		try {
			initializeAgingReceivableVectorAndTotalArrayList();
			sumInvoiceBalancesToItsDaysOverReceivableTotal();
			return createAgingReceivableReport();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private void initializeAgingReceivableVectorAndTotalArrayList() {
		agingReceivables = new Vector<>();
		totals = new ArrayList<>(7);
		for (int i = 0; i < 7; i++)
			totals.add(ZERO);
	}

	private void sumInvoiceBalancesToItsDaysOverReceivableTotal() {
		List<BillableEntity> l = repository.findByNumIdNotNullAndFullyPaidFalseOrderByCustomerAscOrderDateDesc();
		l = l.stream().map(b -> reviseUnpaidForPostDatedCheck(b)).collect(toList());
		l.forEach(i -> sumBalancesPerCustomer(i));
	}

	private AgingReceivableReport createAgingReceivableReport() {
		AgingReceivableReport r = new AgingReceivableReport();
		r.setReceivables(agingReceivables);
		r.setTotals(totals);
		r.setTimestamp(ZonedDateTime.now());
		return r;
	}

	private BillableEntity reviseUnpaidForPostDatedCheck(BillableEntity b) {
		if (isZero(b.getUnpaidValue()))
			b.setUnpaidValue(b.getTotalValue());
		return b;
	}

	private void sumBalancesPerCustomer(BillableEntity i) {
		createNewAgingReceivablePerNewCustomer(i);
		sumEachDaysOverBalances(i);
		sumAllBalances(i);
	}

	private void createNewAgingReceivablePerNewCustomer(BillableEntity i) {
		if (agingReceivables.isEmpty() || lastItem().getId() != i.getCustomer().getId())
			agingReceivables.add(newAgingReceivable(i));
	}

	private void sumEachDaysOverBalances(BillableEntity i) {
		long daysOver = i.getDueDate().until(now(), DAYS);
		sumEachDaysOverBalances(i, daysOver);
		sumAgingBalances(i, daysOver);
	}

	private void sumAllBalances(BillableEntity i) {
		BigDecimal balance = lastItem().getTotalValue().add(i.getUnpaidValue());
		lastItem().setTotalValue(balance);
		totals.set(ALL, totals.get(ALL).add(i.getUnpaidValue()));
	}

	private AgingReceivable lastItem() {
		return agingReceivables.lastElement();
	}

	private AgingReceivable newAgingReceivable(BillableEntity i) {
		AgingReceivable r = new AgingReceivable();
		CustomerEntity c = i.getCustomer();
		r.setId(c.getId());
		r.setCustomer(c.getName());
		r.setSeller(c.getSeller());
		return r;
	}

	private void sumEachDaysOverBalances(BillableEntity i, long daysOver) {
		if (daysOver <= 0)
			addToCurrent(i);
		else if (daysOver >= 1 && daysOver <= 7)
			addTo1to7(i);
		else if (daysOver >= 8 && daysOver <= 15)
			addTo8to15(i);
		else if (daysOver >= 16 && daysOver <= 30)
			addTo16to30(i);
		else
			addToMoreThan30(i);
	}

	private void sumAgingBalances(BillableEntity i, long daysOver) {
		if (daysOver > 0)
			addToAging(i);
	}

	private void addToCurrent(BillableEntity i) {
		BigDecimal balance = lastItem().getCurrentValue().add(i.getUnpaidValue());
		lastItem().setCurrentValue(balance);
		totals.set(CURRENT, totals.get(CURRENT).add(i.getUnpaidValue()));
	}

	private void addTo1to7(BillableEntity i) {
		BigDecimal balance = lastItem().getOneToSevenValue().add(i.getUnpaidValue());
		lastItem().setOneToSevenValue(balance);
		totals.set(ONE_TO_SEVEN, totals.get(ONE_TO_SEVEN).add(i.getUnpaidValue()));
	}

	private void addTo8to15(BillableEntity i) {
		BigDecimal balance = lastItem().getEightToFifteenValue().add(i.getUnpaidValue());
		lastItem().setEightToFifteenValue(balance);
		totals.set(EIGHT_TO_FIFTEEN, totals.get(EIGHT_TO_FIFTEEN).add(i.getUnpaidValue()));
	}

	private void addTo16to30(BillableEntity i) {
		BigDecimal balance = lastItem().getSixteenToThirtyValue().add(i.getUnpaidValue());
		lastItem().setSixteenToThirtyValue(balance);
		totals.set(SIXTEEN_TO_THIRTY, totals.get(SIXTEEN_TO_THIRTY).add(i.getUnpaidValue()));
	}

	private void addToMoreThan30(BillableEntity i) {
		BigDecimal balance = lastItem().getGreaterThanThirtyValue().add(i.getUnpaidValue());
		lastItem().setGreaterThanThirtyValue(balance);
		totals.set(MORE_THAN_THIRTY, totals.get(MORE_THAN_THIRTY).add(i.getUnpaidValue()));
	}

	private void addToAging(BillableEntity i) {
		BigDecimal balance = lastItem().getAgingValue().add(i.getUnpaidValue());
		lastItem().setAgingValue(balance);
		totals.set(AGING, totals.get(AGING).add(i.getUnpaidValue()));
	}
}