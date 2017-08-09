package ph.txtdis.mgdc.gsm.service.server;

import org.springframework.stereotype.Service;
import ph.txtdis.dto.Billable;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.mgdc.gsm.repository.BadRmaRepository;

import java.math.BigDecimal;
import java.util.List;

import static java.math.BigDecimal.ZERO;

@Service("badRmaService")
public class BadRmaServiceImpl //
	extends AbstractRmaService<BadRmaRepository> //
	implements BadRmaService {

	@Override
	public Billable findByReferenceId(Long id) throws NotFoundException {
		BillableEntity e = rmaRepository.findByRmaAndBookingId(false, id);
		return throwExceptionIfNotFound("Bad RMA No. ", e, id);
	}

	@Override
	public Billable findClosedRmaByCustomerToDetermineAllowanceBalance(Long id) {
		List<BillableEntity> b = rmaRepository.findByRmaAndNumIdNotNullAndCustomerId(false, id);
		BigDecimal v = b.stream().map(BillableEntity::getTotalValue).reduce(ZERO, BigDecimal::add);
		return toTotalValueOnlyBillable(v);
	}

	@Override
	protected BillableEntity firstEntity() {
		return rmaRepository.findFirstByRmaOrderByIdAsc(false);
	}

	@Override
	protected BillableEntity lastEntity() {
		return rmaRepository.findFirstByRmaOrderByIdDesc(false);
	}

	@Override
	protected BillableEntity nextEntity(Long id) {
		return rmaRepository.findFirstByRmaAndIdGreaterThanOrderByIdAsc(false, id);
	}

	@Override
	protected BillableEntity previousEntity(Long id) {
		return rmaRepository.findFirstByRmaAndIdLessThanOrderByIdDesc(true, id);
	}
}