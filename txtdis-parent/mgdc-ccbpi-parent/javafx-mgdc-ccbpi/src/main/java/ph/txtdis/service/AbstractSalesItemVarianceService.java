package ph.txtdis.service;

import static java.math.BigDecimal.ZERO;
import static java.util.Arrays.asList;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import ph.txtdis.dto.SalesItemVariance;

public abstract class AbstractSalesItemVarianceService extends AbstractVarianceService<SalesItemVariance>
		implements SellerTrackedVarianceService {

	@Override
	public LocalDate getEndDate() {
		if (end == null)
			end = today();
		return end;
	}

	@Override
	public LocalDate getStartDate() {
		if (start == null)
			start = today();
		return start;
	}

	@Override
	public List<BigDecimal> getTotals(List<SalesItemVariance> l) {
		return asList(getExpectedQty(l), getActualQty(), getVariance(), getValue());
	}

	private BigDecimal getExpectedQty(List<SalesItemVariance> l) {
		return list().stream().map(v -> v.getExpectedQty()).reduce(ZERO, BigDecimal::add);
	}

	private BigDecimal getActualQty() {
		return list().stream().map(v -> v.getActualQty()).reduce(ZERO, BigDecimal::add);
	}

	private BigDecimal getVariance() {
		return list().stream().map(v -> v.getVarianceQty()).reduce(ZERO, BigDecimal::add);
	}

	private BigDecimal getValue() {
		return list().stream().map(v -> v.getValue()).reduce(ZERO, BigDecimal::add);
	}
}
