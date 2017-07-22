package ph.txtdis.mgdc.fx.table;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.BOOLEAN;
import static ph.txtdis.type.Type.FRACTION;
import static ph.txtdis.type.Type.TEXT;

import java.time.LocalDate;
import java.util.List;

import org.apache.commons.lang3.math.Fraction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.control.TableColumn;
import ph.txtdis.app.StockTakeApp;
import ph.txtdis.dto.StockTakeVariance;
import ph.txtdis.fx.table.AbstractTable;
import ph.txtdis.fx.table.Column;
import ph.txtdis.fx.table.FinalQtyAndJustificationStockTakeVarianceContextMenu;
import ph.txtdis.fx.table.StockTakeVarianceTable;
import ph.txtdis.mgdc.app.LaunchableItemApp;
import ph.txtdis.service.StockTakeVarianceService;

@Scope("prototype")
@Component("stockTakeVarianceTable")
public class StockTakeVarianceTableImpl extends AbstractTable<StockTakeVariance> implements StockTakeVarianceTable {

	@Autowired
	private LaunchableItemApp itemApp;

	@Autowired
	private StockTakeApp stockTakeApp;

	@Autowired
	private Column<StockTakeVariance, Boolean> isValid;

	@Autowired
	private Column<StockTakeVariance, Fraction> start, in, out, end, actual, variance, adjusted;

	@Autowired
	private Column<StockTakeVariance, String> item, justification;

	@Autowired
	private StockTakeVarianceService service;

	@Autowired
	private FinalQtyAndJustificationStockTakeVarianceContextMenu contextMenu;

	@Override
	protected List<TableColumn<StockTakeVariance, ?>> addColumns() {
		return asList( //
				item.launches(itemApp).ofType(TEXT).width(180).build("Item", "item"), //
				start.launches(stockTakeApp).addLaunchData(previousCountDate()).ofType(FRACTION).width(90).build(service.getBeginningColumnName(),
						"startQtyInFractions"), //
				in.ofType(FRACTION).width(90).build("Incoming", "inQtyInFractions"), //
				out.ofType(FRACTION).width(90).build("Outgoing", "outQtyInFractions"), //
				end.ofType(FRACTION).width(90).build(service.getExpectedColumnName(), "endQtyInFractions"), //
				actual.launches(stockTakeApp).addLaunchData(latestCountDate()).ofType(FRACTION).width(90).build(service.getActualColumnName(),
						"actualQtyInFractions"), //
				variance.ofType(FRACTION).width(90).build(service.getVarianceColumnName(), "varianceQtyInFractions"), //
				adjusted.ofType(FRACTION).width(90).build("Final", "finalQtyInFractions"), //
				isValid.ofType(BOOLEAN).build("OK", "isValid"), //
				justification.ofType(TEXT).width(360).build("Justification", "justification") //
		);
	}

	private String previousCountDate() {
		LocalDate d = service.getPreviousCountDate();
		return d == null ? "N/A" : d.toString();
	}

	private String latestCountDate() {
		LocalDate d = service.getLatestCountDate();
		return d == null ? "N/A" : d.toString();
	}

	@Override
	protected void addProperties() {
		contextMenu.addMenu(this);
	}
}
