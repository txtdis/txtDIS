package ph.txtdis.dyvek.service;

import static org.apache.commons.lang3.StringUtils.capitalize;

import java.util.List;

import org.springframework.stereotype.Service;

import ph.txtdis.dyvek.model.Customer;

@Service("vendorService")
public class VendorServiceImpl //
		extends AbstractCustomerService //
		implements VendorService {

	private static final String VENDOR = "vendor";

	private static final String VENDORS = VENDOR + "s";

	@Override
	public String getHeaderName() {
		return capitalize(VENDOR);
	}

	@Override
	public List<Customer> list() {
		return list(VENDORS);
	}

	@Override
	public List<String> listVendors() {
		return listNames(VENDORS);
	}
}
