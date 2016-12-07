package ph.txtdis.service;

import static org.apache.log4j.Logger.getLogger;

import java.time.LocalDate;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ph.txtdis.domain.CustomerEntity;
import ph.txtdis.dto.Customer;
import ph.txtdis.dto.PartnerType;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;
import ph.txtdis.util.DateTimeUtils;

@Service("customerService")
public class CustomerServiceImpl extends AbstractCustomerService implements ImportedCustomerService {

	private static Logger logger = getLogger(CustomerServiceImpl.class);

	private static final String CUSTOMER = "customer";

	@Autowired
	private ImportedLocationService locationService;

	@Autowired
	private ReadOnlyService<Customer> readOnlyService;

	@Autowired
	private SavingService<Customer> savingService;

	@Value("${vendor.name}")
	private String vendorName;

	@Value("${vendor.barangay}")
	private String vendorBarangay;

	@Value("${vendor.city}")
	private String vendorCity;

	@Value("${vendor.province}")
	private String vendorProvince;

	@Value("#{'${branch.names}'.split(',')}")
	private List<String> branchNames;

	@Value("#{'${branch.barangays}'.split(',')}")
	private List<String> branchBarangays;

	@Value("#{'${branch.cities}'.split(',')}")
	private List<String> branchCities;

	@Value("#{'${branch.provinces}'.split(',')}")
	private List<String> branchProvinces;

	@Value("#{'${bank.names}'.split(',')}")
	private List<String> bankNames;

	@Value("${vendor.dis.go.live}")
	private String edmsGoLive;

	@Override
	public LocalDate goLiveDate() {
		return DateTimeUtils.toDate(edmsGoLive);
	}

	@Override
	@Transactional
	public void importAll() throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException,
			RestException, InvalidException {
		createVendor();
		createBranches();
		importExTrucks();
		createBanks();
		importOutlets();
	}

	private void createVendor() {
		CustomerEntity c = createCustomer(vendorName, PartnerType.VENDOR);
		c.setBarangay(locationService.getByName(vendorBarangay));
		c.setCity(locationService.getByName(vendorCity));
		c.setProvince(locationService.getByName(vendorProvince));
		repository.save(c);
		logger.info("\n    Vendor: " + c.getName());
	}

	private CustomerEntity createCustomer(String name, PartnerType t) {
		CustomerEntity c = new CustomerEntity();
		c.setName(name);
		c.setType(t);
		return c;
	}

	private void createBranches() {
		for (int i = 0; i < branchNames.size(); i++)
			createBranch(i);
	}

	private void createBranch(int i) {
		logger.info("\n    Branch: " + branchNames.get(i));
		CustomerEntity c = createCustomer(branchNames.get(i), PartnerType.INTERNAL);
		c.setBarangay(locationService.getByName(branchBarangays.get(i)));
		c.setCity(locationService.getByName(branchCities.get(i)));
		c.setProvince(locationService.getByName(branchProvinces.get(i)));
		repository.save(c);
	}

	private void importExTrucks() throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		importCustomers("/exTrucks");
	}

	private void importCustomers(String endPt) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		List<Customer> l = readOnlyService.module(CUSTOMER).getList(endPt);
		l.forEach(c -> repository.save(toEntity(c)));
	}

	private void createBanks() {
		bankNames.forEach(b -> createBank(b));
	}

	private void createBank(String name) {
		logger.info("\n    Bank: " + name);
		CustomerEntity c = createCustomer(name, PartnerType.FINANCIAL);
		repository.save(c);
	}

	private void importOutlets() throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		importCustomers("");
	}

	@Override
	@Transactional
	public Customer save(Customer t) {
		try {
			logger.info("\n    CustomerToSave: " + t);
			t = super.save(t);
			if (t.getParent() != null)
				return t;
			return savingService.module(CUSTOMER).save(t);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}