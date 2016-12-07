package ph.txtdis.service;

import java.sql.Date;

import org.springframework.stereotype.Service;

import ph.txtdis.domain.BillableEntity;
import ph.txtdis.dto.Billable;

@Service("goodRmaService")
public class GoodRmaServiceImpl extends AbstractSpunBillableService implements GoodRmaService {

	@Override
	public Billable findByDate(Date d) {
		BillableEntity b = repository
				.findFirstByRmaTrueAndBookingIdNotNullAndCreatedOnBetweenOrderByCreatedOnAsc(start(d), end(d));
		return toDTO(b);
	}

	@Override
	public Billable next(Long id) {
		BillableEntity b = repository.findFirstByRmaTrueAndBookingIdNotNullAndBookingIdGreaterThanOrderByBookingIdAsc(id);
		return toDTO(b);
	}

	@Override
	public Billable previous(Long id) {
		BillableEntity b = repository.findFirstByRmaTrueAndBookingIdNotNullAndBookingIdLessThanOrderByBookingIdDesc(id);
		return toDTO(b);
	}

	@Override
	protected Billable firstSpun() {
		BillableEntity b = repository.findFirstByRmaTrueAndBookingIdNotNullOrderByBookingIdAsc();
		return toDTO(b);
	}

	@Override
	protected Billable lastSpun() {
		BillableEntity b = repository.findFirstByRmaTrueAndBookingIdNotNullOrderByBookingIdDesc();
		return toDTO(b);
	}
}