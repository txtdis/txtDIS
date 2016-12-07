package ph.txtdis.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import ph.txtdis.domain.CustomerEntity;
import ph.txtdis.dto.Customer;

@Service("customerService")
public class CustomerServiceImpl extends AbstractCustomerService {

	@Override
	public LocalDate goLiveDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Customer toDTO(CustomerEntity e) {
		if (e == null)
			return null;
		Customer c = super.toDTO(e);
		c.setUploadedBy(e.getUploadedBy());
		c.setUploadedOn(e.getUploadedOn());
		return c;
	}

	@Override
	public CustomerEntity toEntity(Customer t) {
		if (t == null)
			return null;
		CustomerEntity c = super.toEntity(t);
		c.setUploadedBy(t.getUploadedBy());
		c.setUploadedOn(t.getUploadedOn());
		return c;
	}
}
