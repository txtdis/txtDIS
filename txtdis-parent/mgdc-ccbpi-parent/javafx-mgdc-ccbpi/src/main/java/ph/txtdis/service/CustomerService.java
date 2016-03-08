package ph.txtdis.service;

import static org.apache.log4j.Logger.getLogger;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Customer;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;

@Service("customerService")
public class CustomerService implements Iconed, Listed<Customer>, Unique<Customer>, UniquelyNamed<Customer> {

	private static Logger logger = getLogger(BillingService.class);

	@Autowired
	private ReadOnlyService<Customer> readOnlyService;

	@Autowired
	private SavingService<Customer> savingService;

	@Override
	public String getModule() {
		return "customer";
	}

	@Override
	public ReadOnlyService<Customer> getReadOnlyService() {
		return readOnlyService;
	}

	public Customer save(Long id, String name) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, InvalidException, RestException {
		Customer c = new Customer();
		c.setCode(id);
		c.setName(name);
		logger.info("Customer before save = " + c);
		c = savingService.module(getModule()).save(c);
		logger.info("Customer after save = " + c);
		return c;
	}

	public List<Customer> search(String text) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, InvalidException, RestException {
		String endpoint = text.isEmpty() ? "" : "/search?name=" + text;
		return readOnlyService.module(getModule()).getList(endpoint);
	}
}
