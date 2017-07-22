package ph.txtdis.mgdc.fx.table;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.PricingType;
import ph.txtdis.fx.table.AbstractNameListTable;
import ph.txtdis.mgdc.fx.dialog.PricingTypeDialog;

@Scope("prototype")
@Component("pricingTypeTable")
public class PricingTypeTable extends AbstractNameListTable<PricingType, PricingTypeDialog> {
}
