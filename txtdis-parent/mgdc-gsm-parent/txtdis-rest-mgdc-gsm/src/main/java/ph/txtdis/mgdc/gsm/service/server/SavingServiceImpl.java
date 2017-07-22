package ph.txtdis.mgdc.gsm.service.server;

import org.springframework.stereotype.Service;

import ph.txtdis.dto.Keyed;
import ph.txtdis.mgdc.gsm.util.MainHttpHeader;
import ph.txtdis.service.AbstractSavingService;

@Service("savingService")
public class SavingServiceImpl<T extends Keyed<?>> //
		extends AbstractSavingService<T, MainHttpHeader, MainRestService, MainRestServerService> {
}
