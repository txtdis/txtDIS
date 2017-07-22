package ph.txtdis.mgdc.gsm.service;

import static ph.txtdis.type.PartnerType.EX_TRUCK;

import org.springframework.stereotype.Service;

import ph.txtdis.dto.Billable;
import ph.txtdis.exception.AlreadyBilledBookingException;

@Service("receivingReportService")
public class ReceivingReportServiceImpl //
		extends AbstractReceivingReportService {

	@Override
	protected String getReferencePrompt(Billable b) {
		return isExTruck(b) ? "L/O" : "S/O";
	}

	private boolean isExTruck(Billable b) {
		return b.getCustomerName().startsWith(EX_TRUCK.toString());
	}

	@Override
	protected void confirmBookingIsStillReceivable(Long bookingId, Billable b) throws Exception {
		super.confirmBookingIsStillReceivable(bookingId, b);
		confirmBookingNotBeenInvoiced(bookingId, b);
	}

	private void confirmBookingNotBeenInvoiced(Long bookingId, Billable b) throws Exception {
		if (isExTruck(b) && hasBeenBilled(b))
			throw new AlreadyBilledBookingException(bookingId, b.getOrderNo());
	}

	private boolean hasBeenBilled(Billable b) {
		return b.getBilledOn() != null;
	}
}
