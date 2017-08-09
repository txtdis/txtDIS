package ph.txtdis.mgdc.gsm.service.server;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ph.txtdis.mgdc.gsm.util.MainHttpHeader;
import ph.txtdis.service.AbstractRestClientService;

@Primary
@Service("primaryRestClientService")
public class PrimaryRestClientServiceImpl<T>
	extends AbstractRestClientService<T, MainHttpHeader, PrimaryRestService, PrimaryRestServerService>
	implements PrimaryRestClientService<T> {
}
