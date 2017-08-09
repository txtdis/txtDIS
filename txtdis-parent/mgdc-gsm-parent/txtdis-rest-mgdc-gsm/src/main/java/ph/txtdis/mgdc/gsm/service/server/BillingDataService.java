package ph.txtdis.mgdc.gsm.service.server;

import ph.txtdis.dto.Billable;
import ph.txtdis.dto.Booked;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.util.NumberUtils;

import java.time.ZonedDateTime;

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

	default Long numId(Billable b) {
		return b.getNumId();
	}

	default boolean isFullyPaid(Billable b) {
		return !NumberUtils.isPositive(b.getUnpaidValue());
	}

	default Long negateBookingId(Booked e) {
		Long id = e.getBookingId();
		return id == null || id < 0 ? id : -id;
	}
}
