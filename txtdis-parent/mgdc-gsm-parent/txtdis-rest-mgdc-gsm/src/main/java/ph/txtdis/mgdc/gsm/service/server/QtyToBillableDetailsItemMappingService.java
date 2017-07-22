package ph.txtdis.mgdc.gsm.service.server;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import ph.txtdis.dto.Billable;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.dto.Bom;
import ph.txtdis.mgdc.gsm.domain.BillableDetailEntity;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;

public interface QtyToBillableDetailsItemMappingService {

	default BillableEntity updateDetailQty(BillableEntity e, Billable b) {
		Map<Long, BillableDetailEntity> m = mapEntityDetailsItemVsDetailsQty(e, b);
		e.setDetails(getEntityDetails(m));
		return e;
	}

	default Map<Long, BillableDetailEntity> mapEntityDetailsItemVsDetailsQty(BillableEntity e, Billable b) {
		Map<Long, BillableDetailEntity> m = e.getDetails().stream().collect(toMap(d -> d.getItem().getId(), identity()));
		for (BillableDetail d : getBomExpandedDetails(b)) {
			if (d != null && d.getId() != null) {
				d.setInvalid(b.getIsValid() != null && b.getIsValid() == false);
				m = mapTheSumOfTheEntityDetailAndDetailItemQuantities(m, d);
			}
		}
		return m;
	}

	default List<BillableDetail> getBomExpandedDetails(Billable b) {
		List<BillableDetail> l = b.getDetails();
		l = l.stream().filter(d -> d != null).flatMap(d -> expandAllDetails(b, d).stream()).collect(toList());
		return l;
	}

	default List<BillableDetail> expandAllDetails(Billable b, BillableDetail d) {
		List<Bom> l = extractAll(d.getId(), d.getItemName(), BigDecimal.ONE);
		return toDetails(b, d, l);
	}

	List<Bom> extractAll(Long itemId, String itemName, BigDecimal qty);

	default List<BillableDetail> toDetails(Billable b, BillableDetail d, List<Bom> l) {
		return l.stream().map(bom -> toDetail(b, d, bom)).collect(toList());
	}

	default BillableDetail toDetail(Billable b, BillableDetail bd, Bom bom) {
		BillableDetail d = new BillableDetail();
		d.setId(bom.getId());
		d.setItemName(bom.getPart());
		d.setInitialQty(bd.getInitialQty().multiply(bom.getQty()));
		d.setReturnedQty(bd.getReturnedQty().multiply(bom.getQty()));
		return d;
	}

	default List<BillableDetailEntity> getEntityDetails(Map<Long, BillableDetailEntity> m) {
		return m.entrySet().stream().map(s -> s.getValue()).collect(toList());
	}

	default Map<Long, BillableDetailEntity> mapTheSumOfTheEntityDetailAndDetailItemQuantities(Map<Long, BillableDetailEntity> m, BillableDetail b) {
		BillableDetailEntity e = m.get(b.getId());
		if (e != null)
			m.put(b.getId(), setTheTotalOfTheMappedEntityAndModelDetailsItemQuantities(e, b));
		return m;
	}

	BillableDetailEntity setTheTotalOfTheMappedEntityAndModelDetailsItemQuantities(BillableDetailEntity e, BillableDetail b);
}
