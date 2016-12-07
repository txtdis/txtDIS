package ph.txtdis.fx.table;

import static ph.txtdis.type.Type.FRACTION;

import org.apache.commons.lang3.math.Fraction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.StockTakeDetail;

@Lazy
@Component("stockTakeTable")
public class StockTakeTableImpl extends AbstractStockTakeTable implements StockTakeTable {

	@Autowired
	private Column<StockTakeDetail, Fraction> quantity;

	@Override
	protected Column<StockTakeDetail, ?> quantityColumn() {
		return quantity.ofType(FRACTION).build("Quantity", "qtyInFractions");
	}
}
