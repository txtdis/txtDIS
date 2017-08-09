package ph.txtdis.mgdc.gsm.fx.table;

import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.fx.table.AbstractTable;
import ph.txtdis.fx.table.Column;
import ph.txtdis.fx.table.DecisionNeededTableControls;
import ph.txtdis.mgdc.gsm.dto.CustomerDiscount;
import ph.txtdis.mgdc.gsm.fx.dialog.CustomerDiscountDialog;

import java.math.BigDecimal;

public abstract class AbstractCustomerDiscountTable
	extends AbstractTable<CustomerDiscount>
	implements CustomerDiscountTable {

	@Autowired
	protected DecisionNeededTableControls<CustomerDiscount> decisionNeeded;

	@Autowired
	protected Column<CustomerDiscount, BigDecimal> discount;

	@Autowired
	private CustomerDiscountDialog dialog;

	@Override
	public AbstractCustomerDiscountTable build() {
		return (AbstractCustomerDiscountTable) super.build();
	}

	@Override
	protected void addProperties() {
		decisionNeeded.addContextMenu(this, dialog);
	}
}
