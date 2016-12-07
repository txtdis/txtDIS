package ph.txtdis.service;

import java.sql.Date;

import org.springframework.stereotype.Service;

import ph.txtdis.domain.BillableEntity;
import ph.txtdis.dto.Billable;

@Service("purchaseOrderService")
public class PurchaseOrderServiceImpl extends AbstractSpunBillableService implements PurchaseOrderService {

	@Override
	public Billable findByDate(Date d) {
		BillableEntity b = repository.findFirstByBookingIdNotNullAndCustomerIdAndCreatedOnBetweenOrderByCreatedOnAsc(
				vendorId(), start(d), end(d));
		return toDTO(b);
	}

	@Override
	public Billable next(Long id) {
		BillableEntity b = repository
				.findFirstByCustomerIdAndBookingIdNotNullAndBookingIdGreaterThanOrderByBookingIdAsc(vendorId(), id);
		return toDTO(b);
	}

	@Override
	public Billable previous(Long id) {
		BillableEntity b = repository
				.findFirstByCustomerIdAndBookingIdNotNullAndBookingIdLessThanOrderByBookingIdDesc(vendorId(), id);
		return toDTO(b);
	}

	@Override
	protected Billable firstSpun() {
		BillableEntity b = repository.findFirstByCustomerIdAndBookingIdNotNullOrderByBookingIdAsc(vendorId());
		return toDTO(b);
	}

	@Override
	protected Billable lastSpun() {
		BillableEntity b = repository.findFirstByCustomerIdAndBookingIdNotNullOrderByBookingIdDesc(vendorId());
		return toDTO(b);
	}
}