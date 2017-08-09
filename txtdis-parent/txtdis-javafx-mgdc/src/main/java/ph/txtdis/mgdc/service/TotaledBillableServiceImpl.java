package ph.txtdis.mgdc.service;

import org.springframework.stereotype.Service;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.BillableDetail;

import java.math.BigDecimal;
import java.util.function.Function;

import static java.math.BigDecimal.ZERO;

@Service("totaledBillableService")
public class TotaledBillableServiceImpl //
	implements TotaledBillableService {

	@Override
	public Billable updateFinalTotals(Billable b) {
		return computeTotals(b, d -> d.getFinalSubtotalValue());
	}

	private Billable computeTotals(Billable b, Function<BillableDetail, BigDecimal> subtotal) {
		b.setGrossValue(computeGross(b, subtotal));
		b.setTotalValue(computeTotal(b));
		b = computeUnpaid(b);
		return b;
	}

	private BigDecimal computeGross(Billable b, Function<BillableDetail, BigDecimal> subtotal) {
		return b.getDetails().stream().filter(d -> d != null).map(subtotal).reduce(ZERO, BigDecimal::add);
	}

	private BigDecimal computeTotal(Billable b) {
		return b.getGrossValue().add(b.getAdjustmentValue());
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
