package ph.txtdis.mgdc.gsm.service.server;

import org.springframework.stereotype.Service;
import ph.txtdis.dto.Billable;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.mgdc.gsm.repository.GoodRmaRepository;

@Service("goodRmaService")
public class GoodRmaServiceImpl
	extends AbstractRmaService<GoodRmaRepository> //
	implements GoodRmaService {

	@Override
	public Billable findByReferenceId(Long id) throws NotFoundException {
		BillableEntity e = rmaRepository.findByRmaAndBookingId(true, id);
		return throwExceptionIfNotFound("Good RMA No. ", e, id);
	}

	@Override
	protected BillableEntity firstEntity() {
		return rmaRepository.findFirstByRmaOrderByIdAsc(true);
	}

	@Override
	protected BillableEntity nextEntity(Long id) {
		return rmaRepository.findFirstByRmaAndIdGreaterThanOrderByIdAsc(true, id);
	}

	@Override
	protected BillableEntity lastEntity() {
		return rmaRepository.findFirstByRmaOrderByIdDesc(true);
	}

	@Override
	protected BillableEntity previousEntity(Long id) {
		return rmaRepository.findFirstByRmaAndIdLessThanOrderByIdDesc(true, id);
	}
}