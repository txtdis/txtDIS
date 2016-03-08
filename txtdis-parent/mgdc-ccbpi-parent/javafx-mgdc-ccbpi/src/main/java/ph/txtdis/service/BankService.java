package ph.txtdis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Bank;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;

@Service("bankService")
public class BankService implements Iconed, Listed<Bank>, UniquelyNamed<Bank> {

	@Autowired
	private ReadOnlyService<Bank> readOnlyService;

	@Autowired
	private SavingService<Bank> savingService;

	@Override
	public String getModule() {
		return "bank";
	}

	@Override
	public ReadOnlyService<Bank> getReadOnlyService() {
		return readOnlyService;
	}

	public Bank save(String name) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, InvalidException, RestException {
		Bank b = new Bank();
		b.setName(name);
		return savingService.module(getModule()).save(b);
	}
}
