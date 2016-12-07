package ph.txtdis.service;

import java.math.BigDecimal;

import ph.txtdis.type.UomType;

public interface QuantityValidated {

	void setQtyUponValidation(UomType uom, BigDecimal qty) throws Exception;
}
