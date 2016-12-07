package ph.txtdis.service;

import java.util.List;

import ph.txtdis.domain.ItemEntity;
import ph.txtdis.domain.QtyPerUomEntity;
import ph.txtdis.dto.Item;
import ph.txtdis.dto.QtyPerUom;

public interface QtyPerUomService {

	List<QtyPerUom> toList(List<QtyPerUomEntity> l);

	List<QtyPerUomEntity> toEntities(ItemEntity e, Item i);
}