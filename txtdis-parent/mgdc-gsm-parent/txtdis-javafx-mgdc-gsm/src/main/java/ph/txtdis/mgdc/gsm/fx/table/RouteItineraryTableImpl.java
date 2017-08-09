package ph.txtdis.mgdc.gsm.fx.table;

import javafx.scene.control.TableColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.fx.table.AbstractTable;
import ph.txtdis.fx.table.Column;
import ph.txtdis.mgdc.gsm.dto.Customer;
import ph.txtdis.mgdc.gsm.service.StockTakeService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.*;

@Scope("prototype")
@Component("routeItineraryTable")
public class RouteItineraryTableImpl //
	extends AbstractTable<Customer> //
	implements RouteItineraryTable {

	private static final String NULL = "deactivatedBy";

	@Autowired
	private Column<Customer, Boolean> discounted;

	@Autowired
	private Column<Customer, Integer> check, credit;

	@Autowired
	private Column<Customer, Long> id;

	@Autowired
	private Column<Customer, String> name, address, invoice, amount, signature;

	@Autowired
	private StockTakeService stockTakeService;

	@Override
	protected List<TableColumn<Customer, ?>> addColumns() {
		List<TableColumn<Customer, ?>> l = new ArrayList<>(asList( //
			id.ofType(ID).width(30).build("ID", "id"), //
			name.ofType(TEXT).width(240).build("Outlet", "name"), //
			address.ofType(TEXT).width(720).build("Address", "getAddress"), //
			credit.ofType(INTEGER).width(24).verticalHeader().build("Credit", "creditTermInDays"), //
			discounted.ofType(BOOLEAN).width(24).verticalHeader().build("Discount", "discounted"), //
			check.ofType(INTEGER).width(24).verticalHeader().build("Cheque", "gracePeriodInDays") //
		));
		l.addAll(itemOnStockColumns());
		l.addAll(Arrays.asList(//
			invoice.ofType(TEXT).width(90).build("S/I No.", NULL), //
			amount.ofType(TEXT).width(90).build("Amount", NULL), //
			signature.ofType(TEXT).width(90).build("Signature", NULL) //
		));
		return l;
	}

	private List<Column<Customer, String>> itemOnStockColumns() {
		List<String> itemsOnStock = itemsOnStock();
		List<Column<Customer, String>> columns = new ArrayList<>();
		for (int i = 0; i < 30; i++)
			columns.add(new Column<Customer, String>().ofType(TEXT).width(12).verticalHeader()
				.build(itemName(itemsOnStock, i), NULL));
		return columns;
	}

	private List<String> itemsOnStock() {
		try {
			return stockTakeService.listItemsOnStock();
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	private String itemName(List<String> itemsOnStock, int i) {
		return itemsOnStock == null || i >= itemsOnStock.size() ? "" : itemsOnStock.get(i);
	}
}
