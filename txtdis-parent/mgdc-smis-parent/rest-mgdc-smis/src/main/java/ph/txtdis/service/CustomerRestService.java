package ph.txtdis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.Customer;
import ph.txtdis.repository.CustomerRepository;

@Service("customerRestService")
public class CustomerRestService {

	@Autowired
	private CustomerRepository repository;

	@Value("${vendor.id}")
	private String vendorId;

	private Customer vendor;

	public void deactivateNonBuyingOutlets() {
	}

	public Customer toBank(String id) {
		return repository.findOne(Long.valueOf(id));
	}

	public Customer getVendor() {
		if (vendor == null)
			vendor = repository.findOne(Long.valueOf(vendorId));
		return vendor;
	}
}
