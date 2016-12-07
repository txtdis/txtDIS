package ph.txtdis.service;

import org.springframework.stereotype.Service;

import ph.txtdis.domain.BillableEntity;
import ph.txtdis.dto.Billable;

@Service("billingService")
public class BillingServiceImpl extends AbstractBillableService {

	@Override
	protected boolean cancelled(Billable b) {
		return false;
	}

	@Override
	public BillableEntity findEntityByLoadOrSalesOrderId(Long id) {
		return repository.findByBookingId(id);
	}
}