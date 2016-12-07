package ph.txtdis.service;

import java.math.BigDecimal;

import ph.txtdis.type.UomType;

public interface QtyPerUomService {

	BigDecimal getQtyPerUom(UomType uom);
}
