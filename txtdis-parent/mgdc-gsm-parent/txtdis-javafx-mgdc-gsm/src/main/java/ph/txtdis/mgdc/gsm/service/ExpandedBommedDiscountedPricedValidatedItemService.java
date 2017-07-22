package ph.txtdis.mgdc.gsm.service;

import java.math.BigDecimal;

import ph.txtdis.dto.BillableDetail;

public interface ExpandedBommedDiscountedPricedValidatedItemService
		extends BommedDiscountedPricedValidatedItemService {

	BigDecimal getBomExpandedQtyInCases(BillableDetail d);
}
