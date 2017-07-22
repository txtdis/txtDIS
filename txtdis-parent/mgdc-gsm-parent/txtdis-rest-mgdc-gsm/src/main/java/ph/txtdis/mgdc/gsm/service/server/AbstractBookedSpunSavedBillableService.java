package ph.txtdis.mgdc.gsm.service.server;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.dto.Billable;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.mgdc.gsm.repository.BookingRepository;
import ph.txtdis.type.PartnerType;

public abstract class AbstractBookedSpunSavedBillableService<BR extends BookingRepository> //
		extends AbstractSpunSavedBillableService //
		implements BookingService {

	@Autowired
	protected BR bookingRepository;

	protected List<Billable> findAllUnpickedOn(LocalDate date, List<PartnerType> types) {
		List<BillableEntity> l = bookingRepository.findByOrderDateAndCustomerTypeInAndBilledOnNullAndReceivedOnNullAndRmaNullAndPickingNull(date,
				types);
		return toModels(l).stream().filter(byValidity()).collect(Collectors.toList());
	}

	private Predicate<Billable> byValidity() {
		return b -> b.getIsValid() == null || b.getIsValid() == true;
	}
}