package ph.txtdis.service;

import static org.apache.log4j.Logger.getLogger;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ph.txtdis.dto.Billable;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;

@Service("billingService")
public class BillingServiceImpl extends AbstractBillableService implements ImportedBillingService {

	private static Logger logger = getLogger(BillingServiceImpl.class);

	private static final String BILLABLE = "billable";

	@Autowired
	private ReadOnlyService<Billable> readOnlyService;

	@Autowired
	private SavingService<Billable> savingService;

	@Override
	public void importAll() throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException,
			RestException, InvalidException {
		post(readOnlyService.module(BILLABLE).getList());
	}

	@Override
	@Transactional
	public Billable save(Billable t) {
		try {
			logger.info("\n    BillableToSave: " + t);
			t = super.save(t);
			return savingService.module(BILLABLE).save(t);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}