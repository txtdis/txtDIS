package ph.txtdis.dyvek.service;

import org.springframework.stereotype.Service;
import ph.txtdis.dyvek.model.Aging;
import ph.txtdis.service.RestClientService;

@Service("agingPayableService")
public class AgingPayableServiceImpl //
	implements AgingPayableService {

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public RestClientService<Aging> getRestClientServiceForLists() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getModuleName() {
		// TODO Auto-generated method stub
		return null;
	}
}
