package ph.txtdis.service;

import static java.math.BigDecimal.ZERO;
import static ph.txtdis.util.NumberUtils.isNegative;

import java.math.BigDecimal;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import ph.txtdis.dto.Billable;
import ph.txtdis.dto.BillableDetail;

@Service("totaledBillableService")
public class TotaledBillableServiceImpl implements TotaledBillableService {

	@Override
	public Billable updateFinalTotals(Billable b) {
		return computeTotals(b, d -> d.getFinalSubtotalValue());
	}

	private Billable computeTotals(Billable b, Function<BillableDetail, BigDecimal> subtotal) {
		b.setGrossValue(computeGross(b, subtotal));
		b.setTotalValue(b.getGrossValue());
		b = computeUnpaid(b);
		return b;
	}

	private BigDecimal computeGross(Billable b, Function<BillableDetail, BigDecimal> subtotal) {
		try {
			BigDecimal gross = b.getDetails().stream().map(subtotal).reduce(ZERO, BigDecimal::add);
			return isNegative(gross) ? null : gross;
		} catch (Exception e) {
			return null;
		}
	}

	private Billable computeUnpaid(Billable b) {
		if (b.getPayments() == null || b.getPayments().isEmpty())
			b.setUnpaidValue(b.getTotalValue());
		return b;
	}

	@Override
	public Billable updateInitialTotals(Billable b) {
		return computeTotals(b, d -> d.getInitialSubtotalValue());
	}
}
