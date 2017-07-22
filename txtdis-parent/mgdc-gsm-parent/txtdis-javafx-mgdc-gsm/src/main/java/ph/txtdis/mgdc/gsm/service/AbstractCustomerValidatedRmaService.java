package ph.txtdis.mgdc.gsm.service;

import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.mgdc.gsm.dto.Customer;

public abstract class AbstractCustomerValidatedRmaService //
		extends AbstractRefundedRmaService {

	@Autowired
	private CustomerValidationService customerValidationService;

	@Override
	public void updateUponCustomerIdValidation(Long id) throws Exception {
		Customer c = customerValidationService.validate(id, getOrderDate());
		setCustomerData(c);
	}
}
