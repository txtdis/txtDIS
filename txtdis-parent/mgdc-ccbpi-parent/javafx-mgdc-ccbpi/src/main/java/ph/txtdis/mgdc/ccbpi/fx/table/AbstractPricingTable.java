package ph.txtdis.mgdc.ccbpi.fx.table;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.CURRENCY;
import static ph.txtdis.type.Type.ENUM;
import static ph.txtdis.type.Type.OTHERS;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.scene.control.TableColumn;
import ph.txtdis.dto.Price;
import ph.txtdis.dto.PricingType;
import ph.txtdis.fx.table.AbstractTable;
import ph.txtdis.fx.table.Column;
import ph.txtdis.fx.table.DecisionNeededTableControls;
import ph.txtdis.mgdc.ccbpi.dto.Channel;
import ph.txtdis.mgdc.ccbpi.fx.dialog.PricingDialog;

public abstract class AbstractPricingTable //
	extends AbstractTable<Price> //
	implements PricingTable {

	@Autowired
	protected Column<Price, BigDecimal> price;

	@Autowired
	protected Column<Price, Channel> channelLimit;

	@Autowired
	protected Column<Price, PricingType> type;

	@Autowired
	protected PricingDialog dialog;

	@Autowired
	protected DecisionNeededTableControls<Price> decisionNeeded;

	@Override
	protected List<TableColumn<Price, ?>> addColumns() {
		buildColumns();
		return columns();
	}

	protected void buildColumns() {
		type.ofType(ENUM).width(120).build("Type", "type");
		price.ofType(CURRENCY).build("Price", "priceValue");
		channelLimit.ofType(OTHERS).width(180).build("Limited\nto", "channelLimit");
	}

	protected List<TableColumn<Price, ?>> columns() {
		List<TableColumn<Price, ?>> l = new ArrayList<>(asList(type, price, channelLimit));
		l.addAll(decisionNeeded.addColumns());
		return l;
	}

	@Override
	protected void addProperties() {
		decisionNeeded.addContextMenu(this, dialog);
	}
}
