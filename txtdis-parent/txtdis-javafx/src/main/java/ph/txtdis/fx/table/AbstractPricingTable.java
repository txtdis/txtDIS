package ph.txtdis.fx.table;

import static ph.txtdis.type.Type.CURRENCY;
import static ph.txtdis.type.Type.ENUM;
import static ph.txtdis.type.Type.OTHERS;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.dto.Channel;
import ph.txtdis.dto.Price;
import ph.txtdis.dto.PricingType;
import ph.txtdis.fx.dialog.PricingDialog;

public abstract class AbstractPricingTable extends AbstractTableView<Price> implements PricingTable {

	@Autowired
	private Column<Price, PricingType> type;

	@Autowired
	private Column<Price, BigDecimal> price;

	@Autowired
	private PricingDialog dialog;

	@Autowired
	protected Column<Price, Channel> channelLimit;

	@Autowired
	protected DecisionNeededTableControls<Price> decisionNeeded;

	@Override
	@SuppressWarnings("unchecked")
	protected void addColumns() {
		getColumns().setAll(//
				type.ofType(ENUM).width(120).build("Type", "type"), //
				price.ofType(CURRENCY).build("Price", "priceValue"),
				channelLimit.ofType(OTHERS).width(180).build("Limited\nto", "channelLimit"));
		getColumns().addAll(decisionNeeded.addColumns());
	}

	@Override
	protected void addProperties() {
		decisionNeeded.addContextMenu(this, dialog);
	}
}
