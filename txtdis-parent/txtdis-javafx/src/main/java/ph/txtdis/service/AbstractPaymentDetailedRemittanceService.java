package ph.txtdis.service;

import ph.txtdis.dto.RemittanceDetail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPaymentDetailedRemittanceService
	extends AbstractRemittanceService
	implements PaymentDetailedRemittanceService {

	private BigDecimal remaining;

	@Override
	public BigDecimal getRemaining() {
		return remaining;
	}

	@Override
	public void reset() {
		super.reset();
		remaining = null;
	}

	@Override
	public void setPayment(BigDecimal p) {
		super.setPayment(p);
		remaining = p;
	}

	@Override
	public List<RemittanceDetail> getDetails() {
		List<RemittanceDetail> l = get().getDetails();
		return l == null ? new ArrayList<>() : new ArrayList<>(l);
	}
}
