package ph.txtdis.mgdc.fx.table;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.mgdc.type.SalesVolumeReportType;

import static ph.txtdis.mgdc.type.SalesVolumeReportType.ITEM;

@Scope("prototype")
@Component("salesVolumeTable")
public class SalesVolumeTable //
	extends AbstractSalesVolumeTable {

	@Autowired
	private SalesVolumeContextMenu menu;

	@Override
	protected void addProperties() {
		setId(ITEM.name());
		setTableColumnVisibility(ITEM);
		menu.addMenu(this);
	}

	public void setTableColumnVisibility(SalesVolumeReportType t) {
		hideGroupColumns();
		switch (t) {
			case CATEGORY:
				category.setVisible(true);
				break;
			case PRODUCT_LINE:
				prodLine.setVisible(true);
				break;
			case ITEM:
			default:
				item.setVisible(true);
		}
		setMinWidth(width());
		refresh();
	}

	private void hideGroupColumns() {
		billingNo.setVisible(false);
		orderDate.setVisible(false);
		seller.setVisible(false);
		channel.setVisible(false);
		customer.setVisible(false);
		street.setVisible(false);
		barangay.setVisible(false);
		city.setVisible(false);
		category.setVisible(false);
		customerStartDate.setVisible(false);
		prodLine.setVisible(false);
		item.setVisible(false);
	}
}
