package ph.txtdis.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import ph.txtdis.util.HttpHeader;

@Scope("prototype")
@Service("restClientService")
public class RestClientServiceImpl<T> //
	extends AbstractRestClientService<T, HttpHeader, RestService, RestServerService> {
}
