package ph.txtdis.mgdc.ccbpi.service;

import ph.txtdis.dto.SalesItemVariance;
import ph.txtdis.service.AbstractVarianceService;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

import static java.math.BigDecimal.ZERO;
import static java.util.Arrays.asList;

public abstract class AbstractSalesItemVarianceService //
	extends AbstractVarianceService<SalesItemVariance> //
	implements SellerTrackedVarianceService {

	@Override
	public List<BigDecimal> getTotals(List<SalesItemVariance> l) {
		return asList(bookedQty(l), deliveredQty(l), returnedQty(l), varianceQty(l), value(l));
	}

	protected BigDecimal bookedQty(List<SalesItemVariance> l) {
		return total(l, SalesItemVariance::getExpectedQty);
	}

	private BigDecimal deliveredQty(List<SalesItemVariance> l) {
		return total(l, SalesItemVariance::getActualQty);
	}

	protected BigDecimal returnedQty(List<SalesItemVariance> l) {
		return total(l, SalesItemVariance::getReturnedQty);
	}

	protected BigDecimal varianceQty(List<SalesItemVariance> l) {
		return total(l, SalesItemVariance::getVarianceQty);
	}

	protected BigDecimal value(List<SalesItemVariance> l) {
		return total(l, SalesItemVariance::getValue);
	}

	protected BigDecimal total(List<SalesItemVariance> l, Function<SalesItemVariance, BigDecimal> qty) {
		return l.stream().map(qty).reduce(ZERO, BigDecimal::add);
	}
}
