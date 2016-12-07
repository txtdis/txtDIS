package ph.txtdis.service;

import org.springframework.stereotype.Service;

import ph.txtdis.util.MainHttpHeader;

@Service("savingService")
public class SavingServiceImpl<T>
		extends AbstractSavingService<T, MainHttpHeader, MainRestService, MainRestServerService> {
}
