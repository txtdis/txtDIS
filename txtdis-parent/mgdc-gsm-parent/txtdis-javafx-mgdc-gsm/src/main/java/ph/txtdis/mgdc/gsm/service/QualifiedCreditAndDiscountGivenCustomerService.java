package ph.txtdis.mgdc.gsm.service;

import ph.txtdis.mgdc.service.RouteAssignedCustomerService;

public interface QualifiedCreditAndDiscountGivenCustomerService //
	extends CreditGivenCustomerService,
	RouteAssignedCustomerService,
	ValueBasedCustomerDiscountService {

	boolean cannotGiveCreditAndOrDiscount();

	boolean hasCreditOrDiscountBeenGiven();
}
