package ph.txtdis.service;

import java.sql.Date;

import ph.txtdis.domain.BillableEntity;
import ph.txtdis.dto.Billable;

public abstract class AbstractPurchaseReceiptService extends AbstractSpunBillableService
		implements PurchaseReceiptService {

	@Override
	public Billable findByDate(Date d) {
		BillableEntity b = repository.findFirstByCustomerIdAndReceivingIdNotNullAndCreatedOnBetweenOrderByCreatedOnAsc(
				vendorId(), start(d), end(d));
		return toDTO(b);
	}

	@Override
	public Billable next(Long id) {
		BillableEntity b = repository
				.findFirstByCustomerIdAndReceivingIdNotNullAndReceivingIdGreaterThanOrderByReceivingIdAsc(vendorId(), id);
		return toDTO(b);
	}

	@Override
	public Billable previous(Long id) {
		BillableEntity b = repository
				.findFirstByCustomerIdAndReceivingIdNotNullAndReceivingIdLessThanOrderByReceivingIdDesc(vendorId(), id);
		return toDTO(b);
	}

	@Override
	protected Billable firstSpun() {
		BillableEntity b = repository.findFirstByCustomerIdAndReceivingIdNotNullOrderByReceivingIdAsc(vendorId());
		return toDTO(b);
	}

	@Override
	protected Billable lastSpun() {
		BillableEntity b = repository.findFirstByCustomerIdAndReceivingIdNotNullOrderByReceivingIdDesc(vendorId());
		return toDTO(b);
	}
}