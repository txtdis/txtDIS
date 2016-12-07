package ph.txtdis.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import ph.txtdis.domain.ItemEntity;
import ph.txtdis.domain.QtyPerUomEntity;
import ph.txtdis.dto.Item;
import ph.txtdis.dto.QtyPerUom;

@Service("qtyPerUomService")
public class QtyPerUomServiceImpl implements QtyPerUomService {

	@Override
	public List<QtyPerUom> toList(List<QtyPerUomEntity> l) {
		return l == null ? null : l.stream().map(e -> toDTO(e)).collect(Collectors.toList());
	}

	@Override
	public List<QtyPerUomEntity> toEntities(ItemEntity e, Item i) {
		return e == null || i == null ? null
				: i.getQtyPerUomList().stream().map(q -> toEntity(e, q)).collect(Collectors.toList());
	}

	private QtyPerUom toDTO(QtyPerUomEntity e) {
		QtyPerUom q = new QtyPerUom();
		q.setId(e.getId());
		q.setUom(e.getUom());
		q.setQty(e.getQty());
		q.setPurchased(e.getPurchased());
		q.setSold(e.getSold());
		q.setReported(e.getReported());
		q.setCreatedBy(e.getCreatedBy());
		q.setCreatedOn(e.getCreatedOn());
		return q;
	}

	private QtyPerUomEntity toEntity(ItemEntity i, QtyPerUom t) {
		QtyPerUomEntity e = new QtyPerUomEntity();
		e.setItem(i);
		e.setUom(t.getUom());
		e.setQty(t.getQty());
		e.setPurchased(t.getPurchased());
		e.setSold(t.getSold());
		e.setReported(t.getReported());
		return e;
	}
}