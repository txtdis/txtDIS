package ph.txtdis.mgdc.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.app.AbstractTableApp;
import ph.txtdis.dto.PricingType;
import ph.txtdis.mgdc.fx.table.PricingTypeTable;
import ph.txtdis.mgdc.service.PricingTypeService;

@Scope("prototype")
@Component("pricingTypeApp")
public class PricingTypeApp
	extends AbstractTableApp<PricingTypeTable, PricingTypeService, PricingType> {
}
