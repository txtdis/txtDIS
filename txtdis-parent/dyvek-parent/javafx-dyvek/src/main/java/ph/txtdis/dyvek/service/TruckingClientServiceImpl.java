package ph.txtdis.dyvek.service;

import org.springframework.stereotype.Service;

@Service("truckingClientService")
public class TruckingClientServiceImpl //
	extends AbstractCustomerService //
	implements TruckingClientService {

	@Override
	public String getHeaderName() {
		return "Trucking Client";
	}
}
