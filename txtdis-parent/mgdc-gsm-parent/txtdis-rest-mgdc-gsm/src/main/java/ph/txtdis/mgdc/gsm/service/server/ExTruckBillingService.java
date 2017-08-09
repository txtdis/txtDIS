package ph.txtdis.mgdc.gsm.service.server;

import ph.txtdis.dto.Billable;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.mgdc.gsm.domain.BillableDetailEntity;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.mgdc.gsm.repository.BillableRepository;

import java.math.BigDecimal;

import static java.util.stream.Collectors.toList;
import static ph.txtdis.type.PartnerType.EX_TRUCK;

public interface ExTruckBillingService //
	extends BillingDataService,
	QtyToLoadOrderDetailItemMappingService {

	default BillableEntity updateLoadOrderQtyAndNegateBookingIdIfFromLoadOrderThenSetBillingData( //
	                                                                                              BillableRepository r,
	                                                                                              //
	                                                                                              BillableEntity e,
	                                                                                              //
	                                                                                              Billable b) {
		updateLoadOrderDetailQty(r, b);
		if (isFromLoadOrder(e))
			e.setBookingId(negateBookingId(e));
		return setOrderNoAndBillingData(e, b);
	}

	default boolean isFromLoadOrder(BillableEntity e) {
		try {
			return e.getCustomer().getRoute(e.getOrderDate()).getName().startsWith(EX_TRUCK.toString());
		} catch (Exception x) {
			return false;
		}
	}

	default BillableDetailEntity setTheTotalOfTheMappedEntityAndModelDetailsItemQuantities(BillableDetailEntity ed,
	                                                                                       BillableDetail bd) {
		ed.setSoldQty(getSoldQty(ed, bd));
		return ed;
	}

	default BigDecimal getSoldQty(BillableDetailEntity ed, BillableDetail bd) {
		if (bd.isInvalid())
			return ed.getSoldQty().subtract(bd.getInitialQty());
		return ed.getSoldQty().add(bd.getInitialQty());
	}

	default BillableEntity updateDetailQty(BillableEntity e, Billable b) {
		if (isInvalid(b))
			e = nullifyReceivingData(e);
		return QtyToLoadOrderDetailItemMappingService.super.updateDetailQty(e, b);
	}

	default boolean isInvalid(Billable b) {
		return b.getIsValid() != null && b.getIsValid() == false;
	}

	default BillableEntity nullifyReceivingData(BillableEntity e) {
		e.setReceivingId(null);
		e.setReceivedBy(null);
		e.setReceivedOn(null);
		e.setDetails(e.getDetails().stream().map(d -> nullifyReturnedQty(d)).collect(toList()));
		return e;
	}

	default BillableDetailEntity nullifyReturnedQty(BillableDetailEntity ed) {
		ed.setReturnedQty(null);
		return ed;
	}
}
