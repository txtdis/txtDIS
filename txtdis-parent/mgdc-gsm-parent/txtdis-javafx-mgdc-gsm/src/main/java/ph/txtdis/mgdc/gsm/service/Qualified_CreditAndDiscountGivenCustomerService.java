package ph.txtdis.mgdc.gsm.service;

import ph.txtdis.mgdc.service.RouteAssignedCustomerService;

public interface Qualified_CreditAndDiscountGivenCustomerService //
		extends CreditGivenCustomerService, RouteAssignedCustomerService, ValueBasedCustomerDiscountService {

	boolean cannotGiveCreditAndOrDiscount();

	boolean hasCreditOrDiscountBeenGiven();
}
