package ph.txtdis.mgdc.gsm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ph.txtdis.mgdc.gsm.dto.Customer;
import ph.txtdis.service.AbstractCustomerReceivableService;

@Service("customerReceivableService")
public class CustomerReceivableServiceImpl //
	extends AbstractCustomerReceivableService {

	@Autowired
	private CustomerService customerService;

	@Override
	public void listInvoicesByCustomerBetweenTwoDayCounts(String... ids) throws Exception {
		super.listInvoicesByCustomerBetweenTwoDayCounts(ids);
		customerName = ((Customer) customerService.findByEndPt("/" + ids[CUSTOMER_ID])).getName();
	}
}
