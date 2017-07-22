package ph.txtdis.mgdc.gsm.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.mgdc.service.SellerFilteredAgingReceivableService;

@Scope("prototype")
@Component("agingReceivableApp")
public class AgingReceivableAppImpl //
		extends AbstractAgingReceivableApp<SellerFilteredAgingReceivableService> {
}
