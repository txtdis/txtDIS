package ph.txtdis.mgdc.fx.table;

import static ph.txtdis.type.Type.FRACTION;

import org.apache.commons.lang3.math.Fraction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.StockTakeDetail;
import ph.txtdis.fx.table.AbstractStockTakeTable;
import ph.txtdis.fx.table.Column;
import ph.txtdis.fx.table.StockTakeTable;

@Scope("prototype")
@Component("stockTakeTable")
public class StockTakeTableImpl extends AbstractStockTakeTable implements StockTakeTable {

	@Autowired
	private Column<StockTakeDetail, Fraction> quantity;

	@Override
	protected Column<StockTakeDetail, ?> quantityColumn() {
		return quantity.ofType(FRACTION).build("Quantity", "qtyInFractions");
	}
}
