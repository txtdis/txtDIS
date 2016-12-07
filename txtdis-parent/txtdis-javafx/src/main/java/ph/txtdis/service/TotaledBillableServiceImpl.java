package ph.txtdis.service;

import static java.math.BigDecimal.ZERO;
import static ph.txtdis.util.NumberUtils.isNegative;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import ph.txtdis.dto.Billable;

@Service("totaledBillableService")
public class TotaledBillableServiceImpl implements TotaledBillableService {

	@Override
	public Billable updateTotals(Billable b) {
		if (b.getId() == null)
			b = updateGrossAndTotal(b);
		b = computeUnpaid(b);
		return b;
	}

	private Billable updateGrossAndTotal(Billable b) {
		b.setGrossValue(computeGross(b));
		b.setTotalValue(b.getGrossValue());
		return b;
	}

	private BigDecimal computeGross(Billable b) {
		try {
			BigDecimal gross = b.getDetails().stream().map(d -> d.getSubtotalValue()).reduce(ZERO, BigDecimal::add);
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
}
