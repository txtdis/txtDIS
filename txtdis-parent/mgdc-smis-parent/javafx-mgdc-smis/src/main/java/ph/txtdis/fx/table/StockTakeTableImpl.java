package ph.txtdis.fx.table;

import static ph.txtdis.type.Type.QUANTITY;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.StockTakeDetail;

@Lazy
@Component("stockTakeTable")
public class StockTakeTableImpl extends AbstractStockTakeTable implements StockTakeTable {

	@Autowired
	private Column<StockTakeDetail, BigDecimal> quantity;

	@Override
	protected Column<StockTakeDetail, ?> quantityColumn() {
		return quantity.ofType(QUANTITY).build("Quantity", "qty");
	}
}
