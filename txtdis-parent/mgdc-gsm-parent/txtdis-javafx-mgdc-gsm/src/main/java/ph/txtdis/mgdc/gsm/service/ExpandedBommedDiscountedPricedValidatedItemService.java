package ph.txtdis.mgdc.gsm.service;

import ph.txtdis.dto.BillableDetail;

import java.math.BigDecimal;

public interface ExpandedBommedDiscountedPricedValidatedItemService
	extends BommedDiscountedPricedValidatedItemService {

	BigDecimal getBomExpandedQtyInCases(BillableDetail d);
}
