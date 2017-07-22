package ph.txtdis.dyvek.service;

import org.springframework.stereotype.Service;

@Service("tradingClientService")
public class TradingClientServiceImpl //
		extends AbstractCustomerService //
		implements TradingClientService {

	@Override
	public String getHeaderName() {
		return "Trading Client";
	}
}
