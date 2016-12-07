package ph.txtdis.service;

import java.sql.Date;

import org.springframework.stereotype.Service;

import ph.txtdis.domain.BillableEntity;
import ph.txtdis.dto.Billable;

@Service("badRmaService")
public class BadRmaServiceImpl extends AbstractSpunBillableService implements BadRmaService {

	@Override
	public Billable findByDate(Date d) {
		BillableEntity b = repository
				.findFirstByRmaFalseAndBookingIdNotNullAndCreatedOnBetweenOrderByCreatedOnAsc(start(d), end(d));
		return toDTO(b);
	}

	@Override
	public Billable next(Long id) {
		BillableEntity b = repository
				.findFirstByRmaFalseAndBookingIdNotNullAndBookingIdGreaterThanOrderByBookingIdAsc(id);
		return toDTO(b);
	}

	@Override
	public Billable previous(Long id) {
		BillableEntity b = repository.findFirstByRmaFalseAndBookingIdNotNullAndBookingIdLessThanOrderByBookingIdDesc(id);
		return toDTO(b);
	}

	@Override
	protected Billable firstSpun() {
		return toDTO(repository.findFirstByRmaFalseAndBookingIdNotNullOrderByBookingIdAsc());
	}

	@Override
	protected Billable lastSpun() {
		return toDTO(repository.findFirstByRmaFalseAndBookingIdNotNullOrderByBookingIdDesc());
	}
}