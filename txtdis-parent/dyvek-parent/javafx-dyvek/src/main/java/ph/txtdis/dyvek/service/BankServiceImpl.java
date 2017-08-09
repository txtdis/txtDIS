package ph.txtdis.dyvek.service;

import java.util.List;

import org.springframework.stereotype.Service;

@Service("bankService")
public class BankServiceImpl //
	extends AbstractCustomerService //
	implements BankService {

	@Override
	public String getHeaderName() {
		return "Bank";
	}

	@Override
	public List<String> listBanks() {
		return listClients();
	}
}
