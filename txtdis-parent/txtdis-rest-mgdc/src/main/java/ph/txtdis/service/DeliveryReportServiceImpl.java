package ph.txtdis.service;

import java.sql.Date;

import org.springframework.stereotype.Service;

import ph.txtdis.domain.BillableEntity;
import ph.txtdis.dto.Billable;

@Service("deliveryReportService")
public class DeliveryReportServiceImpl extends AbstractSpunBillableService implements DeliveryReportService {

	@Override
	public Billable findByDate(Date d) {
		BillableEntity b = repository.findFirstByNumIdLessThanAndOrderDateOrderByIdAsc(0L, d.toLocalDate());
		return toDTO(b);
	}

	@Override
	public Billable next(Long id) {
		BillableEntity b = repository.findFirstByNumIdLessThanAndIdGreaterThanOrderByIdAsc(0L, id);
		return toDTO(b);
	}

	@Override
	public Billable previous(Long id) {
		BillableEntity b = repository.findFirstByNumIdLessThanAndIdLessThanOrderByIdDesc(0L, id);
		return toDTO(b);
	}

	@Override
	protected Billable firstSpun() {
		BillableEntity b = repository.findFirstByNumIdLessThanOrderByIdAsc(0L);
		return toDTO(b);
	}

	@Override
	protected Billable lastSpun() {
		BillableEntity b = repository.findFirstByNumIdLessThanOrderByIdDesc(0L);
		return toDTO(b);
	}
}