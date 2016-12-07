package ph.txtdis.fx.table;

import static ph.txtdis.type.SalesVolumeReportType.ITEM;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import ph.txtdis.type.SalesVolumeReportType;

@Lazy
@Component("salesVolumeTable")
public class SalesVolumeTable extends AbstractSalesVolumeTable {

	@Autowired
	private SalesVolumeContextMenu menu;

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
		orderDate.setVisible(false);
		seller.setVisible(false);
		channel.setVisible(false);
		customer.setVisible(false);
		category.setVisible(false);
		prodLine.setVisible(false);
		item.setVisible(false);
	}

	@Override
	protected void addProperties() {
		setId(ITEM.name());
		setTableColumnVisibility(ITEM);
		menu.setMenu();
	}
}
