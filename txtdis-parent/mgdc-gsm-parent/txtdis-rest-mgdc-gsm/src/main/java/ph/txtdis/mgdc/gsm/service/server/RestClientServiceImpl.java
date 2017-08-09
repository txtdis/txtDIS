package ph.txtdis.mgdc.gsm.service.server;

import org.springframework.stereotype.Service;
import ph.txtdis.dto.Keyed;
import ph.txtdis.mgdc.gsm.util.MainHttpHeader;
import ph.txtdis.service.AbstractRestClientService;

@Service("restClientService")
public class RestClientServiceImpl<T extends Keyed<?>> //
	extends AbstractRestClientService<T, MainHttpHeader, PrimaryRestService, PrimaryRestServerService> {
}
