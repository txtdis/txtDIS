package ph.txtdis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ph.txtdis.dto.Remittance;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;

@Service("remittanceService")
public class RemittanceServiceImpl extends AbstractRemittanceService implements ImportedRemittanceService {

	@Autowired
	private ReadOnlyService<Remittance> readOnlyService;

	@Override
	@Transactional
	public void importAll() throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException,
			RestException, InvalidException {
		post(readOnlyService.module("remittance").getList());
	}
}
