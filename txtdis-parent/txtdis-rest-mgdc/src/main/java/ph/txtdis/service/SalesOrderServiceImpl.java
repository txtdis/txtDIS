package ph.txtdis.service;

import static ph.txtdis.dto.PartnerType.OUTLET;

import java.sql.Date;

import org.springframework.stereotype.Service;

import ph.txtdis.domain.BillableEntity;
import ph.txtdis.dto.Billable;

@Service("salesOrderService")
public class SalesOrderServiceImpl extends AbstractSpunBillableService implements SalesOrderService {

	@Override
	public Billable findByDate(Date d) {
		BillableEntity b = repository.findFirstByBookingIdNotNullAndCustomerTypeAndRmaNullAndOrderDateOrderByBookingIdAsc(
				OUTLET, d.toLocalDate());
		return toDTO(b);
	}

	@Override
	public Billable next(Long id) {
		BillableEntity b = repository
				.findFirstByCustomerTypeAndRmaNullAndBookingIdNotNullAndBookingIdGreaterThanOrderByBookingIdAsc(OUTLET, id);
		return toDTO(b);
	}

	@Override
	public Billable previous(Long id) {
		BillableEntity b = repository
				.findFirstByCustomerTypeAndRmaNullAndBookingIdNotNullAndBookingIdLessThanOrderByBookingIdDesc(OUTLET, id);
		return toDTO(b);
	}

	@Override
	protected Billable firstSpun() {
		BillableEntity b = repository.findFirstByCustomerTypeAndRmaNullAndBookingIdNotNullOrderByBookingIdAsc(OUTLET);
		return toDTO(b);
	}

	@Override
	protected Billable lastSpun() {
		BillableEntity b = repository.findFirstByCustomerTypeAndRmaNullAndBookingIdNotNullOrderByBookingIdDesc(OUTLET);
		return toDTO(b);
	}
}