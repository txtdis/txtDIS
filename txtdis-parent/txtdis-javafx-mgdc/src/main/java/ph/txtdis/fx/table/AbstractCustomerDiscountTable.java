package ph.txtdis.fx.table;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.dto.CustomerDiscount;
import ph.txtdis.fx.dialog.CustomerDiscountDialog;

public abstract class AbstractCustomerDiscountTable extends AbstractTableView<CustomerDiscount>
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
