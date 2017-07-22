package ph.txtdis.mgdc.gsm.service.server;

import org.springframework.stereotype.Service;

import ph.txtdis.mgdc.gsm.util.MainHttpHeader;
import ph.txtdis.service.AbstractReadOnlyService;

@Service("readOnlyService")
public class ReadOnlyServiceImpl<T>
		extends AbstractReadOnlyService<T, MainHttpHeader, MainRestService, MainRestServerService>
		implements MainReadOnlyService<T> {
}
