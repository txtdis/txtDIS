package ph.txtdis.mgdc.gsm.service.server;

import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.dto.Billable;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.mgdc.gsm.repository.BookingRepository;
import ph.txtdis.type.PartnerType;

import java.time.LocalDate;
import java.util.List;

import static java.util.Arrays.asList;
import static ph.txtdis.type.PartnerType.EX_TRUCK;
import static ph.txtdis.type.PartnerType.OUTLET;

public abstract class AbstractBookingService<BR extends BookingRepository> //
	extends AbstractSpunSavedBillableService //
	implements BookingService {

	protected static final List<PartnerType> DELIVERED_ROUTES = asList(EX_TRUCK, OUTLET);

	@Autowired
	protected BR bookingRepository;

	@Override
	public List<Billable> findAllUnpicked(LocalDate d) {
		List<BillableEntity> l =
			bookingRepository.findByOrderDateAndCustomerTypeInAndBilledOnNullAndReceivedOnNullAndRmaNullAndPickingNull(//
				d, DELIVERED_ROUTES);
		return toModels(l);
	}

	@Override
	public Billable findByReferenceId(Long id) throws NotFoundException {
		BillableEntity e = id == null ? null : findEntityByBookingNo(id.toString());
		if (e == null)
			throw new NotFoundException("Booking No. " + id);
		return toModel(e);
	}
}