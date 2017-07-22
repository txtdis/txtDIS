package ph.txtdis.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import ph.txtdis.util.HttpHeader;

@Scope("prototype")
@Service("readOnlyService")
public class ReadOnlyServiceImpl<T> //
		extends AbstractReadOnlyService<T, HttpHeader, RestService, RestServerService> {
}
