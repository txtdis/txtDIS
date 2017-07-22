package ph.txtdis.mgdc.gsm.service.server;

import static java.math.BigDecimal.ZERO;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.QtyPerUom;
import ph.txtdis.mgdc.gsm.domain.ItemEntity;
import ph.txtdis.mgdc.gsm.domain.QtyPerUomEntity;
import ph.txtdis.mgdc.gsm.dto.Item;
import ph.txtdis.mgdc.gsm.repository.QtyPerUomRepository;
import ph.txtdis.type.UomType;

@Service("qtyPerUomService")
public class QtyPerUomServiceImpl //
		implements QtyPerUomService {

	@Autowired
	private QtyPerUomRepository repository;

	@Override
	public BigDecimal getItemQtyPerUom(ItemEntity e, UomType u) {
		QtyPerUomEntity q = repository.findByItemAndUom(e, u);
		return q == null ? ZERO : q.getQty();
	}

	@Override
	public List<QtyPerUom> toList(List<QtyPerUomEntity> l) {
		return l == null ? null : l.stream().map(e -> toDTO(e)).collect(Collectors.toList());
	}

	@Override
	public List<QtyPerUomEntity> toEntities(ItemEntity e, Item i) {
		try {
			return i.getQtyPerUomList().stream().map(q -> toEntity(e, q)).collect(Collectors.toList());
		} catch (Exception x) {
			return null;
		}
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