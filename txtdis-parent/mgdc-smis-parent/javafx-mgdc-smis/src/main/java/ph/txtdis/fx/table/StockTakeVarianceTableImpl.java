package ph.txtdis.fx.table;

import static ph.txtdis.type.Type.QUANTITY;
import static ph.txtdis.type.Type.TEXT;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.StockTakeVariance;
import ph.txtdis.service.VarianceService;
import ph.txtdis.type.Type;

@Lazy
@Component("stockTakeVarianceTable")
public class StockTakeVarianceTableImpl extends AbstractTableView<StockTakeVariance> implements StockTakeVarianceTable {

	@Autowired
	private Column<StockTakeVariance, Long> id;

	@Autowired
	private Column<StockTakeVariance, String> item;

	@Autowired
	private Column<StockTakeVariance, BigDecimal> start, in, out, end, actual, variance;

	@Autowired
	private VarianceService<StockTakeVariance> service;

	@Override
	@SuppressWarnings("unchecked")
	protected void addColumns() {
		getColumns().setAll(//
				id.ofType(Type.ID).build("ID", "id"), //
				item.ofType(TEXT).build("Item", "item"), //
				start.ofType(QUANTITY).build("Beginning", "startQty"), //
				in.ofType(QUANTITY).build("Incoming", "inQty"), //
				out.ofType(QUANTITY).build("Outgoing", "outQty"), //
				end.ofType(QUANTITY).build(service.getExpectedHeader(), "endQty"), //
				actual.ofType(QUANTITY).build(service.getActualHeader(), "actualQty"), //
				variance.ofType(QUANTITY).build(service.getVarianceHeader(), "varianceQty") //
		);
	}
}
