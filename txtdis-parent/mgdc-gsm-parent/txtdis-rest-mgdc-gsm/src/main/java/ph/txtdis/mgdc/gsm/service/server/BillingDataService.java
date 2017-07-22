package ph.txtdis.mgdc.gsm.service.server;

import java.time.ZonedDateTime;

import ph.txtdis.dto.Billable;
import ph.txtdis.dto.Booked;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.util.NumberUtils;

public interface BillingDataService {

	default BillableEntity setOrderNoAndBillingData(BillableEntity e, Billable b) {
		e = setThreePartOrderNo(e, b);
		return b.getBilledBy() == null || e.getBilledBy() != null ? e : setBillingData(e, b);
	}

	default BillableEntity setThreePartOrderNo(BillableEntity e, Billable b) {
		e.setNumId(numId(b));
		e.setPrefix(b.getPrefix());
		e.setSuffix(b.getSuffix());
		return e;
	}

	default BillableEntity setBillingData(BillableEntity e, Billable b) {
		e.setBilledBy(b.getBilledBy());
		e.setBilledOn(ZonedDateTime.now());
		e.setFullyPaid(isFullyPaid(b));
		return e;
	}

	default boolean isFullyPaid(Billable b) {
		return !NumberUtils.isPositive(b.getUnpaidValue());
	}

	default Long numId(Billable b) {
		return b.getNumId();
	}

	default Long negateBookingId(Booked e) {
		Long id = e.getBookingId();
		return id == null || id < 0 ? id : -id;
	}
}
