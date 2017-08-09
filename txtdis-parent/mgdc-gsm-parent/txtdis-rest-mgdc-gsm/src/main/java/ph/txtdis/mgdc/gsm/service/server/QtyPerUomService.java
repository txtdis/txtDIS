package ph.txtdis.mgdc.gsm.service.server;

import ph.txtdis.dto.QtyPerUom;
import ph.txtdis.mgdc.gsm.domain.ItemEntity;
import ph.txtdis.mgdc.gsm.domain.QtyPerUomEntity;
import ph.txtdis.mgdc.gsm.dto.Item;
import ph.txtdis.type.UomType;

import java.math.BigDecimal;
import java.util.List;

public interface QtyPerUomService {

	BigDecimal getItemQtyPerUom(ItemEntity e, UomType uom);

	List<QtyPerUomEntity> toEntities(ItemEntity e, Item i);

	List<QtyPerUom> toList(List<QtyPerUomEntity> l);
}