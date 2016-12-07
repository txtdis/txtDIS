package ph.txtdis.service;

import java.sql.Date;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import ph.txtdis.domain.BillableEntity;
import ph.txtdis.dto.Billable;

@Service("receivingReportService")
public class ReceivingReportServiceImpl extends AbstractSpunBillableService implements ReceivingReportService {

	@Override
	public Billable findByDate(@RequestParam("on") Date d) {
		BillableEntity b = repository
				.findFirstByCustomerIdNotAndRmaNullAndReceivingIdNotNullAndReceivedOnBetweenOrderByReceivedOnAsc(vendorId(),
						start(d), end(d));
		return toDTO(b);
	}

	@Override
	public Billable next(Long id) {
		BillableEntity b = repository
				.findFirstByCustomerIdNotAndRmaNullAndReceivingIdNotNullAndReceivingIdGreaterThanOrderByReceivingIdAsc(
						vendorId(), id);
		return toDTO(b);
	}

	@Override
	public Billable previous(Long id) {
		BillableEntity b = repository
				.findFirstByCustomerIdNotAndRmaNullAndReceivingIdNotNullAndReceivingIdLessThanOrderByReceivingIdDesc(
						vendorId(), id);
		return toDTO(b);
	}

	@Override
	protected Billable firstSpun() {
		BillableEntity b = repository
				.findFirstByCustomerIdNotAndRmaNullAndReceivingIdNotNullOrderByReceivingIdAsc(vendorId());
		return toDTO(b);
	}

	@Override
	protected Billable lastSpun() {
		BillableEntity b = repository
				.findFirstByCustomerIdNotAndRmaNullAndReceivingIdNotNullOrderByReceivingIdDesc(vendorId());
		return toDTO(b);
	}
}