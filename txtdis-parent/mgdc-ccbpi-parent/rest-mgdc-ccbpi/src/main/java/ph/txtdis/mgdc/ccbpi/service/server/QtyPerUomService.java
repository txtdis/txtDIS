package ph.txtdis.mgdc.ccbpi.service.server;

import java.math.BigDecimal;
import java.util.List;

import ph.txtdis.dto.QtyPerUom;
import ph.txtdis.mgdc.ccbpi.domain.ItemEntity;
import ph.txtdis.mgdc.ccbpi.domain.QtyPerUomEntity;
import ph.txtdis.mgdc.ccbpi.dto.Item;
import ph.txtdis.type.UomType;

public interface QtyPerUomService {

	BigDecimal getItemQtyPerUom(ItemEntity e, UomType uom);

	List<QtyPerUomEntity> toEntities(ItemEntity e, Item i);

	List<QtyPerUom> toList(List<QtyPerUomEntity> l);
}