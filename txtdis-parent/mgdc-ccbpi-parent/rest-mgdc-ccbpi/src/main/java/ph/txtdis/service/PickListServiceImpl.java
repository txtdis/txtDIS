package ph.txtdis.service;

import static org.apache.log4j.Logger.getLogger;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.BillableDetailEntity;
import ph.txtdis.domain.BillableEntity;
import ph.txtdis.domain.BomEntity;
import ph.txtdis.domain.PickListDetailEntity;
import ph.txtdis.domain.PickListEntity;
import ph.txtdis.dto.Booking;
import ph.txtdis.dto.PickList;
import ph.txtdis.util.DateTimeUtils;

@Service("pickListService")
public class PickListServiceImpl extends AbstractPickListService {

	private static Logger logger = getLogger(PickListServiceImpl.class);

	@Override
	protected PickListEntity post(PickList p) {
		PickListEntity e = toEntity(p);
		e.setDetails(toDetails(summaryOfQuantitiesPerItem(e)));
		return repository.save(e);
	}

	private List<PickListDetailEntity> toDetails(List<BomEntity> boms) {
		return boms.stream().map(b -> toDetail(b)).collect(Collectors.toList());
	}

	private PickListDetailEntity toDetail(BomEntity b) {
		PickListDetailEntity d = new PickListDetailEntity();
		d.setItem(b.getPart());
		d.setPickedQty(b.getQty().intValue());
		return d;
	}

	@Override
	protected BomEntity toBom(BillableDetailEntity d) {
		logger.info("\n    BillableDetailEntityBeforeBomCreation = " + d);
		return createBom(d.getItem(), d.getFinalQty());
	}

	@Override
	protected Booking toBooking(BillableEntity e) {
		Booking b = super.toBooking(e);
		b.setLocation(e.getCustomer().getVendorId() // 
				+ "-" + DateTimeUtils.toOrderConfirmationDate(e.getOrderDate()) //
				+ "/" + e.getNumId());
		return b;
	}
}