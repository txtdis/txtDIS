package ph.txtdis.service;

import org.springframework.stereotype.Service;

import ph.txtdis.util.MainHttpHeader;

@Service("readOnlyService")
public class ReadOnlyServiceImpl<T>
		extends AbstractReadOnlyService<T, MainHttpHeader, MainRestService, MainRestServerService>
		implements MainReadOnlyService<T> {
}
